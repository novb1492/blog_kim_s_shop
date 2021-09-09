package com.example.blog_kim_s_token.model.payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface paidDao extends JpaRepository<paidDto,Integer> {
    Optional<paidDto> findByPaymentId(String paymentId);
}
