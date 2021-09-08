package com.example.blog_kim_s_token.service.ApiServies.kakao;






import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.example.blog_kim_s_token.customException.failKakaoPay;
import com.example.blog_kim_s_token.enums.aboutPayEnums;
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
    private final String sucUrl="http://localhost:8080/api/okKakaopay";
    private final String cancleUrl="http://localhost:8080/auth/cancleKakaopay";
    private final String failUrl="http://localhost:8080/auth/failKakaopay";
    private final String readyUrl="https://kapi.kakao.com/v1/payment/ready";
    private final String approveUrl="https://kapi.kakao.com/v1/payment/approve";
    private final String realCancleUrl="https://kapi.kakao.com/v1/payment/cancel";
    private final String status="paid";
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private MultiValueMap<String,Object> body=new LinkedMultiValueMap<>();

 
    @Autowired
    private paymentService paymentService;
    @Autowired
    private userService userService;
    @Autowired
    private resevationService resevationService;
    
    
    public JSONObject doKakaoPay(tryKakaoPayDto tryKakaoPayDto,HttpServletRequest request) {
        System.out.println("doKakaoPay");
        System.out.println(tryKakaoPayDto);
        try {
            String[][] itemArray=tryKakaoPayDto.getItemArray();
            String kind=aboutPayEnums.valueOf(tryKakaoPayDto.getKind()).getString();
            Map<String,Object>result=paymentService.getTotalPageAndOther(itemArray, kind);
            System.out.println(result+" 상품정보 가공");
            int totalPrice=(int)result.get("totalPrice");
            String itemName=(String)result.get("itemName");
            int count=(int)result.get("count");
            List<Integer>times=(List<Integer>)result.get("times");
            paymentService.confrimProduct(tryKakaoPayDto.getTotalPrice(),totalPrice,count,itemName);
            System.out.println(itemName+"/"+count);
            String partner_order_id=utillService.GetRandomNum(10);
            userDto userDto=userService.sendUserDto();
            body.add("cid", cid);
            body.add("partner_order_id",partner_order_id);
            body.add("partner_user_id", userDto.getEmail());
            body.add("item_name", itemName);
            body.add("quantity", count);
            body.add("total_amount", totalPrice);
            body.add("tax_free_amount", 0);
            body.add("approval_url", sucUrl);
            body.add("cancel_url", cancleUrl);
            body.add("fail_url", failUrl);
            JSONObject response=getPayLink(readyUrl);
            System.out.println(response+" 카카오페이 통신요청 결과");
            HttpSession httpSession=request.getSession();
            httpSession.setAttribute("partner_order_id", partner_order_id);
            httpSession.setAttribute("tid", response.get("tid"));
            httpSession.setAttribute("item", itemName);
            httpSession.setAttribute("totalPrice", totalPrice);
            httpSession.setAttribute("kind", kind);
            httpSession.setAttribute("email",userDto.getEmail());
            httpSession.setAttribute("name", userDto.getName());
            httpSession.setAttribute("count", count);
            httpSession.setAttribute("itemArray", itemArray);
            httpSession.setAttribute("other", tryKakaoPayDto.getOther());
            httpSession.setAttribute("times", times);
            return utillService.makeJson(true,(String)response.get("next_redirect_pc_url"));
        }catch(IllegalArgumentException e){
            e.printStackTrace();
            System.out.println("doKakaoPay");
            throw new RuntimeException("조건에 맞지 않는 상품선택입니다");
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("doKakaoPay");
            throw new RuntimeException(e.getMessage());
        }finally{
            headers.clear();
            body.clear();
        }  
    }
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
        List<Integer>times=(List<Integer>)httpSession.getAttribute("times");
        try {
            body.add("cid", cid);
            body.add("tid",paymentid);
            body.add("partner_order_id",httpSession.getAttribute("partner_order_id"));
            body.add("partner_user_id", httpSession.getAttribute("email"));
            body.add("quantity",httpSession.getAttribute("count"));
            body.add("pg_token", pgToken);
            JSONObject response=getPayLink(approveUrl);
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
                resevationService.doReservation(email,name, paymentid, itemArray, other, times,status,usedKind);
      
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
    private JSONObject getPayLink(String url) {
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
    public void  cancleKakaopay(MultiValueMap<String,Object> body) {
        System.out.println("cancleKakaopay");
        try {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization","KakaoAK "+adminKey);
           
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            JSONObject response=restTemplate.postForObject(realCancleUrl,entity,JSONObject.class);
            System.out.println(response+" 카카오페이 취소완료");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cancleKakaopay error");
        }finally{
            headers.clear();
            body.clear();
        }
      
    }
}
