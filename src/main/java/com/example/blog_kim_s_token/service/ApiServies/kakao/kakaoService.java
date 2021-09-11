package com.example.blog_kim_s_token.service.ApiServies.kakao;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.blog_kim_s_token.model.user.userDto;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class kakaoService {
    private final String getTokenUrl="https://kauth.kakao.com/oauth/token";
    private final String LoginCallBckUrl="http://localhost:8080/auth/kakaocallback";
    private final String requestLoginUrl="https://kapi.kakao.com/v2/user/me";
    private final String requestMessageCallBackUrl="http://localhost:8080/auth/kakaocallback2";
    private final String requestMessageUrl="https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private final String kakao="kakao";
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

    @Value("${kakao.apikey}")
    private String apikey;
    @Value("${kakao.adminkey}")
    private String adminKey;
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
    
    public String kakaoGetLoginCode() {
        System.out.println("kakaoGetLoginCode");
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+apikey+"&redirect_uri="+LoginCallBckUrl+"";
    }
    public String getMoreOk(HttpServletRequest request) {
        System.out.println("getMoreOk 추후 scope변수화 예정" );
        HttpSession httpSession=request.getSession();
        insertKakaoTokenDto dto=kakaoTokenDao.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null);
        httpSession.setAttribute("token",dto.getAccessToken());
        return "https://kauth.kakao.com/oauth/authorize?client_id="+apikey+"&redirect_uri="+requestMessageCallBackUrl+"&response_type=code&scope=talk_message";
    }
    private JSONObject requestToKakao(String url) {
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
        makeBodyAndHeader(code,LoginCallBckUrl);
        JSONObject getToken=requestToKakao(getTokenUrl);
        System.out.println(getToken+" 카카오통신응답");
        headers.add("Authorization","Bearer "+(String)getToken.get("access_token"));
        JSONObject getProfile =requestToKakao(requestLoginUrl);
        System.out.println(getProfile+" 카카오통신응답");
        LinkedHashMap<String,Object> profile=(LinkedHashMap<String,Object>)getProfile.get("kakao_account");
        userDto dto=kakaoLoginservice.kakaoLogin(profile,getToken);   
        kakaoLoginservice.makeCookie(dto, response);
     }
     public void sendMessege(String code,HttpServletRequest request) {
        System.out.println("sendMessege");
        HttpSession httpSession=request.getSession();
        makeBodyAndHeader(code,requestMessageCallBackUrl);
        String getToken=(String) httpSession.getAttribute("token");//requestToKakao(getTokenUrl);
        System.out.println(getToken+" 카카오통신응답");
        headers.add("Authorization","Bearer "+getToken);
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
     private void makeBodyAndHeader(String code,String callBackUrl) {
        System.out.println("makeBodyAndHeader");
        body.add("grant_type", "authorization_code");
        body.add("client_id", apikey);
        body.add("redirect_uri", callBackUrl);
        body.add("code", code);
     }
     public insertKakaoTokenDto selectByEmail(String email) {
       return  kakaoTokenDao.findByEmail(email).orElse(new insertKakaoTokenDto());
     }

    

}
