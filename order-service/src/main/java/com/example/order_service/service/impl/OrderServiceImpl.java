package com.example.order_service.service.impl;

import com.example.order_service.entity.OrderDetailEntity;
import com.example.order_service.entity.OrderEntity;
import com.example.order_service.entity.ProductEntity;
import com.example.order_service.kafka.OrderProducer;
import com.example.order_service.model.event.OrderStatusEvent;
import com.example.order_service.model.OrderDTO;
import com.example.order_service.model.OrderDetailDTO;
import com.example.order_service.repository.OrderDetailRepository;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.repository.ProductRepository;
import com.example.order_service.service.OrderService;
import com.example.order_service.service.ProductService;
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
    private ProductRepository productRepository;

    @Autowired
    private OrderProducer orderProducer;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {
        OrderEntity orderEntity = mapper.map(orderDTO,OrderEntity.class);
        orderEntity.setUserId(orderDTO.getUserId());
        orderEntity.setCreatedAt(LocalDateTime.now());
        orderEntity.setStatus("PENDING");
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        List<OrderDetailEntity> detailEntities = new ArrayList<>();

        for(OrderDetailDTO d : orderDTO.getOrderDetails()){
            //kiểm tra kho và trừ số lượng
            ProductEntity productEntity = productRepository.findById(d.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            if(productEntity.getStockQuantity() < d.getQuantity()){
                throw new RuntimeException("Hết hàng: " + productEntity.getName());
            }

            productEntity.setStockQuantity(productEntity.getStockQuantity() - d.getQuantity());
            productRepository.save(productEntity);

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

        OrderStatusEvent event = new OrderStatusEvent();
        event.setOrderId(orderEntity.getId());
        event.setUserId(orderEntity.getUserId());
        event.setTotalAmount(orderEntity.getTotalPrice());

        orderProducer.sendOrderCreatedEvent(event);

        return orderDTO;
    }

    //roll back lại số lượng sản phẩm của đơn hàng
    @Override
    @Transactional
    public void rollBackQuantity(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("PENDING".equals(order.getStatus())) {
            for (OrderDetailEntity detail : order.getOrderDetails()) {
                productService.restoreStock(detail.getProductId(), detail.getQuantity());
            }
            order.setStatus("CANCELLED");
            orderRepository.save(order);
        }
    }
}
