package com.example.order_service.model.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentStatusEvent {
    private Long orderId;
    private BigDecimal amount;
    private String status; // SUCCESS or FAILED
}
