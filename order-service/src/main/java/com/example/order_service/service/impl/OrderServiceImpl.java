package com.example.order_service.service.impl;

import com.example.order_service.entity.OrderDetailEntity;
import com.example.order_service.entity.OrderEntity;
import com.example.order_service.kafka.OrderProducer;
import com.example.order_service.model.OrderCreatedEvent;
import com.example.order_service.model.OrderDTO;
import com.example.order_service.model.OrderDetailDTO;
import com.example.order_service.repository.OrderDetailRepository;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProducer orderProducer;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {
        OrderEntity orderEntity = mapper.map(orderDTO,OrderEntity.class);
        orderEntity.setUserId(orderDTO.getUserId());
        orderEntity.setCreatedAt(LocalDateTime.now());
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        List<OrderDetailEntity> detailEntities = new ArrayList<>();

        for(OrderDetailDTO d : orderDTO.getOrderDetails()){
            BigDecimal lineTotal = d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity()));

            OrderDetailEntity detail = new OrderDetailEntity();
            detail.setOrder(orderEntity);
            detail.setProductId(d.getProductId());
            detail.setProductName(d.getProductName());
            detail.setQuantity(d.getQuantity());
            detail.setUnitPrice(d.getUnitPrice());
            detail.setLineTotal(lineTotal);

            /*
            BigDecimal là immutable, nên khi cộng trừ sẽ trả về
            1 đối tượng mới
            */
            totalAmount = totalAmount.add(lineTotal);
            detailEntities.add(detail);
        }
        orderEntity.setTotalPrice(totalAmount);

        orderEntity = orderRepository.save(orderEntity);

        for (OrderDetailEntity detail : detailEntities) {
            detail.setOrder(orderEntity);
        }

        orderDetailRepository.saveAll(detailEntities);

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(orderEntity.getId());
        event.setUserId(orderEntity.getUserId());
        event.setTotalAmount(orderEntity.getTotalPrice());

        orderProducer.sendOrderCreatedEvent(event);

        return orderDTO;
    }
}
