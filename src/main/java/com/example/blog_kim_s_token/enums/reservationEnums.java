package com.example.blog_kim_s_token.enums;

public enum reservationEnums {
    sucInsert("예약되었습니다",true),
    findAlready("",false);

    private  String messege;
    private  boolean bool;
   

    reservationEnums(String messege,boolean bool){
        this.messege=messege;
        this.bool=bool;
    }
    public void setMessete(String messege) {
        this.messege=messege;
    }
    public String getMessege() {
        return messege;
    }
    public Boolean getBool() {
        return bool;
    }
}
