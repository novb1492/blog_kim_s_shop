package com.example.blog_kim_s_token.enums;

public enum userEnums {
    sucSingUp("회원가입 성공",true),
    alreadyEmail("이미 존재하는 이메일 입니다",false),
    alreadyPhone("이미 존재하는 전화번호 입니다",false),
    notEqualsPwd("비밀번호가 일치하지 않습니다",false),
    notEqualsPhone("전화번호가 변조 되었습니다",false),
    notConfrimPhone("전화번호가 인증되지 않았습니다",false),
    sendSmsNum("인증번호가 발송되었습니다",true),
    failSendSmsNum("인증번호 발송에 실패했습니다",false),
    nullRequestNum("빈칸입니다",false),
    notEqualsNum("인증번호가 일치 하지 않습니다",false),
    EqualsNum("인증번호가 일치 합니다",true);

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
