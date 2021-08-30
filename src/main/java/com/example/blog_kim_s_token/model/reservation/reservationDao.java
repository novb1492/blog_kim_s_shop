package com.example.blog_kim_s_token.model.reservation;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface reservationDao extends JpaRepository<mainReservationDto,Integer> {
    
    @Query(value = "select  count(*) from reservation where r_date=? AND seat=?",nativeQuery = true)
    int findByRdate(Timestamp timestamp,String seat);

    @Query(value = "select  count(*) from reservation where date_and_time=? AND seat=?",nativeQuery = true)
    int findByTime(Timestamp timestamp,String seat);

    @Query(value = "select  * from reservation where email=? AND seat=?",nativeQuery = true)
    List<mainReservationDto>findByEmailNative(String email,String seat);

    @Query(value = "select *from reservation where email=? order by id desc limit ?,?",nativeQuery = true)
    List<mainReservationDto>findByEmailOrderByIdDescNative(String email,int nowPage,int totalPage);

    @Query(value = "select *from reservation where email=? and r_date between ? and ? order by id desc limit ?,?",nativeQuery = true)
    List<mainReservationDto>findByEmailOrderByIdBetweenDescNative(String email,Timestamp startDate,Timestamp endDate,int nowPage,int totalPage);

    @Query(value = "select count(*) from reservation where email=? and r_date between ? and ?",nativeQuery = true)
    int countByEmailNative(String email,Timestamp startDate,Timestamp endDate);

    int countByEmail(String email);
}
   
