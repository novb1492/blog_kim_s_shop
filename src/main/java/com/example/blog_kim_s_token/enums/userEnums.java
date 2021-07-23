package com.example.blog_kim_s_token.enums;

public enum userEnums {
    sucSingUp("회원가입 성공",true),
    alreadyEmail("이미 존재하는 이메일 입니다",false);
    private final String messege;
    private final boolean torf;
   

    userEnums(String messege,boolean torf){
        this.messege=messege;
        this.torf=torf;
    }
    public String getMessege() {
        return messege;
    }
    public Boolean getBool() {
        return torf;
    }
    
}
