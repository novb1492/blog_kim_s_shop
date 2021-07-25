package com.example.blog_kim_s_token.model.confrim;

import org.springframework.data.jpa.repository.JpaRepository;

public interface confimDao extends JpaRepository<confrimDto,Integer> {
    confrimDto findByPhoneNum(String phoneNum);

    confrimDto findByEmail(String email);
}
