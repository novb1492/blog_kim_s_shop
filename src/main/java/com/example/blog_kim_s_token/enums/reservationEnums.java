package com.example.blog_kim_s_token.enums;

public enum reservationEnums {
    sucInsert("예약되었습니다",true);

    private final String messege;
    private final boolean bool;
   

    reservationEnums(String messege,boolean bool){
        this.messege=messege;
        this.bool=bool;
    }
    public String getMessege() {
        return messege;
    }
    public Boolean getBool() {
        return bool;
    }
}
