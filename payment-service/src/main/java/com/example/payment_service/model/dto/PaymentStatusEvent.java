package com.example.payment_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusEvent {
    private Long orderId;
    private BigDecimal amount;
    private String status; // SUCCESS or FAILED
}
