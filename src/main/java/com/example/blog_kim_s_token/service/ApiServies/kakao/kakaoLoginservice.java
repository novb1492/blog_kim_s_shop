package com.example.blog_kim_s_token.service.ApiServies.kakao;

import com.example.blog_kim_s_token.config.principaldetail;
import com.example.blog_kim_s_token.config.security;
import com.example.blog_kim_s_token.enums.confirmEnums;
import com.example.blog_kim_s_token.enums.confrimTrue;
import com.example.blog_kim_s_token.enums.role;
import com.example.blog_kim_s_token.jwt.jwtService;
import com.example.blog_kim_s_token.model.jwt.jwtDto;
import com.example.blog_kim_s_token.model.oauth.kakao.kakaoAccountDto;
import com.example.blog_kim_s_token.model.oauth.kakao.kakaoLoginDto;
import com.example.blog_kim_s_token.model.oauth.kakao.kakaoTokenDto;
import com.example.blog_kim_s_token.model.user.userDao;
import com.example.blog_kim_s_token.model.user.userDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.LinkedHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class kakaoLoginservice {
    private final String apikey="2b8214590890931fb474d08986898680";
    private final String callBackUrl="http://localhost:8080/auth/kakaocallback";
    private final String kakao="kakao";
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private MultiValueMap<String,String> body=new LinkedMultiValueMap<>();

    @Value("${oauth.pwd}")
    private String oauthPwd;

    @Autowired
    private userDao userDao;
    @Autowired
    private security security;
    @Autowired
    private jwtService jwtService;


    public String kakaoGetCode() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+apikey+"&redirect_uri="+callBackUrl+"";
    }
    public kakaoTokenDto kakaoGetToken(String code) {
        System.out.println(code+" kakaocode");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        body.add("grant_type", "authorization_code");
        body.add("client_id", apikey);
        body.add("redirect_uri", callBackUrl);
        body.add("code", code);
        try {
            HttpEntity<MultiValueMap<String,String>>entity=new HttpEntity<>(body,headers);
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
    public String kakaoLogin(kakaoTokenDto kakaoTokenDto,HttpServletResponse response) {
        headers.add("Authorization", "Bearer "+kakaoTokenDto.getAccess_token());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        try {
            HttpEntity<MultiValueMap<String,String>>entity=new HttpEntity<>(headers);
            kakaoLoginDto kakaoLoginDto =restTemplate.postForObject("https://kapi.kakao.com/v2/user/me",entity,kakaoLoginDto.class);
            System.out.println(kakaoLoginDto+"카카오 로그인정보");


            kakaoAccountDto kakaoAccountDto =new kakaoAccountDto((boolean)kakaoLoginDto.getKakao_account().get("email_needs_agreement"),(boolean)kakaoLoginDto.getKakao_account().get("profile_nickname_needs_agreement"),(LinkedHashMap<String,String>)kakaoLoginDto.getKakao_account().get("profile"),(boolean)kakaoLoginDto.getKakao_account().get("is_email_valid"),(boolean)kakaoLoginDto.getKakao_account().get("is_email_verified"),(boolean)kakaoLoginDto.getKakao_account().get("has_email"),(String)kakaoLoginDto.getKakao_account().get("email"));
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
                                        .extraAddress("exa")
                                        .phoneNum("phoneNum")
                                        .phoneCheck(confrimTrue.yes.getValue())
                                        .emailCheck(confrimTrue.yes.getValue())
                                        .provider(kakao).build(); 
                                        userDao.save(dto);
            }
            Authentication authentication=jwtService.confrimAuthenticate(dto);
            jwtService.setSecuritySession(authentication);

            String jwtToken=jwtService.getJwtToken(dto.getId());
            jwtDto jwtDto=jwtService.getRefreshToken(dto.getId());
            String refreshToken=jwtService.getRefreshToken(jwtDto,dto.getId());
            
            Cookie cookie=new Cookie("Authorization",jwtToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");

            Cookie cookie2=new Cookie("refreshToken",refreshToken);
            cookie2.setHttpOnly(true);
            cookie2.setPath("/");
        
            response.addCookie(cookie);
            response.addCookie(cookie2);
            
            return email;

        } catch (Exception e) {
           e.printStackTrace();
        }finally{
            headers.clear();
        }
        return null;
    }

}
