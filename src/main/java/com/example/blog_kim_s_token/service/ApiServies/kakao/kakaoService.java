package com.example.blog_kim_s_token.service.ApiServies.kakao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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
import com.example.blog_kim_s_token.service.reservation.reservationService;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class kakaoService {
    private final String getTokenUrl="https://kauth.kakao.com/oauth/token";
    private final String LoginCallBckUrl="http://localhost:8080/auth/kakaoLogincallback";
    private final String requestLoginUrl="https://kapi.kakao.com/v2/user/me";
    private final String requestMessageCallBackUrl="http://localhost:8080/auth/kakaoMoreOkcallback";
    private final String requestMessageUrl="https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private final String getAccessTokenGrandType="authorization_code";
    private final String getRefreshTokenGrandType="refresh_token";
    private final String kakao="kakao";
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

    @Value("${kakao.apikey}")
    private String apikey;
    @Value("${kakao.adminkey}")
    private String adminKey;
    @Value("${kakao.kakaoPay.cid}")
    private String cid;
    @Value("${oauth.pwd}")
    private String oauthPwd;
    @Value("${jwt.accessToken.name}")
    private String AuthorizationTokenName;
    @Value("${jwt.refreshToken.name}")
    private String refreshTokenName;

    @Autowired
    private kakaoTokenDao kakaoTokenDao;
    @Autowired
    private kakaoLoginservice kakaoLoginservice;
    @Autowired
    private kakaoMessageService kakaoMessageService;
    @Autowired
    private kakaopayService kakaopayService;
    @Autowired
    private userService userService;
    @Autowired
    private jwtService jwtService;
    @Autowired
    private paymentService paymentService;
    @Autowired
    private reservationService resevationService;

    
    public String kakaoGetLoginCode() {
        System.out.println("kakaoGetLoginCode");
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+apikey+"&redirect_uri="+LoginCallBckUrl+"";
    }
    public String getMoreOk(HttpServletRequest request) {
        System.out.println("getMoreOk 추후 scope변수화 예정" );
        return "https://kauth.kakao.com/oauth/authorize?client_id="+apikey+"&redirect_uri="+requestMessageCallBackUrl+"&response_type=code&scope=talk_message";
    }
    public JSONObject requestToKakao(String url) {
        System.out.println("requestToKakao");
        try {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            System.out.println(entity.getBody()+" 요청정보"+entity.getHeaders());
            return restTemplate.postForObject(url,entity,JSONObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("requestToKakao error "+ e.getMessage());
            throw new RuntimeException("카카오 통신 실패");
        }finally{
            body.clear();
            headers.clear();
        }
    }
    public void kakaoLogin(String code,HttpServletResponse response) {
        System.out.println("kakaoLogin");
        makeBodyAndHeader(code,LoginCallBckUrl,getAccessTokenGrandType);
        JSONObject getToken=requestToKakao(getTokenUrl);
        System.out.println(getToken+" 카카오통신응답");
        headers.add("Authorization","Bearer "+(String)getToken.get("access_token"));
        JSONObject getProfile =requestToKakao(requestLoginUrl);
        System.out.println(getProfile+" 카카오통신응답");
        LinkedHashMap<String,Object> profile=(LinkedHashMap<String,Object>)getProfile.get("kakao_account");
        userDto dto=kakaoLoginservice.kakaoLogin(profile,getToken);   
        kakaoLoginservice.makeCookie(dto, response);
     }
     @Transactional
     public void sendMessege(){
        System.out.println("sendMessege");
        insertKakaoTokenDto insertKakaoTokenDto=kakaoTokenDao.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new RuntimeException("카카오 토큰이없습니다"));
        confrimTokenExprise(insertKakaoTokenDto);
        System.out.println(insertKakaoTokenDto.getAccessToken()+ "카카오토큰");
        headers.add("Authorization","Bearer "+insertKakaoTokenDto.getAccessToken());
        JSONObject jsonObject=new JSONObject();
        JSONObject jsonObject2=new JSONObject();
        jsonObject2.put("web_url","http:localhost:3030/index.html");
        jsonObject.put("object_type", "text");
        jsonObject.put("link",jsonObject2);
        System.out.println(jsonObject2.toString());
        jsonObject.put("text", "value");
        System.out.println(jsonObject.toString());
        body.add("template_object",jsonObject);
        JSONObject response =requestToKakao(requestMessageUrl);
        System.out.println(response+" 카카오통신응답");

     }
     private void makeBodyAndHeader(String code,String callBackUrl,String grantType) {
        System.out.println("makeBodyAndHeader");
        body.add("grant_type", grantType);
        body.add("client_id", apikey);
        body.add("redirect_uri", callBackUrl);
        body.add("code", code);
    }
    public insertKakaoTokenDto selectByEmail(String email) {
       return  kakaoTokenDao.findByEmail(email).orElse(new insertKakaoTokenDto());
    }
    private void confrimTokenExprise(insertKakaoTokenDto insertKakaoTokenDto) {
        try {
            if(LocalDateTime.now().isAfter(insertKakaoTokenDto.getAccessTokenExpiresin().toLocalDateTime())){
                System.out.println("카카오 토큰만료 토큰 재요청");
                makeBodyAndHeader(null, null,getRefreshTokenGrandType);
                body.add("refresh_token", insertKakaoTokenDto.getRefreshToken());
                JSONObject getToken=requestToKakao(getTokenUrl);
                System.out.println(getToken+" 카카오토큰통신결과");
                insertKakaoTokenDto.setAccessToken((String)getToken.get("access_token"));
                insertKakaoTokenDto.setAccessTokenExpiresin(Timestamp.valueOf(LocalDateTime.now().plusSeconds((int)getToken.get("expires_in"))));
            } 
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("confrimTokenExprise error"+e.getMessage());
            throw new RuntimeException("갱신에 실패했습니다 다시 로그인 부탁드립니다");
        }   
    }
    public JSONObject showPaidWindow(tryKakaoPayDto tryKakaoPayDto,HttpServletRequest request,HttpServletResponse httpServletResponse) {
        System.out.println("doKakaoPay");
        System.out.println(tryKakaoPayDto);
        try {

            String[][] itemArray=tryKakaoPayDto.getItemArray();
            String kind=aboutPayEnums.valueOf(tryKakaoPayDto.getKind()).getString();
            Map<String,Object>result=paymentService.getTotalPriceAndOther(itemArray, kind);
            System.out.println(result+" 상품정보 가공");
            int totalPrice=(int)result.get("totalPrice");
            String itemName=(String)result.get("itemName");
            int count=(int)result.get("count");
            List<Integer>timesOrSize=(List<Integer>)result.get("timesOrSize");
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
            headers.add("Authorization","KakaoAK "+adminKey);
            JSONObject response=requestToKakao(readyUrl);
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
            httpSession.setAttribute("timesOrSize", timesOrSize);
            jwtService.makeNewAccessToken(request, httpServletResponse);

            return utillService.makeJson(true,(String)response.get("next_redirect_pc_url"));
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("doKakaoPay");
            throw new RuntimeException("카카오 페이 불러오기 실패");
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public JSONObject requestKakaopay(String pgToken,HttpSession httpSession) {
        System.out.println("requestKakaopay");
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
            headers.add("Authorization","KakaoAK "+adminKey);
            JSONObject response=requestToKakao(approveUrl);
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
            if(kind.equals(aboutPayEnums.reservation.getString())){
                System.out.println("예약 상품 결제");
                resevationService.doReservation(email,name, paymentid, itemArray, other,timesOrSize,status,usedKind);
      
            }else if(kind.equals(aboutPayEnums.product.getString())){
                System.out.println("상품결제");
            }
  
           return utillService.makeJson(true, "완료 되었습니다");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insertPaymentForkakao error"+e.getMessage());
            throw new failKakaoPay(e.getMessage(),cid,paymentid,totalPrice);
        }
    }  
    public void  cancleKakaopay(MultiValueMap<String,Object> body2) {
        System.out.println("cancleKakaopay");
        body=body2;
        headers.add("Authorization","KakaoAK "+adminKey);
        requestToKakao(realCancleUrl);
    }
  
    

    

}
