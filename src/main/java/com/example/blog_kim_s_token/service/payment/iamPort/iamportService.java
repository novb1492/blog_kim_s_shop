package com.example.blog_kim_s_token.service.payment.iamPort;





import com.example.blog_kim_s_token.customException.failBuyException;
import com.example.blog_kim_s_token.model.iamport.buyInforDto;
import com.example.blog_kim_s_token.model.iamport.impTokenDto;
import com.example.blog_kim_s_token.service.payment.payMentInterFace;
import com.example.blog_kim_s_token.service.payment.paymentService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private paymentService paymentService;


    public void confrimPayment(payMentInterFace payMentInterFace) {
        System.out.println("confrimPayment");
        confrimBuy(getBuyInfor(payMentInterFace.getPaymentId()),payMentInterFace);
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
    private JSONObject getBuyInfor(String impId){
        System.out.println("getBuyInfor");
        String token=getToken();
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
    private void confrimBuy(JSONObject buyInfor,payMentInterFace payInter) {
        System.out.println("confrimBuy");
        int amount=(int) buyInfor.get("amount");
        String status=(String) buyInfor.get("status");
        System.out.println(amount+"결제총량"+payInter.getTotalPrice()+" 결제되어야 하는 금액"+status+" 결제상태");
        if(payInter.getTotalPrice()==amount&&status.equals("paid")){
            System.out.println("결제 검증완료");
            payInter.setUsedKind((String)buyInfor.get("pay_method"));
            paymentService.insertPayment(payInter);
            return;
        }
        System.out.println("결제 검증실패");
        throw new failBuyException("결제 검증실패",payInter.getPaymentId());
    }    
    
    public boolean cancleBuy(String impId,int zeorOrPrice) {
        System.out.println("cancleBuy");
        try {
            String token=getToken();
            headers.add("Authorization",token);
            body.put("imp_uid", impId);
            if(zeorOrPrice!=0){
                body.put("amount", zeorOrPrice);
            }
            HttpEntity<JSONObject>entity=new HttpEntity<JSONObject>(body, headers);
            restTemplate.postForObject("https://api.iamport.kr/payments/cancel",entity,JSONObject.class);
            System.out.println("아임포트 환불 성공");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cancleBuy가 실패 했습니다 직접 환불 바랍니다");
            throw new RuntimeException("환불에 실패 했습니다 다시시도 바랍니다");
        }finally{
            headers.clear();
            body.clear();

        }

    }

    
}
