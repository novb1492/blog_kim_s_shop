package com.example.blog_kim_s_token.model.payment;

import java.sql.Timestamp;
import java.util.Optional;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface vbankDao extends JpaRepository<vBankDto,Integer> {
    Optional<vBankDto> findByPaymentId(String paymentId);
    
    //select innerjoin날리는법
    //@Query(value = "select a.*  from vbank  a inner join reservation  b  on a.payment_id=b.payment_id where a.created <=? ",nativeQuery = true)
    //delete innerjoin
    //@Query(value = "delete a,b  from vbank  a inner join reservation  b  on a.payment_id=b.payment_id where a.id =76 ",nativeQuery = true)
    
    @Modifying
    @Transactional
    @Query(value = "delete a,b  from vbank  a inner join reservation  b  on a.payment_id=b.payment_id where a.end_date<=? ",nativeQuery = true)
    void innerfind(Timestamp today);
  



}
