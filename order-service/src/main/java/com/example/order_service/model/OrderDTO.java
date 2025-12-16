package com.example.order_service.model;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Data
public class OrderDTO {
    private Long id;

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotEmpty(message = "Danh sách đơn hàng không được để trống")
    @Valid
    private List<OrderDetailDTO> orderDetails;
}
