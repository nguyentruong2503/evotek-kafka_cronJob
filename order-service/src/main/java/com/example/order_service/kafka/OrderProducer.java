package com.example.order_service.kafka;

import com.example.order_service.model.event.OrderStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedEvent(OrderStatusEvent event) {
        kafkaTemplate.send("order.created", event);
    }
}
