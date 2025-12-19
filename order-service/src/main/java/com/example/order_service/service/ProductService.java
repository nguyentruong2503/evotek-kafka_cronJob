package com.example.order_service.service;

public interface ProductService {
    void restoreStock(Long productId, int quantity);
}
