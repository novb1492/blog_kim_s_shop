package com.example.blog_kim_s_token.service.ApiServies.kakao;






import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.example.blog_kim_s_token.customException.failKakaoPay;
import com.example.blog_kim_s_token.enums.aboutPayEnums;
import com.example.blog_kim_s_token.jwt.jwtService;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.userService;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.payment.paymentService;
import com.example.blog_kim_s_token.service.payment.iamPort.nomalPayment;
import com.example.blog_kim_s_token.service.reservation.resevationService;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class kakaopayService {
    private final String adminKey="ac5d7bd93834444767d1b59477e6f92f";
    private final String cid="TC0ONETIME";
    private final String approveUrl="https://kapi.kakao.com/v1/payment/approve";
    private final String realCancleUrl="https://kapi.kakao.com/v1/payment/cancel";
    private final String status="paid";
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private MultiValueMap<String,Object> body=new LinkedMultiValueMap<>();

 
    @Autowired
    private paymentService paymentService;
    @Autowired
    private resevationService resevationService;

    @Transactional(rollbackFor = Exception.class)
    public JSONObject insertPaymentForkakao(String pgToken,HttpSession httpSession) {
        System.out.println("insertPaymentForkakao");
        String[][]itemArray=(String[][])httpSession.getAttribute("itemArray");
        String[]other=(String[])httpSession.getAttribute("other");
        String email=(String)httpSession.getAttribute("email");
        String name=(String)httpSession.getAttribute("name");
        int totalPrice=(int)httpSession.getAttribute("totalPrice");
        String kind=(String)httpSession.getAttribute("kind");
        String paymentid=(String)httpSession.getAttribute("tid");
        List<Integer>timesOrSize=(List<Integer>)httpSession.getAttribute("timesOrSize");
        try {
            body.add("cid", cid);
            body.add("tid",paymentid);
            body.add("partner_order_id",httpSession.getAttribute("partner_order_id"));
            body.add("partner_user_id", httpSession.getAttribute("email"));
            body.add("quantity",httpSession.getAttribute("count"));
            body.add("pg_token", pgToken);
            JSONObject response=requestToKakaoPay(approveUrl);
            System.out.println(response+" 카카오페이 결제완료");
            String usedKind=aboutPayEnums.kakaoPay.getString();
            nomalPayment nomalPayment=new nomalPayment();
            nomalPayment.setKind(kind);
            nomalPayment.setEmail(email);
            nomalPayment.setPayMethod(usedKind);
            nomalPayment.setPaymentid(paymentid);
            nomalPayment.setStatus(status);
            nomalPayment.setUsedKind(usedKind);
            nomalPayment.setName(name);
            paymentService.insertPayment(nomalPayment,totalPrice);
            if(kind.equals("reservation")){
                System.out.println("예약 상품 결제");
                resevationService.doReservation(email,name, paymentid, itemArray, other,timesOrSize,status,usedKind);
      
            }else if(kind.equals("product")){
                System.out.println("상품결제");
            }
  
           return utillService.makeJson(true, "완료 되었습니다");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insertPaymentForkakao error"+e.getMessage());
            throw new failKakaoPay(e.getMessage(),cid,paymentid,totalPrice);
        }
    }  
    private JSONObject requestToKakaoPay(String url) {
        System.out.println("getPayLink");
        try {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization","KakaoAK "+adminKey);
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            return restTemplate.postForObject(url,entity,JSONObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getPayLink error "+ e.getMessage());
            throw new RuntimeException("카카오 통신 실패");
        }finally{
            headers.clear();
            body.clear();
        }
    }
    public void  cancleKakaopay(MultiValueMap<String,Object> body2) {
        System.out.println("cancleKakaopay");
        body=body2;
        requestToKakaoPay(realCancleUrl);
    }
}
