package com.example.payment_service.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentCompletedEvent {
    private Long orderId;
    private BigDecimal amount;
    private String status; // SUCCESS or FAILED
}
