package com.example.blog_kim_s_token.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.blog_kim_s_token.model.csrf.csrfDao;
import com.example.blog_kim_s_token.model.csrf.csrfDto;
import com.example.blog_kim_s_token.model.user.userDao;
import com.example.blog_kim_s_token.model.user.userDto;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class jwtAuthorizationFilter  extends BasicAuthenticationFilter {

    private userDao dao;
    private jwtService jwtService;
    private csrfDao csrfDao;

    public jwtAuthorizationFilter(AuthenticationManager authenticationManager,userDao dao,jwtService jwtService,csrfDao csrfDao) {
        super(authenticationManager);
        this.dao=dao;
        this.jwtService=jwtService;
        this.csrfDao=csrfDao;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws IOException, ServletException {
        System.out.println("doFilterInternal 입장"+request.getHeader("Authorization"));
        System.out.println(request.getRequestURL()+"url");
        System.out.println(request.getHeader("REFERER")+"도메인");

        String uri=request.getRequestURI();
        if(request.getHeader("REFERER")==null){
            System.out.println("도메인이 없습니다"+uri);
            if(uri.equals("/auth/navercallback")){
                System.out.println("네이버 로그인 시도입니다");
            }
            else if(uri.equals("/auth/kakaocallback")){
                System.out.println("카카오 로그인 시도입니다");
            }
            else{
                System.out.println("도메인이 없습니다");
                return;
            }
        }
        else if(!request.getHeader("REFERER").equals("http://localhost:3030/")){
            System.out.println("도에민이 다릅니다"+request.getRequestURI()+request.getRequestURL());
            return;
        }

        if(request.getHeader("Authorization")==null||!request.getHeader("Authorization").startsWith("Bearer")){
            System.out.println("헤더 없음");
            chain.doFilter(request, response);
        }else{
            String jwtToken=request.getHeader("Authorization");
            if(jwtToken.startsWith("Bearer")){
                jwtToken=jwtToken.replace("Bearer ", "");
                System.out.println(jwtToken+"토큰받음");
                try {
                    int userid=jwtService.onpenJwtToken(jwtToken);
                    System.out.println(userid+"토큰해제");
                    
                    csrfDto csrfDto=csrfDao.findByUserId(userid);
                    String csrfToken=request.getHeader("csrfToken");
                    System.out.println(csrfToken+"csrfToken"+csrfDto.getCsrfToken());
                    if(csrfDto==null||!csrfToken.equals(csrfDto.getCsrfToken())){
                        System.out.println("csrf 토큰이 없거나 조작됨");
                        return;
                    }

                    userDto userDto=dao.findById(userid).orElseThrow(()->new RuntimeException("존재하지 않는 회원입니다"));
                    jwtService.setSecuritySession(jwtService.makeAuthentication(userDto));
                    
                   
                    chain.doFilter(request, response);   
                } catch (TokenExpiredException e) {
                    e.printStackTrace();
                    System.out.println("기간만료");
                    goToError("/auth/jwtex", request, response);
                }catch(JWTDecodeException e){
                    e.printStackTrace();
                    System.out.println("베리어 다음 토큰이 없음");
                    goToError("/auth/onlyBearer", request, response);
                }
            } 
        }
    }
    private void goToError(String errorUrl,HttpServletRequest request,HttpServletResponse response) {
        System.out.println("goToError");
        RequestDispatcher dp=request.getRequestDispatcher(errorUrl);
        try {
            dp.forward(request, response);
        } catch (ServletException | IOException e) {
            System.out.println("에러링크 존재 하지 않음");
            e.printStackTrace();
        } 
    }
    
}
