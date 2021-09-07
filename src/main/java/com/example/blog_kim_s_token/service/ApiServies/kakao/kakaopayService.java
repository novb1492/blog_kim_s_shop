package com.example.blog_kim_s_token.service.ApiServies.kakao;





import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.example.blog_kim_s_token.enums.paymentEnums;
import com.example.blog_kim_s_token.model.product.productDao;
import com.example.blog_kim_s_token.model.product.productDto;
import com.example.blog_kim_s_token.model.reservation.reservationInsertDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.priceService;
import com.example.blog_kim_s_token.service.userService;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.payment.paymentService;
import com.example.blog_kim_s_token.service.payment.paymentabstract;
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
    private final String sucUrl="http://localhost:8080/api/okKakaopay";
    private final String cancleUrl="http://localhost:8080/auth/cancleKakaopay";
    private final String failUrl="http://localhost:8080/auth/failKakaopay";
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
    @Autowired
    private resevationService resevationService;
    
    public JSONObject doKakaoPay(JSONObject jsonObject,HttpServletRequest request) {
        List<Integer>count=(List<Integer>)jsonObject.get("count");
        int countSize=count.size();
        productDto productDto=productDao.findByProductName((String)jsonObject.get("item"));
        int totalPrice=priceService.getTotalPrice((String)jsonObject.get("item"), countSize);
        int partner_order_id=Integer.parseInt(utillService.GetRandomNum(10));
        if((int)jsonObject.get("totalPrice")==totalPrice){
            userDto userDto=userService.sendUserDto();
            body.add("cid", cid);
            body.add("partner_order_id",partner_order_id);
            body.add("partner_user_id", userDto.getEmail());
            body.add("item_name", jsonObject.get("item"));
            body.add("quantity", countSize);
            body.add("total_amount", totalPrice);
            body.add("tax_free_amount", 0);
            body.add("approval_url", sucUrl);
            body.add("cancel_url", cancleUrl);
            body.add("fail_url", failUrl);
            JSONObject response=getPayLink();
            System.out.println(response+" 카카오페이 통신요청 결과");
            HttpSession httpSession=request.getSession();
            httpSession.setAttribute("partner_order_id", partner_order_id);
            httpSession.setAttribute("tid", response.get("tid"));
            httpSession.setAttribute("item", jsonObject.get("item"));
            httpSession.setAttribute("month", jsonObject.get("month"));
            httpSession.setAttribute("date", jsonObject.get("date"));
            httpSession.setAttribute("times", jsonObject.get("times"));
            httpSession.setAttribute("year", jsonObject.get("year"));
            httpSession.setAttribute("totalPrice", totalPrice);
            httpSession.setAttribute("kind", productDto.getBigKind());
            httpSession.setAttribute("email",userDto.getEmail());
            httpSession.setAttribute("name", userDto.getName());
            httpSession.setAttribute("count", count);
            return utillService.makeJson(true,(String)response.get("next_redirect_pc_url"));
        }
        return utillService.makeJson(false, "결제검증 실패");
    }
    private JSONObject getPayLink() {
        System.out.println("getPayLink");
        try {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization","KakaoAK "+adminKey);
           
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            return restTemplate.postForObject("https://kapi.kakao.com/v1/payment/ready",entity,JSONObject.class);
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
        try {
            List<Integer>count=(List<Integer>)httpSession.getAttribute("count");
            body.add("cid", cid);
            body.add("tid",httpSession.getAttribute("tid"));
            body.add("partner_order_id",httpSession.getAttribute("partner_order_id"));
            body.add("partner_user_id", httpSession.getAttribute("email"));
            body.add("quantity", count.size());
            body.add("pg_token", pgToken);
            JSONObject response=getPaymentResult();
            nomalPayment nomalPayment=new nomalPayment();
            nomalPayment.setKind((String)httpSession.getAttribute("kind"));
            nomalPayment.setEmail((String)httpSession.getAttribute("email"));
            nomalPayment.setPayMethod("kakaoPay");
            nomalPayment.setPaymentid((String)response.get("tid"));
            nomalPayment.setStatus("paid");
            nomalPayment.setUsedKind("kakaoPay");
            nomalPayment.setName((String)httpSession.getAttribute("name"));
            paymentService.insertPayment(nomalPayment,(int)httpSession.getAttribute("totalPrice"));
            doReservation(nomalPayment,(int)httpSession.getAttribute("month"),(int)httpSession.getAttribute("year"),(int)httpSession.getAttribute("date"),count,(String)httpSession.getAttribute("item"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insertPaymentForkakao error"+e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
   
        return utillService.makeJson(true, "예약이 완료 되었습니다");
    }  
    public JSONObject getPaymentResult() {
        System.out.println("getPaymentResult");
        try {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization","KakaoAK "+adminKey);
  
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
    private void doReservation(nomalPayment nomalPayment,int month,int year,int date,List<Integer>count,String seat ) {
        System.out.println("doReservation");
        try {
            reservationInsertDto reservationInsertDto=new reservationInsertDto();
            reservationInsertDto.setEmail(nomalPayment.getEmail());
            reservationInsertDto.setName(nomalPayment.getName());
            reservationInsertDto.setStatus("paid");
            reservationInsertDto.setUsedKind(nomalPayment.getUsedKind());
            reservationInsertDto.setDate(date);
            reservationInsertDto.setMonth(month);
            reservationInsertDto.setPaymentId(nomalPayment.getPaymentid());
            reservationInsertDto.setYear(year);
            reservationInsertDto.setSeat(seat);
            reservationInsertDto.setTimes(count);
            resevationService.confrimContents(reservationInsertDto);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("doReservation error"+ e.getMessage());
            throw new RuntimeException("예약등록실패");
        }
    }
    public void cancleKakaopay() {
        
    }
}
