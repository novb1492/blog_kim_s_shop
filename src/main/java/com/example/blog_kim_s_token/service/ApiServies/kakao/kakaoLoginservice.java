package com.example.blog_kim_s_token.service.ApiServies.kakao;

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
import com.nimbusds.jose.shaded.json.parser.JSONParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class kakaoLoginservice {
    private final String apikey="2b8214590890931fb474d08986898680";
    private final String callBackUrl="http://localhost:8080/auth/kakaocallback";
    private final String callBackUrl2="http://localhost:8080/auth/kakaocallback2";
    private final String kakao="kakao";
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private MultiValueMap<String,Object> body=new LinkedMultiValueMap<>();

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
    private kakaoTokenDao kakaoTokenDao;



    public String kakaoGetCode() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+apikey+"&redirect_uri="+callBackUrl+"";
    }
    public kakaoTokenDto kakaoGetToken(String code,String callbackUrl) {
        System.out.println(code+" kakaocode");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        body.add("grant_type", "authorization_code");
        body.add("client_id", apikey);
        body.add("redirect_uri", callbackUrl);
        body.add("code", code);
        body.add("scope","talk_message");
        try {
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            kakaoTokenDto kakaoTokenDto=restTemplate.postForObject("https://kauth.kakao.com/oauth/token",entity,kakaoTokenDto.class);
            System.out.println(kakaoTokenDto+" kakaotoken");
            return kakaoTokenDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("getKakaoToken 오류가 발생했습니다");
        }finally{
            headers.clear();
            body.clear();
        }
    }
    public userDto kakaoLogin(LinkedHashMap<String,Object> profile) {
        System.out.println("kakaoLogin");
            kakaoAccountDto kakaoAccountDto =new kakaoAccountDto((boolean)profile.get("email_needs_agreement"),(boolean)profile.get("profile_nickname_needs_agreement"),(LinkedHashMap<String,String>)profile.get("profile"),(boolean)profile.get("is_email_valid"),(boolean)profile.get("is_email_verified"),(boolean)profile.get("has_email"),(String)profile.get("email"));
            String email=kakaoAccountDto.getEmail();
            System.out.println(email);
            userDto dto=userDao.findByEmail(email);
            if(dto==null){
                    dto=userDto.builder().email(email)
                                        .name(kakaoAccountDto
                                        .getProfile().get("nickname"))
                                        .pwd(security.pwdEncoder().encode(oauthPwd))
                                        .role(role.USER.getValue())
                                        .postCode("111111")
                                        .address("address")
                                        .detailAddress("detailAddress")
                                        .phoneNum("phoneNum")
                                        .phoneCheck(confrimTrue.yes.getValue())
                                        .emailCheck(confrimTrue.yes.getValue())
                                        .provider(kakao).build(); 
                                        userDao.save(dto);
            }
            return dto;
    }
    public void makeCookie(userDto dto,HttpServletResponse response) {
        System.out.println("makeCookie");
        Authentication authentication=jwtService.confrimAuthenticate(dto);
        jwtService.setSecuritySession(authentication);

        String jwtToken=jwtService.getJwtToken(dto.getId());
        jwtDto jwtDto=jwtService.getRefreshToken(dto.getId());
        String refreshToken=jwtService.getRefreshToken(jwtDto,dto.getId());

        String csrfToken=csrfTokenService.getCsrfToken();
        csrfService.insertCsrfToken(dto.getId(),csrfToken,dto.getEmail());
        
        String[][] cookiesNamesAndValues=new String[3][3];
        cookiesNamesAndValues[0][0]=AuthorizationTokenName;
        cookiesNamesAndValues[0][1]=jwtToken;
        cookiesNamesAndValues[0][2]="httponly";
        cookiesNamesAndValues[1][0]=refreshTokenName;
        cookiesNamesAndValues[1][1]=refreshToken;
        cookiesNamesAndValues[1][2]="httponly";
        cookiesNamesAndValues[2][0]="csrfToken";
        cookiesNamesAndValues[2][1]=csrfToken;
        cookiesNamesAndValues[2][2]="httponly";
        cookieService.cookieFactory(response, cookiesNamesAndValues);
    }
    public JSONObject sendKakaoMessage(String code) {
        System.out.println("sendKakaoMessage");
        try {
            kakaoTokenDto kakaoTokenDto=kakaoGetToken(code,callBackUrl2);
            String accessToken=kakaoTokenDto.getAccess_token();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization", "Bearer "+accessToken);
            JSONObject jsonObject=new JSONObject();
            JSONObject jsonObject2=new JSONObject();
            jsonObject2.put("web_url","http:localhost:3030/index.html");


            jsonObject.put("object_type", "text");
            jsonObject.put("link",jsonObject2);
            System.out.println(jsonObject2.toString());
            jsonObject.put("text", "value");
            System.out.println(jsonObject.toString());
            body.add("template_object",jsonObject);
            

            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            System.out.println(entity.getBody().toString());
            JSONObject response=restTemplate.postForObject("https://kapi.kakao.com/v2/api/talk/memo/default/send",entity,JSONObject.class);
            System.out.println(response.toString());
            return utillService.makeJson(true, "예약내역이 전송되었습니다");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("sendKakaoMessage error"+e.getMessage());
            throw new RuntimeException("내역 보내기 실패 ");
        }
    }
    public String getNewToken(HttpSession httpSession) {
        String email=(String) httpSession.getAttribute("email");
        try {
          insertKakaoTokenDto kakaoTokenDto=kakaoTokenDao.findByEmail(email).orElseThrow();
          headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
          body.add("grant_type", "refresh_token");
          body.add("client_id",apikey);
          body.add("refresh_token", kakaoTokenDto.getRefreshToken());
          HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
          JSONObject response=restTemplate.postForObject("https://kauth.kakao.com/oauth/token",entity,JSONObject.class);
          String access_token=(String)response.get("access_token");
          System.out.println(access_token+"access_token");
          return access_token;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("토큰 재발급 실패");
        }finally{
            headers.clear();
            body.clear();
        }
    }
    public String getMoreOk(HttpServletRequest request) {
        try {
            HttpSession httpSession=request.getSession();
            httpSession.setAttribute("email", SecurityContextHolder.getContext().getAuthentication().getName());
          return "https://kauth.kakao.com/oauth/authorize?client_id="+apikey+"&redirect_uri="+callBackUrl2+"&response_type=code&scope=talk_message";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("getMoreOk");
        }
    }
   

}
