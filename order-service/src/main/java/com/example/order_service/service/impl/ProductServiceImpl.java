package com.example.order_service.service.impl;

import com.example.order_service.entity.ProductEntity;
import com.example.order_service.repository.ProductRepository;
import com.example.order_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void restoreStock(Long productId, int quantity) {
        ProductEntity product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setStockQuantity(product.getStockQuantity() + quantity);
            productRepository.save(product);
        }
    }
}
