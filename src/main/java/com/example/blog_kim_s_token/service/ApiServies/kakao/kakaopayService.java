package com.example.blog_kim_s_token.service.ApiServies.kakao;





import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.example.blog_kim_s_token.enums.paymentEnums;
import com.example.blog_kim_s_token.model.product.productDao;
import com.example.blog_kim_s_token.model.product.productDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.priceService;
import com.example.blog_kim_s_token.service.userService;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.payment.paymentService;
import com.example.blog_kim_s_token.service.payment.iamPort.nomalPayment;
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
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private MultiValueMap<String,Object> body=new LinkedMultiValueMap<>();

    @Autowired
    private productDao productDao;
    @Autowired
    private paymentService paymentService;
    @Autowired
    private priceService priceService;
    @Autowired
    private userService userService;
    
    public JSONObject doKakaoPay(JSONObject jsonObject,HttpServletRequest request) {
        List<Integer>count=(List<Integer>)jsonObject.get("count");
        int countSize=count.size();
        productDto productDto=productDao.findByProductName((String)jsonObject.get("item"));
        int totalPrice=priceService.getTotalPrice((String)jsonObject.get("item"), countSize);
        if((int)jsonObject.get("totalPrice")==totalPrice){
            return getPayLink(jsonObject,request,countSize,totalPrice,productDto.getBigKind());
        }
        return utillService.makeJson(false, "결제검증 실패");
    }

    private JSONObject getPayLink(JSONObject jsonObject,HttpServletRequest request,int count,int totalPrice,String kind) {
        System.out.println("getPayLink");
        try {
            userDto userDto=userService.sendUserDto();
            String email=userDto.getEmail();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization","KakaoAK "+adminKey);
            body.add("cid", cid);
            body.add("partner_order_id",1234+"");
            body.add("partner_user_id", email);
            body.add("item_name", jsonObject.get("item"));
            body.add("quantity", count);
            body.add("total_amount", totalPrice);
            body.add("tax_free_amount", 0);
            body.add("approval_url", "http://localhost:8080/api/okKakaopay");
            body.add("cancel_url", "http://localhost:8080/auth/cancleKakaopay");
            body.add("fail_url", "http://localhost:8080/auth/failKakaopay");
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            JSONObject response=restTemplate.postForObject("https://kapi.kakao.com/v1/payment/ready", entity,JSONObject.class);
            System.out.println(response+" 카카오페이 통신요청 결과");
            HttpSession httpSession=request.getSession();
            httpSession.setAttribute("tid", response.get("tid"));
            httpSession.setAttribute("seat", jsonObject.get("seat"));
            httpSession.setAttribute("month", jsonObject.get("month"));
            httpSession.setAttribute("date", jsonObject.get("date"));
            httpSession.setAttribute("times", jsonObject.get("times"));
            httpSession.setAttribute("year", jsonObject.get("year"));
            httpSession.setAttribute("totalPrice", totalPrice);
            httpSession.setAttribute("kind", kind);
            httpSession.setAttribute("email",email);
            httpSession.setAttribute("name", userDto.getName());
            return utillService.makeJson(true,(String)response.get("next_redirect_pc_url"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getPayLink error "+ e.getMessage());
            throw new RuntimeException("카카오 통신 실패");
        }finally{
            headers.clear();
            body.clear();
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public JSONObject insertPaymentForkakao(String pgToken,HttpSession httpSession) {
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
        return utillService.makeJson(true, "예약이 완료 되었습니다");
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
