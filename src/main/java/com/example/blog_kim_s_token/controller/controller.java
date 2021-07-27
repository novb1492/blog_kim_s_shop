package com.example.blog_kim_s_token.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.blog_kim_s_token.service.ApiServies.naver.naverLoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class controller {

    @Autowired
    private naverLoginService naverLoingService;
    
    @RequestMapping("/auth/navercallback")
    public String naverLogin2(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("naverlogin요청");
        String token=naverLoingService.LoginNaver(naverLoingService.getNaverToken(request.getParameter("code"), request.getParameter("state")),request,response);
        return "redirect:http://localhost:3030/kim_s_Shop/index.jsp?token="+token;
    }
}
