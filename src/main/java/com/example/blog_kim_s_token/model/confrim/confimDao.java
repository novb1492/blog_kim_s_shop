package com.example.blog_kim_s_token.model.confrim;

import java.sql.Timestamp;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface confimDao extends JpaRepository<confrimDto,Integer> {
    confrimDto findByPhoneNum(String phoneNum);

    confrimDto findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE confrim c SET phone_temp_num=?1,requesttime=?2,created=?3 WHERE c.phone_num=?4",nativeQuery = true)
    void updatePhoneTempNum(String tempNum,int requestTime,Timestamp timestamp,String phoneNum);

    @Modifying
    @Transactional
    @Query(value = "UPDATE confrim c SET c.phonecheck=?1 WHERE c.phone_num=?2",nativeQuery = true)
    void updatePhoneCheckTrue(int one,String phoneNum);

    
    @Modifying
    @Transactional
    @Query(value = "delete from confrim c WHERE c.phone_num=?1",nativeQuery = true)
    void deleteByPhoneNum(String phoneNum);
}
