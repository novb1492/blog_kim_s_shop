package com.example.blog_kim_s_token.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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
        System.out.println("doFilterInternal 입장 ");
        System.out.println(request.getRequestURL()+" url");
        System.out.println(request.getHeader("REFERER")+" 도메인");
        String uri=request.getRequestURI();
        if(request.getHeader("REFERER")==null){
            System.out.println("도메인이 없습니다"+uri);
            if(uri.equals("/auth/navercallback")){
                System.out.println("네이버 로그인 시도입니다");
            }
            else if(uri.equals("/auth/kakaocallback")){
                System.out.println("카카오 로그인 시도입니다");
            }
            else if(uri.equals("/auth/payment")){
                System.out.println("결제 시스템입니다");
            } else if(uri.equals("/api/okKakaopay")){
                System.out.println("카카오결제 시스템입니다");
                goToError("/api/okKakaopay", request, response);
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
        String token=null;
        String csrfToken=null;
        try {
            Cookie[] cookies=request.getCookies();
            for(Cookie c:cookies){
                if(c.getName().equals("Authorization")){
                    token=c.getValue();
                }else if(c.getName().equals("csrfToken")){
                    csrfToken=c.getValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("토큰없음");
        }
        if(token==null){
            System.out.println("토큰 없음");
            chain.doFilter(request, response);
        }else{
            String jwtToken=token;
                System.out.println(jwtToken+"토큰받음");
                try {
                    int userid=jwtService.onpenJwtToken(jwtToken);
                    System.out.println(userid+"토큰해제");
                    
                    csrfDto csrfDto=csrfDao.findByUserId(userid);
                    System.out.println(csrfToken+"csrfToken"+csrfDto.getCsrfToken());
                    if(csrfDto==null||!csrfToken.equals(csrfDto.getCsrfToken())){
                        System.out.println("csrf 토큰이 없거나 조작됨");
                        throw new JWTDecodeException(null);
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
                    System.out.println("토큰변환실패");
                    goToError("/auth/failOpenToken", request, response);
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
