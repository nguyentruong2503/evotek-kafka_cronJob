package com.example.payment_service.service.impl;

import com.example.payment_service.entity.PaymentEntity;
import com.example.payment_service.model.dto.OrderCreatedEvent;
import com.example.payment_service.model.dto.PaymentStatusEvent;
import com.example.payment_service.repository.PaymentRepository;
import com.example.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private final String PAYMENT_COMPLETED_TOPIC = "payment.completed";
    private final String PAYMENT_FAILED_TOPIC = "payment.failed";

    @Override
    @Transactional
    public void processPayment(OrderCreatedEvent evt) {
        Long orderId = evt.getOrderId();

        // nếu đã có payment SUCCESS thì không xử lý lại
        PaymentEntity existing = paymentRepository.findByOrderId(orderId).orElse(null);
        if (existing != null && "SUCCESS".equalsIgnoreCase(existing.getStatus())) {
            return; // đã xử lý rồi
        }

        // Ban đầu mặc định là Pending
        PaymentEntity p = new PaymentEntity();
        p.setOrderId(orderId);
        p.setAmount(evt.getTotalAmount() != null ? evt.getTotalAmount() : BigDecimal.ZERO);
        p.setStatus("PENDING");
        paymentRepository.save(p);

        //test fail
//        if (true) {
//            System.out.println("DEMO: Giả lập user không thanh toán -> Giữ nguyên trạng thái PENDING");
//            PaymentStatusEvent failedEvent = new PaymentStatusEvent(orderId, p.getAmount(), p.getStatus());
//            kafkaTemplate.send(PAYMENT_FAILED_TOPIC, failedEvent);
//            return;
//        }

        //Ramdom success hoặc failed
        if (Math.random() < 0.5) {
            System.out.println("DEMO: Giả lập user không thanh toán -> Giữ nguyên trạng thái PENDING");
            PaymentStatusEvent failedEvent = new PaymentStatusEvent(orderId, p.getAmount(), p.getStatus());
            kafkaTemplate.send(PAYMENT_FAILED_TOPIC, failedEvent);
            return;
        }

        //Giả lập thanh toán thành công
        p.setStatus("SUCCESS");
        paymentRepository.save(p);

        // Publish payment.completed event
        PaymentStatusEvent completed = new PaymentStatusEvent();
        completed.setOrderId(orderId);
        completed.setAmount(p.getAmount());
        completed.setStatus(p.getStatus());
        try {
            kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, completed);
            System.out.println("gửi đến noti: " + completed);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to publish payment.completed", ex);
        }
    }
}
