package com.example.payment_service.repository;

import com.example.payment_service.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByOrderId(Long orderId);
    List<PaymentEntity> findByStatusAndCreatedAtBefore(String status, LocalDateTime createAt);
}
