package com.example.blog_kim_s_token.model.csrf;

import org.springframework.data.jpa.repository.JpaRepository;


public interface csrfDao extends JpaRepository<csrfDto,Integer> {
    csrfDto findByUserId(int userID);

    csrfDto findByEmail(String email);

}
