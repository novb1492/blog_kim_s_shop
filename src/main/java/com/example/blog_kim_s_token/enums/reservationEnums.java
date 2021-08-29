package com.example.blog_kim_s_token.enums;

public enum reservationEnums {
    sucInsert("",true),
    findAlready("",false),
    fail("",false),
    can("",true),
    yes("",true),
    no("",false);
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
