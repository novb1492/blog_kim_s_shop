package com.example.blog_kim_s_token.model.reservation;

import java.sql.Timestamp;

public interface getClientInter {
    int getId();
    String getSeat();
    Timestamp getCreated();
    Timestamp getDate_and_time();
    String getStatus();
    String getPayment_id();
    String getUsed_pay_kind();
    String getBank();
    String getBank_num();
    Timestamp getEnd_date();
    int getVbank_total_price(); 
    int getPrice(); 
}
