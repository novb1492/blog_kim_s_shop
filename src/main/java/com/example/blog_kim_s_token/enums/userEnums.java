package com.example.blog_kim_s_token.enums;

public enum userEnums {
    sucSingUp("회원가입 성공",true),
    notEqualsPwd("비밀번호가 일치하지 않습니다",false),
    notEqualsPhone("전화번호가 변조 되었습니다",false),
    notConfrimPhone("전화번호가 인증되지 않았습니다",false),
    failSendSmsNum("인증번호 발송에 실패했습니다",false),
    nullRequestNum("빈칸입니다",false),
    sucLogin("로그인 성공",true),
    failLogin("아이디 혹은 비밀번호가 일치 하지 않습니다",false);


    private final String messege;
    private final boolean bool;
   

    userEnums(String messege,boolean bool){
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
