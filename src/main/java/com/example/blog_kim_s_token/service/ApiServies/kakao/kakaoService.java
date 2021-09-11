package com.example.blog_kim_s_token.service.ApiServies.kakao;

import java.util.LinkedHashMap;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.example.blog_kim_s_token.config.security;
import com.example.blog_kim_s_token.enums.confrimTrue;
import com.example.blog_kim_s_token.enums.role;
import com.example.blog_kim_s_token.jwt.jwtService;
import com.example.blog_kim_s_token.model.jwt.jwtDto;
import com.example.blog_kim_s_token.model.oauth.kakao.kakaoAccountDto;
import com.example.blog_kim_s_token.model.oauth.kakao.kakaoLoginDto;
import com.example.blog_kim_s_token.model.oauth.kakao.kakaoTokenDto;
import com.example.blog_kim_s_token.model.user.userDao;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.csrfTokenService;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.cookie.cookieService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
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
    private userDao userDao;
    @Autowired
    private security security;
    @Autowired
    private jwtService jwtService;
    @Autowired
    private csrfTokenService csrfService;
    @Autowired
    private kakaoLoginservice kakaoLoginservice;
    
    public String kakaoGetLoginCode() {
        System.out.println("kakaoGetLoginCode");
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+apikey+"&redirect_uri="+LoginCallBckUrl+"";
    }
    public String getMoreOk() {
        System.out.println("getMoreOk 추후 scope변수화 예정" );
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
        System.out.println(response+" 카카오통신응답");
        headers.add("Authorization","Bearer "+(String)getToken.get("access_token"));
        JSONObject getProfile =requestToKakao(requestLoginUrl);
        System.out.println(getProfile+" 카카오통신응답");
        LinkedHashMap<String,Object> profile=(LinkedHashMap<String,Object>)getProfile.get("kakao_account");
        userDto dto=kakaoLoginservice.kakaoLogin(profile);   
        kakaoLoginservice.makeCookie(dto, response);
     }
     private void makeBodyAndHeader(String code,String callBackUrl) {
        System.out.println("makeBodyAndHeader");
        body.add("grant_type", "authorization_code");
        body.add("client_id", apikey);
        body.add("redirect_uri", callBackUrl);
        body.add("code", code);
     }
    

}
