package com.example.order_service.service;

import com.example.order_service.model.OrderDTO;
import org.hibernate.query.Order;

public interface OrderService {
    OrderDTO create(OrderDTO orderDTO);

    void rollBackQuantity(Long orderId);

}
