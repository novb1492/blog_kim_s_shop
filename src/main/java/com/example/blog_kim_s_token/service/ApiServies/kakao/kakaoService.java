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
    private final String apikey="2b8214590890931fb474d08986898680";
    private final String adminKey="ac5d7bd93834444767d1b59477e6f92f";
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
        makeBodyAndHeader(code);
        JSONObject getToken=requestToKakao(getTokenUrl);
        System.out.println(response+" 카카오통신응답");
        headers.add("Authorization","Bearer "+(String)getToken.get("access_token"));
        JSONObject getProfile =requestToKakao(requestLoginUrl);
        System.out.println(getProfile+" 카카오통신응답");
        LinkedHashMap<String,Object> profile=(LinkedHashMap<String,Object>)getProfile.get("kakao_account");
        userDto dto=kakaoLogin(profile);   
        makeCookie(dto, response);
     }
     private void makeBodyAndHeader(String code) {
        System.out.println("makeBodyAndHeader");
        body.add("grant_type", "authorization_code");
        body.add("client_id", apikey);
        body.add("redirect_uri", LoginCallBckUrl);
        body.add("code", code);
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

}
