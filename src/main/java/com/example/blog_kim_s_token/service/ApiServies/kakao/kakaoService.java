package com.example.blog_kim_s_token.service.ApiServies.kakao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.example.blog_kim_s_token.model.user.userDto;
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
    private final String LoginCallBckUrl="http://localhost:8080/auth/kakaocallback";
    private final String requestLoginUrl="https://kapi.kakao.com/v2/user/me";
    private final String requestMessageCallBackUrl="http://localhost:8080/auth/kakaocallback2";
    private final String requestMessageUrl="https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private final String getAccessTokenGrandType="authorization_code";
    private final String getRefreshTokenGrandType="refresh_token";
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
    

    

}
