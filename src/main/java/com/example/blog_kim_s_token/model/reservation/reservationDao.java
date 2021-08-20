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
    List<mainReservationDto>findByEmail(String email,String seat);
}
   
