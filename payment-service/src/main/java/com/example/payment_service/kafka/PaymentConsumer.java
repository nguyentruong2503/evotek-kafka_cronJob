package com.example.payment_service.kafka;

import com.example.payment_service.model.dto.OrderCreatedEvent;
import com.example.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    @Autowired
    private PaymentService paymentService;

    @KafkaListener(topics = "order.created", groupId = "payment-group")
    public void listen(OrderCreatedEvent event) {
        paymentService.processPayment(event);
        System.out.println("Received order: " + event);
    }

}

