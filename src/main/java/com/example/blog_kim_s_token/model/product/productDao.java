package com.example.blog_kim_s_token.model.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface productDao extends JpaRepository<productDto,Integer> {
    Optional<productDto> findByProductName(String productName);
}
