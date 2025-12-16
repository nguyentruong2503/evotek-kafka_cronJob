package com.example.payment_service.config;

import com.example.payment_service.entity.PaymentEntity;
import com.example.payment_service.model.dto.PaymentCompletedEvent;
import com.example.payment_service.repository.PaymentRepository;
import com.github.kagkarlsson.scheduler.task.Task;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class PaymentJobConfig {

    @Autowired
    private PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentJobConfig(PaymentRepository paymentRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public Task<Void> paymentCleanupTask() {
        return Tasks.recurring("cleanup-pending-payments", FixedDelay.ofMinutes(1)) // Chạy mỗi 1 phút
                .execute((instance, context) -> {
                    System.out.println("db-scheduler: Bắt đầu quét đơn treo...");

                    // Tìm đơn PENDING quá 2 phút
                    LocalDateTime threshold = LocalDateTime.now().minusMinutes(2);
                    List<PaymentEntity> stuckPayments = paymentRepository.findByStatusAndCreatedAtBefore("PENDING", threshold);

                    for (PaymentEntity p : stuckPayments) {
                        // Đổi trạng thái -> CANCELLED
                        p.setStatus("CANCELLED");
                        paymentRepository.save(p);

                        //Gửi event PaymentFailed sang Kafka
                        PaymentCompletedEvent failedEvent = new PaymentCompletedEvent();
                        failedEvent.setOrderId(p.getOrderId());
                        failedEvent.setAmount(p.getAmount());
                        failedEvent.setStatus(p.getStatus());
                        try {
                            kafkaTemplate.send("payment.failed", failedEvent);
                            System.out.println("Đã hủy đơn treo ID: " + p.getOrderId());
                        } catch (Exception e) {
                            System.err.println("Lỗi gửi Kafka: " + e.getMessage());
                        }
                    }
                });
    }
}
