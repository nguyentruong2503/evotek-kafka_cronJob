package com.example.order_service.model.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderStatusEvent {
    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
}
