package com.example.blog_kim_s_token.enums;


public enum confirmEnums {

    alreadyPhone("이미 존재하는 전화번호 입니다",false),
    alreadyEmail("이미 존재하는 이메일 입니다",false),
    sendSmsNum("인증번호가 발송되었습니다",true),
    timeOut("인증시간이 만료되었습니다",false),
    tooManyTime("하루 10회 제한입니다",false),
    nullPhoneNumInDb("인증번호 요청기록이 존재하지 않습니다",false),
    notEqulsPhoneNum("핸드폰번호가 변조 되었습니다",false),
    notEqulsTempNum("인증번호가 일치 하지 않습니다",false),
    overTime("인증시간이 초과되었습니다",false),
    EqulsTempNum("인증 되었습니다",true);

    private final String messege;
    private final boolean bool;
   

    confirmEnums(String messege,boolean bool){
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
