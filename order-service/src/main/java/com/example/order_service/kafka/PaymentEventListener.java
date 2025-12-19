package com.example.order_service.kafka;

import com.example.order_service.model.event.PaymentStatusEvent;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderService orderService;

    @KafkaListener(topics = "payment.failed", groupId = "order-group")
    public void handlePaymentFailed(PaymentStatusEvent event) {
        System.out.println("Received payment.failed for Order ID: " + event.getOrderId());

        // Rollback lại số lượng trong kho
        orderService.rollBackQuantity(event.getOrderId());
    }
}
