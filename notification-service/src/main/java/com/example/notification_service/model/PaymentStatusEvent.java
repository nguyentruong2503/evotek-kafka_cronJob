package com.example.notification_service.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentStatusEvent {
    private Long orderId;
    private BigDecimal amount;
    private String status; // SUCCESS or FAILED
}
