package com.example.blog_kim_s_token.model.confrim;

import java.sql.Timestamp;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface confrimDao extends JpaRepository<confrimDto,Integer> {
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

    
    @Modifying
    @Transactional
    @Query(value = "UPDATE confrim c SET emailtempnum=?1,email_requesttime=?2,created=?3 WHERE c.email=?4",nativeQuery = true)
    void updateEmailTempNum(String tempNum,int requestTime,Timestamp timestamp,String email);

    
    
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE confrim c SET emailcheck=?1 WHERE c.email=?2",nativeQuery = true)
    void updateEmailCheckTrue(int one,String email);
}
