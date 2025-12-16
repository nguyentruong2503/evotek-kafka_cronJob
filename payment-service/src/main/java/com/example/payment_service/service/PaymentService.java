package com.example.payment_service.service;

import com.example.payment_service.model.dto.OrderCreatedEvent;

public interface PaymentService {
    void processPayment(OrderCreatedEvent evt);
}
