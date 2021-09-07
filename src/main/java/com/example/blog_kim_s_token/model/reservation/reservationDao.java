package com.example.blog_kim_s_token.model.reservation;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.example.blog_kim_s_token.model.payment.tryDeleteInter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface reservationDao extends JpaRepository<mainReservationDto,Integer> {
    
    @Query(value = "select  count(*) from reservation where r_date=? AND seat=?",nativeQuery = true)
    int findByRdate(Timestamp timestamp,String seat);

    @Query(value = "select  count(*) from reservation where date_and_time=? AND seat=?",nativeQuery = true)
    int findByTime(Timestamp timestamp,String seat);

    @Query(value = "select  * from reservation where email=? AND seat=?",nativeQuery = true)
    List<mainReservationDto>findByEmailNative(String email,String seat);

    @Query(value = "select a.*,b.price from reservation a inner join product b on a.seat=b.product_name where a.email=? order by a.id desc limit ?,?",nativeQuery = true)
    List<getClientInter> findByEmailJoinOrderByIdDescNative(String email,int nowPage,int totalPage);

    @Query(value = "select a.*,b.price from reservation a inner join product b on a.seat=b.product_name where a.email=? and a.r_date between ? and ? order by a.id desc limit ?,?",nativeQuery = true)
    List<getClientInter>findByEmailJoinOrderByIdBetweenDescNative(String email,Timestamp startDate,Timestamp endDate,int nowPage,int totalPage);

    @Query(value = "select count(*) from reservation where email=? and r_date between ? and ?",nativeQuery = true)
    int countByEmailNative(String email,Timestamp startDate,Timestamp endDate);

    int countByEmail(String email);

    List<mainReservationDto> findByPaymentId(String paymentId);

 

  


    @Query(value = "select a.*,b.price  from reservation  a inner join product  b  on a.seat=b.product_name where a.id=? ",nativeQuery = true)
    Optional<tryDeleteInter> findBySeatJoin(int id);

}
   
