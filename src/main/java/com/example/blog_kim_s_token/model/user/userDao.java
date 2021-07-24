package com.example.blog_kim_s_token.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface userDao  extends JpaRepository<userDto,Integer>{
    userDto findByEmail(String email);

    userDto findByPhoneNum(String phoneNum);
}
