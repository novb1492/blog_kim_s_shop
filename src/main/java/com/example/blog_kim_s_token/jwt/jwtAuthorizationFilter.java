package com.example.blog_kim_s_token.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.blog_kim_s_token.model.user.userDao;
import com.example.blog_kim_s_token.model.user.userDto;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class jwtAuthorizationFilter  extends BasicAuthenticationFilter {

    private userDao dao;
    private jwtService jwtService;

    public jwtAuthorizationFilter(AuthenticationManager authenticationManager,userDao dao,jwtService jwtService) {
        super(authenticationManager);
        this.dao=dao;
        this.jwtService=jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws IOException, ServletException {
        System.out.println("doFilterInternal 입장"+request.getHeader("Authorization"));
        if(request.getHeader("Authorization")==null||!request.getHeader("Authorization").startsWith("Bearer")){
            System.out.println("헤더 없음");
            chain.doFilter(request, response);
        }else{
            String jwtToken=request.getHeader("Authorization");
            System.out.println( request.getHeader("REFERER")+"도메인");
            if(jwtToken.startsWith("Bearer")){
                jwtToken=jwtToken.replace("Bearer ", "");
                System.out.println(jwtToken+"토큰받음");
                try {
                    int userid=jwtService.onpenJwtToken(jwtToken);
                    System.out.println(userid+"토큰해제");
 
                    userDto userDto=dao.findById(userid).orElseThrow(()->new RuntimeException("존재하지 않는 회원입니다"));
                    jwtService.setSecuritySession(jwtService.makeAuthentication(userDto));
            
                    chain.doFilter(request, response);   
                } catch (TokenExpiredException e) {
                    e.printStackTrace();
                    System.out.println("기간만료");
                    RequestDispatcher dp=request.getRequestDispatcher("/auth/jwtex");
		            dp.forward(request, response);
                }
            } 
        }
    }
    
}
