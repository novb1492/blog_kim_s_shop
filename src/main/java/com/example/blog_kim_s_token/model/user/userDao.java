package com.example.blog_kim_s_token.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface userDao  extends JpaRepository<userDto,Integer>{
    userDto findByEmail(String email);

    userDto findByPhoneNum(String phoneNum);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE user u SET u.pwd =?1 WHERE u.email=?2",nativeQuery = true)
    void updatePwd(String pwd,String email);
}
