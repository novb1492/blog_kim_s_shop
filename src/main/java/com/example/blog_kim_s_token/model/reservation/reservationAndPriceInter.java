package com.example.blog_kim_s_token.model.reservation;

import java.sql.Timestamp;

public interface reservationAndPriceInter {
    int getId();
    String getEmail();
    Timestamp getDate_and_time();
    String getPayment_id();
    String getStatus();
    int getPrice();
}
