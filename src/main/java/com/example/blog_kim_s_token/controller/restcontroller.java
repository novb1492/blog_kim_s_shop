package com.example.blog_kim_s_token.controller;




import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.blog_kim_s_token.model.confrim.emailCofrimDto;
import com.example.blog_kim_s_token.model.confrim.phoneCofrimDto;
import com.example.blog_kim_s_token.model.user.addressDto;
import com.example.blog_kim_s_token.model.user.singupDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.confrimService;
import com.example.blog_kim_s_token.service.userService;
import com.example.blog_kim_s_token.service.ApiServies.kakao.kakaoLoginservice;
import com.example.blog_kim_s_token.service.ApiServies.naver.naverLoginService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class restcontroller {

    @Autowired
    private userService userService;
    @Autowired
    private confrimService confrimService;
    @Autowired
    private naverLoginService naverLoingService;
    @Autowired
    private kakaoLoginservice kakaoLoginservice;

    @RequestMapping("/auth/confrimEmail")
    public boolean confrimEmail(HttpServletRequest request,HttpServletResponse response) {
        return userService.confrimEmail((String)request.getParameter("email"));
    }
    @RequestMapping("/auth/confrimPhoneNum")
    public boolean confrimPhoneNum(HttpServletRequest request,HttpServletResponse response) {
        return userService.confrimPhone((String)request.getParameter("phoneNum"));
    }
    @RequestMapping("/auth/sendSms")
    public JSONObject sendSms(HttpServletRequest request,HttpServletResponse response) {
        return confrimService.sendMessege(request);
    }
    @RequestMapping("/auth/cofrimSmsNum")
    public JSONObject cofrimSmsNum(@Valid @RequestBody phoneCofrimDto phoneCofrimDto,HttpServletResponse response) {
        return confrimService.cofrimTempNum(phoneCofrimDto);
    }
    @RequestMapping("/auth/insertUser")
    public JSONObject insertUser(@Valid @RequestBody singupDto singupDto) {
        return userService.insertUser(singupDto);
    }
    @RequestMapping("/login")
    public JSONObject login(HttpServletRequest request,HttpServletResponse response) {
        return userService.doLogin();
    }
    @RequestMapping("/auth/findEmail")
    public JSONObject findEmail(HttpServletRequest request,HttpServletResponse response) {
        return userService.findLostEmail(request.getParameter("phoneNum"));
    }
    @RequestMapping("/auth/sendEmail")
    public JSONObject sendEmail(HttpServletRequest request,HttpServletResponse response) {
        return confrimService.sendEmail(request.getParameter("email"));
    }
    @RequestMapping("/auth/cofrimEmailNum")
    public JSONObject cofrimEmailNum(@Valid @RequestBody emailCofrimDto emailCofrimDto,HttpServletResponse response) {
        return confrimService.confrimTempNum(emailCofrimDto);
    }
    @RequestMapping("/auth/naver")
    public String naverLogin() {
        return  naverLoingService.naverLogin();
    }
    @RequestMapping("/auth/kakao")
    public String kakaoLogin(HttpServletRequest request,HttpServletResponse response) {
        return kakaoLoginservice.kakaoGetCode();
    }
    @RequestMapping("/api/userInfor")
    public userDto getUserInfor(HttpServletRequest request,HttpServletResponse response) {
        return userService.sendUserDto();
    }
    @RequestMapping("/auth/jwtex")
    public void name(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("auth/jwtex");
        throw new TokenExpiredException(null);
    }
    @RequestMapping("/api/logout")
    public JSONObject logout(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("logout");
        return userService.logout(request,response);
    }
    @RequestMapping("/api/updateAddress")
    public JSONObject updateAddress(@Valid @RequestBody addressDto addressDto,HttpServletResponse response) {
        System.out.println("updateAddress");
        System.out.println(addressDto.getAddress());
        return null;
    }
    @RequestMapping("/auth/index2")
    public String hello2(@CookieValue(value = "refreshToken", required = false) Cookie rCookie,HttpServletResponse response) {
        System.out.println("index2");
        System.out.println(rCookie.getValue());
        return "index2";
    }

    @RequestMapping("/api/v1/user/test")
    public JSONObject  user(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("user 입장");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("role", "user");
        jsonObject.put("hello", "world");
        return jsonObject;
    }
    @RequestMapping("/api/v1/manage/test")
    public String  manage(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("manage 입장");
        return "manage";
    }
    @RequestMapping("/api/v1/admin/test")
    public String  admin(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("admin 입장");
        return "admin";
    }

    
}
