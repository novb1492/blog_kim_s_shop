package com.example.blog_kim_s_token.model.payment;

import java.sql.Timestamp;

public interface tryDeleteInter {
    int getId();
    String getPayment_id();
    int getPrice();
    Timestamp getDate_and_time();
    String getStatus();
    String getEmail();
    String getSeat();
}
