package com.example.blog_kim_s_token.service.ApiServies.naver;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.blog_kim_s_token.config.security;
import com.example.blog_kim_s_token.enums.role;
import com.example.blog_kim_s_token.jwt.jwtService;
import com.example.blog_kim_s_token.model.jwt.jwtDto;
import com.example.blog_kim_s_token.model.oauth.naver.naverDto;
import com.example.blog_kim_s_token.model.user.userDao;
import com.example.blog_kim_s_token.model.user.userDto;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



@Service
public class naverLoginService   {
    
    private final String id="DrqDuzgTpM_sfreaZMly";
    private final String pwd="wCLQZ1kaQT";
    private final String callBackUrl="http://localhost:8080/auth/navercallback";
    private final int t=1;
    private final int zero=0;
    @Value("${oauth.pwd}")
    private String oauthPwd;

    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();

    @Autowired
    private userDao dao;
    @Autowired
    private security security;
    @Autowired
    private jwtService jwtService;


    public String naverLogin() {
        String state="";
        try {
            state = URLEncoder.encode(callBackUrl, "UTF-8");
            return "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="+id+"&redirect_uri="+""+callBackUrl+""+"&state="+state+"";
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            throw new RuntimeException("naverLogin 오류 발생");
        } 
    }
    public JSONObject getNaverToken(String code,String state) {
         JSONObject jsonObject= restTemplate.getForObject("https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&client_id="+id+"&client_secret="+pwd+"&code="+code+"&state="+state+"",JSONObject.class);
         System.out.println(jsonObject+" token"); 
         return jsonObject;
     }
     public String LoginNaver(JSONObject jsonObject,HttpServletRequest request,HttpServletResponse response) {
        headers.add("Authorization", "Bearer "+jsonObject.get("access_token"));
        HttpEntity<JSONObject>entity=new HttpEntity<JSONObject>(headers);
        try {
           naverDto naverDto =restTemplate.postForObject("https://openapi.naver.com/v1/nid/me",entity,naverDto.class);
           System.out.println(naverDto+ "정보");
           
           String[] split=naverDto.getResponse().get("email").toString().split("@");
           String email="";

           if(!split[1].equals("naver.com")){
               email=split[0]+"@naver.com";
           }else{
               email=naverDto.getResponse().get("email").toString();
           }
               split=naverDto.getResponse().get("mobile_e164").toString().split("2");
               userDto dto=dao.findByEmail(email);
               if(dto==null){
               BCryptPasswordEncoder bCryptPasswordEncoder=security.pwdEncoder();
                String phoneNum=(String)naverDto.getResponse().get("mobile");
                dto=new userDto(0, email,(String)naverDto.getResponse().get("name"),bCryptPasswordEncoder.encode(oauthPwd),role.USER.getValue(),"우편번호","서울 00동","00아파트","00동",phoneNum.replace("-", ""),t,t,zero,zero);  
                dao.save(dto);
               }
               userDto userDto=new userDto(dto.getId(), dto.getEmail(), dto.getName(),oauthPwd, dto.getRole(),dto.getPostCode(),dto.getAddress(),dto.getDetailAddress(),dto.getExtraAddress(),dto.getPhoneNum(),t,t,zero,zero);
               Authentication authentication=jwtService.confrimAuthenticate(userDto);
               jwtService.setSecuritySession(authentication);

               jwtDto jwtDto=jwtService.getRefreshToken(userDto.getId());
               String jwt="Bearer "+jwtService.getJwtToken(dto.getId());
               Cookie cookie=new Cookie("refreshToken", jwtService.getRefreshToken(jwtDto, userDto.getId()));
               cookie.setHttpOnly(true);
               cookie.setPath("/");

               response.addCookie(cookie);
               return jwt;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LoginNaver 오류가 발생 했습니다");
        }finally{
            headers.clear();
        }
     }
     
}
