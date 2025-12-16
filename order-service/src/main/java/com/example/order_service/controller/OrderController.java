package com.example.order_service.controller;

import com.example.order_service.model.BaseResponse;
import com.example.order_service.model.OrderDTO;
import com.example.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO newOrder = orderService.create(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new BaseResponse("ok", "Đặt hàng thành công", newOrder)
        );
    }
}
