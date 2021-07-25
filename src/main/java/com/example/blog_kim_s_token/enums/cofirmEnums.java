package com.example.blog_kim_s_token.enums;

public enum cofirmEnums {
   
    timeOut("인증시간이 만료되었습니다",false),
    tooManyTime("하루 10회 제한입니다",false);

    private final String messege;
    private final boolean bool;
   

    cofirmEnums(String messege,boolean bool){
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
