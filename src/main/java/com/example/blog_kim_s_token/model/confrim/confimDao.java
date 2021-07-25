package com.example.blog_kim_s_token.model.confrim;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface confimDao extends JpaRepository<confrimDto,Integer> {
    confrimDto findByPhoneNum(String phoneNum);

    confrimDto findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE confrim c SET phone_temp_num=?1,requesttime=?2 WHERE c.phone_num=?3",nativeQuery = true)
    void updatePhoneTempNum(String tempNum,int requestTime,String phoneNum);
}
