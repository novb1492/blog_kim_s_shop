package com.example.blog_kim_s_token.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;



import com.nimbusds.jose.shaded.json.JSONObject;




public class utillService {
    
    public static boolean checkDate(Timestamp timestamp,int refreshTokenValidity) {
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
    public static boolean compareDate(Timestamp timestamp,LocalDateTime localDateTime) {
        System.out.println("checkDate");
        System.out.println("날짜 비교 시작");
        LocalDateTime timestamp2=timestamp.toLocalDateTime(); 
        if(timestamp2.getDayOfMonth()==localDateTime.getDayOfMonth()){
            System.out.println("당일입니다");
            return false;
        }
        if(timestamp2.isBefore(localDateTime)){
            System.out.println("날짜가 지났습니다");
           return true;
        }
        return false;
    }
    public static boolean checkTime(Timestamp timestamp,int totalTokenTime) {
        System.out.println(timestamp+"인증시간");
        System.out.println("날짜 비교 시작");
        LocalDateTime timestamp2=timestamp.toLocalDateTime();
        timestamp2=timestamp2.plusMinutes(totalTokenTime);
        LocalDateTime today= LocalDateTime.now(); 
        if(timestamp2.isBefore(today)){
            System.out.println("시간이 지났습니다");
           return true;
        }
        return false;
    }
    public static boolean checkDate(Timestamp timestamp) {
        System.out.println(timestamp+"인증시간");
        System.out.println("날짜 비교 시작");
        LocalDateTime timestamp2=timestamp.toLocalDateTime();
        timestamp2=timestamp2.plusSeconds(10);
        LocalDateTime today= LocalDateTime.now(); 
        if(timestamp2.isBefore(today)){
            System.out.println("시간이 지났습니다");
           return true;
        }
        return false;
    }
    public static Timestamp getNowTimestamp() {
        System.out.println("getNowTimestamp 현재 시간 가져오기");
        return new Timestamp(System.currentTimeMillis());
    }
    public static JSONObject makeJson(boolean bool,String messege) {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("bool",bool);
        jsonObject.put("messege", messege);
        return jsonObject;
    }
    public static JSONObject makeJson(boolean bool,int messege) {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("bool",bool);
        jsonObject.put("messege", messege);
        return jsonObject;
    }
    public static JSONObject makeJson(boolean bool,String messege,List<String>list) {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("bool",bool);
        jsonObject.put("messege", messege);
        jsonObject.put("errorPart",list);
        return jsonObject;
    }
    public static String GetRandomNum(int end) {
        String num="";
        Random random=new Random();
        for(int i=0;i<end;i++){
            num+=Integer.toString(random.nextInt(10));
        }
        return num;
    } 


}
