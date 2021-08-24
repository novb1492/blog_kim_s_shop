package com.example.blog_kim_s_token.enums;

import java.sql.Timestamp;

public enum paymentEnums {
    sucCheck(null,""),
    failCheck(null,"");

    private  Timestamp period;
    private  String bool;
   

    paymentEnums(Timestamp period,String bool){
        this.period=period;
        this.bool=bool;
    }
    public void setperiod(Timestamp period) {
        this.period=period;
    }
    public Timestamp getperiod() {
        return period;
    }
    public String getBool() {
        return bool;
    }
}
