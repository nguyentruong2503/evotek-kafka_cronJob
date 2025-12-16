package com.example.order_service.model;

import lombok.Data;

import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class OrderDetailDTO {
    private Long id;

    @NotNull(message = "Product ID là bắt buộc")
    private Long productId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String productName;

    @NotNull(message = "Số lượng là bắt buộc")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    private Integer quantity;

    @NotNull(message = "Đơn giá là bắt buộc")
    @Min(value = 0, message = "Đơn giá không được âm")
    private BigDecimal unitPrice;

}
