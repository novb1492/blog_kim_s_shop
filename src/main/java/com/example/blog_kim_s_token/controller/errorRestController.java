package com.example.blog_kim_s_token.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.blog_kim_s_token.customException.failBuyException;
import com.example.blog_kim_s_token.jwt.jwtService;
import com.example.blog_kim_s_token.model.jwt.jwtDto;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.cookie.cookieService;
import com.example.blog_kim_s_token.service.payment.bootPay.bootPayService;
import com.example.blog_kim_s_token.service.payment.iamPort.iamportService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private bootPayService bootPayService;
    
    
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
    public JSONObject failBuyException(failBuyException exception) {
        System.out.println("failBuyException 환불시작");
        try {
            if(exception.getPayMentId().startsWith("imp")){
                System.out.println("아임포트 환불");
                iamportService.cancleBuy(exception.getPayMentId(),0);
            }else{
                System.out.println("부트페이 환불");
                bootPayService.cancleBuy(exception.getPayMentId(), 0, SecurityContextHolder.getContext().getAuthentication().getName(), "결제로직 실패");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            utillService.makeJson(false,e.getMessage());
        }
        return utillService.makeJson(true, exception.getMessage());
    }
}
