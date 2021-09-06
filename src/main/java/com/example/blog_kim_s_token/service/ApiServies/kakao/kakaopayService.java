package com.example.blog_kim_s_token.service.ApiServies.kakao;





import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.example.blog_kim_s_token.model.product.productDao;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.userService;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.payment.paymentService;
import com.example.blog_kim_s_token.service.payment.iamPort.nomalPayment;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class kakaopayService {
    private final String adminKey="ac5d7bd93834444767d1b59477e6f92f";
    private final String cid="TC0ONETIME";
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private MultiValueMap<String,Object> body=new LinkedMultiValueMap<>();

    @Autowired
    private productDao productDao;
    @Autowired
    private paymentService paymentService;
    @Autowired
    private userService userService;
    
    public JSONObject doKakaoPay(JSONObject jsonObject,HttpServletRequest request) {

        return getPayLink(jsonObject, request);
    }
    private JSONObject getPayLink(JSONObject jsonObject,HttpServletRequest request) {
        System.out.println("getPayLink");
        try {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization","KakaoAK "+adminKey);
            body.add("cid", cid);
            body.add("partner_order_id",1234+"");
            body.add("partner_user_id", "kim@kim.com");
            body.add("item_name", "test상품");
            body.add("quantity", 1+"");
            body.add("total_amount", 1000+"");
            body.add("tax_free_amount", 0+"");
            body.add("approval_url", "http://localhost:8080/api/okKakaopay");
            body.add("cancel_url", "http://localhost:8080/auth/cancleKakaopay");
            body.add("fail_url", "http://localhost:8080/auth/failKakaopay");
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            JSONObject response=restTemplate.postForObject("https://kapi.kakao.com/v1/payment/ready", entity,JSONObject.class);
            System.out.println(response+" 카카오페이 통신요청 결과");
            HttpSession httpSession=request.getSession();
            userDto userDto=userService.sendUserDto();
            httpSession.setAttribute("tid", response.get("tid"));
            httpSession.setAttribute("seat", jsonObject.get("seat"));
            httpSession.setAttribute("month", jsonObject.get("month"));
            httpSession.setAttribute("date", jsonObject.get("date"));
            httpSession.setAttribute("times", jsonObject.get("times"));
            httpSession.setAttribute("year", jsonObject.get("year"));
            httpSession.setAttribute("totalPrice", jsonObject.get("totalPrice"));
            httpSession.setAttribute("kind", "reservation");
            httpSession.setAttribute("email", userDto.getEmail());
            httpSession.setAttribute("name", userDto.getName());
            return utillService.makeJson(true,  (String)response.get("next_redirect_pc_url"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getPayLink error "+ e.getMessage());
            throw new RuntimeException("카카오 통신 실패");
        }finally{
            headers.clear();
            body.clear();
        }
    }
    public void insertPaymentForkakao(String pgToken,HttpSession httpSession) {
        System.out.println("insertPaymentForkakao");
        JSONObject response=getPaymentResult(pgToken, httpSession);
        nomalPayment nomalPayment=new nomalPayment();
        nomalPayment.setKind((String)httpSession.getAttribute("kind"));
        nomalPayment.setEmail((String)httpSession.getAttribute("email"));
        nomalPayment.setPayMethod("kakaoPay");
        nomalPayment.setPaymentid((String)response.get("tid"));
        nomalPayment.setStatus("paid");
        nomalPayment.setUsedKind("kakaoPay");
        nomalPayment.setName((String)httpSession.getAttribute("name"));
        paymentService.insertPayment(nomalPayment,(int)httpSession.getAttribute("totalPrice"));
    }  
    public JSONObject getPaymentResult(String pgToken,HttpSession httpSession) {
        System.out.println("getPaymentResult");
        System.out.println(" getPaymentResult"+httpSession.getAttribute("tid")+pgToken);
        try {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization","KakaoAK "+adminKey);
            body.add("cid", cid);
            body.add("tid",httpSession.getAttribute("tid"));
            body.add("partner_order_id",1234+"");
            body.add("partner_user_id", "kim@kim.com");
            body.add("quantity", 1+"");
            body.add("pg_token", pgToken);
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            JSONObject response=restTemplate.postForObject("https://kapi.kakao.com/v1/payment/approve", entity,JSONObject.class);
            System.out.println(response+" 카카오페이 통신요청 결과");
            return response;
        } catch (Exception e) {
           e.printStackTrace();
           System.out.println("getPaymentResult"+ e.getMessage());
           throw new RuntimeException("카카오 결제 등록 실패");
        }
        
    } 
}
