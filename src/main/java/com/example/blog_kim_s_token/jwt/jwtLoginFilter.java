package com.example.blog_kim_s_token.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.blog_kim_s_token.config.principaldetail;
import com.example.blog_kim_s_token.model.jwt.jwtDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class jwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    
    private jwtService jwtService;

    public jwtLoginFilter(jwtService jwtService ){
        this.jwtService=jwtService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)throws AuthenticationException {
        System.out.println("로그인요청 attemptAuthentication  ");
        try {
  
            ObjectMapper objectMapper=new ObjectMapper();
            userDto userDto=objectMapper.readValue(request.getInputStream(), userDto.class);
            System.out.println(userDto);
            
            Authentication authentication=jwtService.confrimAuthenticate(userDto);
            jwtService.setSecuritySession(authentication);
            
            System.out.println("로그인완료"+authentication.getName());

            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,Authentication authResult) throws IOException, ServletException {
        System.out.println("토큰 제작시작");

        principaldetail principaldetail=(principaldetail)authResult.getPrincipal();
        String jwtToken=jwtService.getJwtToken(principaldetail.getUserDto().getId());
        jwtDto jwtDto=jwtService.getRefreshToken(principaldetail.getUserDto().getId());
        String refreshToken=jwtService.getRefreshToken(jwtDto,principaldetail.getUserDto().getId());
        
        Cookie cookie=new Cookie("refreshToken",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
    
        response.addCookie(cookie);
        response.setHeader("Authorization", "Bearer "+jwtToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,AuthenticationException failed) throws IOException, ServletException {
        System.out.println("로그인 실패");
    }
}
