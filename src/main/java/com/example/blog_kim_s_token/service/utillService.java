package com.example.blog_kim_s_token.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.example.blog_kim_s_token.enums.userEnums;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class utillService {
    
    @Value("${jwt.refreshToken.validity}")
    private int cofrimTime;

    public boolean checkDate(Timestamp timestamp,int refreshTokenValidity) {
        System.out.println(timestamp+"토큰 기간");
        System.out.println("날짜 비교 시작");
        LocalDateTime timestamp2=timestamp.toLocalDateTime();
        timestamp2=timestamp2.plusDays(refreshTokenValidity);
        LocalDateTime today= LocalDateTime.now(); 
        if(timestamp2.isBefore(today)){
            System.out.println("날짜가 지났습니다");
           return true;
        }
        return false;
    }
    public boolean checkDate(Timestamp timestamp) {
        System.out.println(timestamp+"인증시간");
        System.out.println("날짜 비교 시작");
        LocalDateTime timestamp2=timestamp.toLocalDateTime();
        timestamp2=timestamp2.plusSeconds(1);
        LocalDateTime today= LocalDateTime.now(); 
        if(timestamp2.isBefore(today)){
            System.out.println("시간이 지났습니다");
           return true;
        }
        return false;
    }
    public JSONObject makeJson(boolean bool,String messege) {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("bool",bool);
        jsonObject.put("messege", messege);
        return jsonObject;
    }
    public String GetRandomNum(int end) {
        String num="";
        Random random=new Random();
        for(int i=0;i<end;i++){
            num+=Integer.toString(random.nextInt(10));
        }
        return num;
    } 
    public JSONObject cofrimSmsNum(HttpServletRequest request) {
        HttpSession httpSession=request.getSession();
        System.out.println("cofrimSmsNum 제출 "+httpSession.getAttribute("insertPhone")+"세션"+httpSession.getAttribute("insertRandNum"));
       /* if(requestNum!=null){
            if(requestNum.equals(sessionNum)){
                httpSession.setAttribute("phoneCheck", true);
                return makeJson(userEnums.EqualsNum.getBool(), userEnums.EqualsNum.getMessege());
            }
            return makeJson(userEnums.notEqualsNum.getBool(), userEnums.notEqualsNum.getMessege());
        }*/
        return makeJson(userEnums.nullRequestNum.getBool(), userEnums.nullRequestNum.getMessege());
    }

}
