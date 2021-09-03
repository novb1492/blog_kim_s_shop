package com.example.blog_kim_s_token.model.payment;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface vbankDao extends JpaRepository<vBankDto,Integer> {
    vBankDto findByPaymentId(String paymentId);
    
    //@Query(value = "select a.*  from vbank  a inner join reservation  b  on a.payment_id=b.payment_id where a.created <=? ",nativeQuery = true)
    //select innerjoin날리는법
    //@Modifying
    //@Transactional
    @Query(value = "select a.*  from vbank  a inner join reservation  b  on a.payment_id=b.payment_id where a.created <=? ",nativeQuery = true)
    List<vBankDto> innerfind(Timestamp today);
  



}
