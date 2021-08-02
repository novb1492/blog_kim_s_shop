package com.example.blog_kim_s_token.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.blog_kim_s_token.jwt.jwtService;
import com.example.blog_kim_s_token.model.jwt.jwtDto;
import com.example.blog_kim_s_token.model.user.userDao;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class errorRestController {
    @Autowired
    private utillService utillService;
    @Autowired
    private jwtService jwtService;
    @Autowired
    private userDao userDao;
    
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
        String refreshToken=request.getHeader("refreshToken");
        System.out.println(refreshToken+" 리프레시 토큰");
        if(refreshToken.startsWith("Bearer")){
            refreshToken=refreshToken.replace("Bearer ", "");
            jwtDto jwtDto=jwtService.getRefreshToken(refreshToken);
            String newJwtToken=jwtService.getNewJwtToken(jwtDto);
            System.out.println(newJwtToken+" 새 토큰");

            JSONObject jsonObject=new JSONObject();
            jsonObject.put("Authorization", newJwtToken);
            jsonObject.put("refreshToken", refreshToken);
            return jsonObject;
        }
        return null;
    }
}
