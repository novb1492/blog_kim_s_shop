package com.example.blog_kim_s_token.service.ApiServies.kakao;

import com.example.blog_kim_s_token.config.security;

import com.example.blog_kim_s_token.enums.confrimTrue;
import com.example.blog_kim_s_token.enums.role;
import com.example.blog_kim_s_token.jwt.jwtService;
import com.example.blog_kim_s_token.model.jwt.jwtDto;
import com.example.blog_kim_s_token.model.oauth.kakao.kakaoAccountDto;
import com.example.blog_kim_s_token.model.user.userDao;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.csrfTokenService;
import com.example.blog_kim_s_token.service.cookie.cookieService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;



import javax.servlet.http.HttpServletResponse;


@Service
public class kakaoLoginservice {
    private final String kakao="kakao";

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

    public userDto kakaoLogin(LinkedHashMap<String,Object> profile,JSONObject getToken) {
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
            saveToken(getToken, email);
            return dto;
    }
    private void saveToken(JSONObject getToken,String email) {
        System.out.println("saveToken");
        try {
            insertKakaoTokenDto dto=kakaoTokenDao.findByEmail(email).orElse(null);
            System.out.println(dto);
            if(dto==null){
                dto=insertKakaoTokenDto.builder().accessToken((String)getToken.get("access_token"))
                                        .email(email)
                                        .refreshToken((String)getToken.get("refresh_token")).build();
                kakaoTokenDao.save(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("saveToken error"+e.getMessage());
            throw new RuntimeException("카카오 token db저장 실패");
        }
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
