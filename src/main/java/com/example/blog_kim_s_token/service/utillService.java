package com.example.blog_kim_s_token.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.stereotype.Service;


@Service
public class utillService {

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
    public JSONObject makeJson(boolean result,String messege) {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("result",result);
        jsonObject.put("messege", messege);
        return jsonObject;
    } 

}
