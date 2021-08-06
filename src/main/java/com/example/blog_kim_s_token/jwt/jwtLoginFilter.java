package com.example.blog_kim_s_token.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.blog_kim_s_token.config.principaldetail;
import com.example.blog_kim_s_token.model.jwt.jwtDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.cookie.cookieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class jwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    
    private jwtService jwtService;
    
    private cookieService cookieService;

    public jwtLoginFilter(jwtService jwtService,cookieService cookieService ){
        this.jwtService=jwtService;
        this.cookieService=cookieService;
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
        System.out.println("successfulAuthentication 입장");

        principaldetail principaldetail=(principaldetail)authResult.getPrincipal();
        int userId=principaldetail.getUserDto().getId();

        String jwtToken=jwtService.getJwtToken(userId);
        jwtDto jwtDto=jwtService.getRefreshToken(userId);
        String refreshToken=jwtService.getRefreshToken(jwtDto,userId);
        
        System.out.println(jwtToken);
        String[][] cookiesNamesAndValues=new String[2][2];
        cookiesNamesAndValues[0][0]="Authorization";
        cookiesNamesAndValues[0][1]=jwtToken;
        cookiesNamesAndValues[0][2]="httponly";
        cookiesNamesAndValues[1][0]="refreshToken";
        cookiesNamesAndValues[1][1]=refreshToken;
        cookiesNamesAndValues[1][2]="httponly";
        cookieService.cookieFactory(response, cookiesNamesAndValues);

        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,AuthenticationException failed) throws IOException, ServletException {
        System.out.println("로그인 실패");
        System.out.println(failed.getCause()+failed.getLocalizedMessage()+failed.getStackTrace()+failed.getSuppressed());
        RequestDispatcher dp=request.getRequestDispatcher("/login");
		dp.forward(request, response);

    }
}
