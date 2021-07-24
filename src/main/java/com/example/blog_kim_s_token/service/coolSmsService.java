package com.example.blog_kim_s_token.service;

import java.util.HashMap;

import com.example.blog_kim_s_token.enums.userEnums;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;

@Service
public class coolSmsService {
    private final String apikey="NCSFT0AZ2O3FHMAX";
    private final String apiSecret="AHZNZ3IIMGSYIXFLR7HQDBYA5KPFSFCS";

    @Autowired
    private utillService utillService;

    public JSONObject sendMessege(String phoneNum,String messege) {
       System.out.println(phoneNum+" 문자전송번호");
        Message coolsms = new Message(apikey, apiSecret);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", phoneNum);
        params.put("from", "01091443409");
        params.put("type", "SMS");
        params.put("text", messege);
        try {
            coolsms.send(params);
            System.out.println("문자 전송 완료");
            return utillService.makeJson(userEnums.sendSmsNum.getBool(), userEnums.sendSmsNum.getMessege());
        } catch (CoolsmsException e) {
            e.printStackTrace();
            System.out.println("sendMessege 전송 실패");
        }
       return utillService.makeJson(userEnums.failSendSmsNum.getBool(), userEnums.failSendSmsNum.getMessege());
    }
}
