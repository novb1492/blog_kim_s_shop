package com.example.blog_kim_s_token.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.blog_kim_s_token.service.ApiServies.kakao.kakaoLoginservice;
import com.example.blog_kim_s_token.service.ApiServies.kakao.kakaoService;
import com.example.blog_kim_s_token.service.ApiServies.naver.naverLoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class controller {

    @Autowired
    private naverLoginService naverLoingService;
    @Autowired
    private kakaoLoginservice kakaoLoginservice;
    @Autowired
    private kakaoService kakaoService;
    
    @RequestMapping("/auth/navercallback")
    public String naverRollback(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("naverlogin요청");
        naverLoingService.LoginNaver(naverLoingService.getNaverToken(request.getParameter("code"), request.getParameter("state")),request,response);
        return "redirect:http://localhost:3030/index.html";
    }
    @RequestMapping("/auth/kakaocallback")
    public String kakaoRollback(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("kakaologin요청");   
        kakaoService.kakaoLogin(request.getParameter("code"),response);
       return "redirect:http://localhost:3030/kakaoplusOkPage.html";

    }
    @RequestMapping("/auth/kakaocallback2")
    public String kakaocallback2(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("kakaocallback2"+request.getHeader("REFERER"));
        return "redirect:http://localhost:3030/index.html";

    }
 

    
}
