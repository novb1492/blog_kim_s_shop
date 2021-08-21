package com.example.blog_kim_s_token.enums;

public enum paymentEnums {
    sucCheck("",true),
    failCheck("",false);

    private  String status;
    private  boolean bool;
   

    paymentEnums(String status,boolean bool){
        this.status=status;
        this.bool=bool;
    }
    public void setStatus(String status) {
        this.status=status;
    }
    public String getStatus() {
        return status;
    }
    public Boolean getBool() {
        return bool;
    }
}
