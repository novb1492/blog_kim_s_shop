package com.example.blog_kim_s_token.service;



import java.util.List;

import com.example.blog_kim_s_token.model.iamport.buyInforDto;
import com.example.blog_kim_s_token.model.iamport.impTokenDto;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class iamportService {
    private final String imp_key="7336595505277037";
    private final String imp_secret="19412b4bca453060662162083d1ccc8ee7c53bd98a2f33faedd7ebc3e6ad4c359c36f899ebd6ddec";
    private final String impGetTokenUrl="https://api.iamport.kr/users/getToken";
    private final String impGetInforUrl="https://api.iamport.kr/payments/";
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private JSONObject body=new JSONObject();

    public boolean confrimPayment(String impId,List<Integer>times,String seat,int totalPrice) {
        System.out.println("confrimPayment");
        String token=getToken();
        JSONObject buyInfor=getBuyInfor(token, impId);
        return confrimBuy(buyInfor, impId, times, seat,totalPrice);
    }
    private String getToken() {
        System.out.println("getToken");
        headers.setContentType(MediaType.APPLICATION_JSON);
        body.put("imp_key", imp_key);
        body.put("imp_secret", imp_secret);
        try {
            HttpEntity<JSONObject>entity=new HttpEntity<JSONObject>(body, headers);
            impTokenDto impTokenDto=restTemplate.postForObject(impGetTokenUrl,entity, impTokenDto.class);
            System.out.println(impTokenDto.toString()+"결제토큰");
            return impTokenDto.getResponse().getAsString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getToken error");
            throw new RuntimeException("아임포트 토큰 발급에 실패했습니다");
        }finally{
            headers.clear();
            body.clear();
        }
    }
    private JSONObject getBuyInfor(String token,String impId){
        System.out.println("getBuyInfor");
        headers.add("Authorization", token);
        HttpEntity<JSONObject>entity=new HttpEntity<JSONObject>(headers);
        try {
            buyInforDto buyInforDto=restTemplate.postForObject(impGetInforUrl+impId, entity,buyInforDto.class);
            System.out.println(buyInforDto.toString()+" 결제정보");
            return buyInforDto.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getBuyInfor error");
            throw new RuntimeException("getBuyInfor 결제 정보 불러오기 실패");
        }finally{
            headers.clear();
            body.clear();
        }
    }
    private boolean confrimBuy(JSONObject buyInfor,String impId,List<Integer>times,String seat,int totalPrice) {
        System.out.println("confrimBuy");
        int amount=(int) buyInfor.get("amount");
        System.out.println(amount+"결제총량"+totalPrice+" 결제되어야 하는 금액");
        if(totalPrice==amount){
            System.out.println("결제 검증완료");
            return true;
        }
        System.out.println("결제 검증실패");
        return false;
    }
    public void cancleBuy(String impId) {
        try {
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cancleBuy error");
            throw new RuntimeException("환불에 실패했습니다");
        }
    }

    
}
