package com.example.blog_kim_s_token.service.payment.bootPay;

import java.util.LinkedHashMap;


import com.example.blog_kim_s_token.model.payment.bootPay.bootTokenDto;
import com.example.blog_kim_s_token.model.payment.bootPay.bootpayInforDto;
import com.example.blog_kim_s_token.service.payment.payMentInterFace;
import com.example.blog_kim_s_token.service.payment.paymentService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class bootPayService {
    private final String bootPayId="611fc06f7b5ba4001f52a3e7";
    private final String PrivateKey="hng9s7nhvvFbj4EhzN8EQWVuYaXOWZoRmPkFNj+qfUA=";
    private final String getInforUrl="https://api.bootpay.co.kr/receipt/";
    private final String cancleUrl="https://api.bootpay.co.kr/cancel.json";
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private JSONObject body=new JSONObject();

    @Autowired
    private paymentService paymentService;

    public void confrimPayment(payMentInterFace payMentInterFace) {
       System.out.println("confrimPayment");
        ConfrimBuy(payMentInterFace,getBuyInfor(payMentInterFace));
    }
    public String getToken() {
        System.out.println("getToken");
        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            body.put("application_id",bootPayId);
            body.put("private_key",PrivateKey);
            HttpEntity<JSONObject>entity=new HttpEntity<JSONObject>(body, headers);
            bootTokenDto bootTokenDto=restTemplate.postForObject("https://api.bootpay.co.kr/request/token", entity,bootTokenDto.class);
            System.out.println(bootTokenDto+" 부트페이 토큰");
            return (String) bootTokenDto.getData().get("token");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("confrimPayment error");
            throw new RuntimeException("부트페이 토큰발급 실패");
        }finally{
            headers.clear();
            body.clear();
        }
    }
    public JSONObject getBuyInfor(payMentInterFace payMentInterFace) {
        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", getToken());
           
            HttpEntity<JSONObject>entity=new HttpEntity<JSONObject>(headers);
            ResponseEntity<bootpayInforDto> reseponse=restTemplate.exchange(getInforUrl+payMentInterFace.getPaymentId(),HttpMethod.GET,entity,bootpayInforDto.class);
            bootpayInforDto bootpayInforDto=reseponse.getBody();
            
            System.out.println(bootpayInforDto+" buyerInfor");
            return bootpayInforDto.getData();
        } catch (Exception e) {
           e.printStackTrace();
           System.out.println("ConfrimBuy error");
           throw new RuntimeException("부트페이 결제정보 가져오기 실패");
        } 
    }
    public void ConfrimBuy(payMentInterFace payMentInterFace,JSONObject data) {
        System.out.println("ConfrimBuy "+payMentInterFace.getPaymentId());
        try {
            if((int)data.get("price")==payMentInterFace.getTotalPrice()){
                System.out.println("부트페이 검증성공");
                LinkedHashMap<String,Object>paymentData=(LinkedHashMap<String, Object>) data.get("payment_data");
                System.out.println(paymentData.get("bankname")+" 은행이름");
                payMentInterFace.setUsedKind((String)paymentData.get("bankname"));
                paymentService.insertVbankPayment(payMentInterFace,(String)paymentData.get("expiredate"));
                return;
            }
            throw new Exception();
        } catch (Exception e) {
           e.printStackTrace();
           System.out.println("ConfrimBuy error");
           throw new RuntimeException("부트페이 결제정보 가져오기 실패");
        }
    }
    public void cancleBuy(String paymentId,int zeorOrPrice,String cancleName,String reason) {
        System.out.println("cancleBuy");
        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", getToken());
            body.put("receipt_id", paymentId);
            body.put("name", cancleName);
            body.put("reason", reason);
            if(zeorOrPrice>0){
                body.put("price", zeorOrPrice);
            }
            HttpEntity<JSONObject>entity=new HttpEntity<JSONObject>(body,headers);
            restTemplate.postForObject(cancleUrl, entity,JSONObject.class);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cancleBuy error");
            throw new RuntimeException("cancleBuy 부트페이 환불 실패");
        }finally{
            headers.clear();
            body.clear();
        }
    }
}
