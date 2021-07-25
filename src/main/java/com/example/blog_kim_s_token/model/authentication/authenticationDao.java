package com.example.blog_kim_s_token.model.authentication;

import org.springframework.data.jpa.repository.JpaRepository;

public interface authenticationDao extends JpaRepository<authenticationDto,Integer> {
    authenticationDto findByPhoneNum(String phoneNum);

    authenticationDto findByEmail(String email);
}
