package com.example.blog_kim_s_token.service.ApiServies.kakao;

import com.example.blog_kim_s_token.model.oauth.kakao.kakaoLoginDto;
import com.example.blog_kim_s_token.model.oauth.kakao.kakaoTokenDto;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class kakaoLoginservice {
    private final String apikey="2b8214590890931fb474d08986898680";
    private final String callBackUrl="http://localhost:8080/auth/kakaocallback";
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private MultiValueMap<String,String> body=new LinkedMultiValueMap<>();

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
    public void kakaoLogin(kakaoTokenDto kakaoTokenDto) {
        headers.add("Authorization", "Bearer "+kakaoTokenDto.getAccess_token());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        try {
            HttpEntity<MultiValueMap<String,String>>entity=new HttpEntity<>(headers);
            kakaoLoginDto kakaoLoginDto =restTemplate.postForObject("https://kapi.kakao.com/v2/user/me",entity,kakaoLoginDto.class);
            System.out.println(kakaoLoginDto+"카카오 로그인정보");
        } catch (Exception e) {
           e.printStackTrace();
        }finally{
            headers.clear();
        }
    }

}
