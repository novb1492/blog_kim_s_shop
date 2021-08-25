package com.example.blog_kim_s_token.model.product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface productDao extends JpaRepository<productDto,Integer> {
    productDto findByProductName(String productName);
}
