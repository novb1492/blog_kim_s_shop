package com.example.blog_kim_s_token.model.csrf;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface csrfDao extends JpaRepository<csrfDto,Integer> {
    csrfDto findByUserId(int userID);

    csrfDto findByEmail(String email);

    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "UPDATE csrftoken c SET c.csrf_token =?1 WHERE c.user_id=?2",nativeQuery = true)
    void updateCsrfToken(String csrfToken,int userId);

}
