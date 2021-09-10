package com.example.blog_kim_s_token.service.ApiServies.kakao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface kakaoTokenDao extends JpaRepository<insertKakaoTokenDto,Integer> {
    
    Optional<insertKakaoTokenDto>findByEmail(String email);
}
