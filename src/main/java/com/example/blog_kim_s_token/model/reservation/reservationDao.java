package com.example.blog_kim_s_token.model.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface reservationDao extends JpaRepository<mainReservationDto,Integer> {
    
}
