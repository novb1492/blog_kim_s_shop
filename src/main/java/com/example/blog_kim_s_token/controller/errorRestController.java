package com.example.blog_kim_s_token.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.blog_kim_s_token.customException.failBuyException;
import com.example.blog_kim_s_token.customException.failKakaoPay;
import com.example.blog_kim_s_token.jwt.jwtService;
import com.example.blog_kim_s_token.model.jwt.jwtDto;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.ApiServies.kakao.kakaopayService;
import com.example.blog_kim_s_token.service.cookie.cookieService;
import com.example.blog_kim_s_token.service.payment.iamPort.iamportService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class errorRestController {
    @Autowired
    private jwtService jwtService;
    @Autowired
    private iamportService iamportService;
    @Autowired
    private kakaopayService kakaopayService;

    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JSONObject processValidationError(MethodArgumentNotValidException exception) {
        System.out.println("processValidationError 유효성 검사 실패");
        BindingResult bindingResult = exception.getBindingResult();
        StringBuilder builder = new StringBuilder();
        List<String>list=new ArrayList<>();
        
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append(fieldError.getDefaultMessage());
            list.add(fieldError.getField());
        }

        return utillService.makeJson(false, builder.toString(),list);
    }
    @ExceptionHandler(TokenExpiredException.class)
    public JSONObject TokenExpiredException(TokenExpiredException exception,HttpServletRequest request,HttpServletResponse response) {
        System.out.println("TokenExpiredException 토큰 재발급시작");
        String refreshToken=null;
        Cookie[] cookies=request.getCookies();
            for(Cookie c:cookies){
                if(c.getName().equals("refreshToken")){
                    refreshToken=c.getValue();
                }
            }
        System.out.println(refreshToken+" 리프레시 토큰");
  
            jwtDto jwtDto=jwtService.getRefreshToken(refreshToken);
            String newJwtToken=jwtService.getNewJwtToken(jwtDto);
            System.out.println(newJwtToken+" 새 토큰");
            String[][] cookiesNamesAndValues=new String[1][3];
            cookiesNamesAndValues[0][0]="Authorization";
            cookiesNamesAndValues[0][1]=newJwtToken;
            cookiesNamesAndValues[0][2]="httponly";
            cookieService.cookieFactory(response, cookiesNamesAndValues);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("status", "newJwtToken");
            System.out.println("새토큰 발급완료");
            return jsonObject;
    }
    @ExceptionHandler(JWTDecodeException.class)
    public JSONObject JWTDecodeException(JWTDecodeException exception,HttpServletRequest request,HttpServletResponse response) {
        System.out.println("JWTDecodeException 입장");
        return utillService.makeJson(false, "재 로그인 부탁드립니다");
    }
    @ExceptionHandler(RuntimeException.class)
    public JSONObject runtimeException(RuntimeException exception) {
        System.out.println("runtimeException");
        exception.printStackTrace();
        return utillService.makeJson(false, exception.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public JSONObject IllegalArgumentException(IllegalArgumentException exception) {
        System.out.println("IllegalArgumentException");
        return utillService.makeJson(false, exception.getMessage());
    }
    @ExceptionHandler(failBuyException.class)
    public JSONObject failBuyException(failBuyException exception,HttpSession httpSession) {
        System.out.println("failBuyException 환불시작");
        JSONObject jsonObject=new JSONObject();
        try {
            if(httpSession.getAttribute("kind").equals("vbank")){
                jsonObject.put("merchant_uid", httpSession.getAttribute("merchantUid"));
                jsonObject.put("vbank_due",  httpSession.getAttribute("vbankDue"));
                jsonObject.put("vbank_holder",  httpSession.getAttribute("vbankHolder"));
                jsonObject.put("amount",  httpSession.getAttribute("amount"));
                jsonObject.put("vbank_code",  httpSession.getAttribute("vbank_code"));
                iamportService.cancleVbank(exception.getpaymentid(),jsonObject);
            }else{
                jsonObject.put("imp_uid", exception.getpaymentid());
                iamportService.cancleBuy(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            utillService.makeJson(false,e.getMessage());
        }
        return utillService.makeJson(false, exception.getMessage());
    }
    @ExceptionHandler(failKakaoPay.class)
    public JSONObject failKakaoPay(failKakaoPay failKakaoPay) {
        MultiValueMap<String,Object> body=new LinkedMultiValueMap<>();
        body.add("cid", failKakaoPay.getCid());
        body.add("tid", failKakaoPay.getTid());
        body.add("cancel_amount", failKakaoPay.getTotalPrice());
        body.add("cancel_tax_free_amount",0);
        kakaopayService.cancleKakaopay(body);
        return utillService.makeJson(false, failKakaoPay.getMessage());
    }
}
