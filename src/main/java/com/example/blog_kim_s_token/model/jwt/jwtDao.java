package com.example.blog_kim_s_token.model.jwt;

import org.springframework.data.jpa.repository.JpaRepository;


public interface jwtDao extends JpaRepository<jwtDto,Integer> {
    jwtDto findByTokenName(String name);

    jwtDto findByUserid(int userid);
    
}
