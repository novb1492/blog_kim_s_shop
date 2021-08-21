package com.example.blog_kim_s_token.controller;




import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.blog_kim_s_token.model.confrim.emailCofrimDto;
import com.example.blog_kim_s_token.model.confrim.phoneCofrimDto;
import com.example.blog_kim_s_token.model.price.seatPriceDto;
import com.example.blog_kim_s_token.model.reservation.getDateDto;
import com.example.blog_kim_s_token.model.reservation.getTimeDto;
import com.example.blog_kim_s_token.model.reservation.reservationInsertDto;
import com.example.blog_kim_s_token.model.user.addressDto;
import com.example.blog_kim_s_token.model.user.phoneDto;
import com.example.blog_kim_s_token.model.user.pwdDto;
import com.example.blog_kim_s_token.model.user.singupDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.priceService;
import com.example.blog_kim_s_token.service.userService;
import com.example.blog_kim_s_token.service.ApiServies.kakao.kakaoLoginservice;
import com.example.blog_kim_s_token.service.ApiServies.naver.naverLoginService;
import com.example.blog_kim_s_token.service.confrim.confrimService;
import com.example.blog_kim_s_token.service.reservation.resevationService;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @Autowired
    private resevationService resevationService;
    @Autowired
    private priceService priceService;

    @PostMapping("/auth/confrimEmail")
    public boolean confrimEmail(HttpServletRequest request,HttpServletResponse response) {
        return userService.confrimEmail((String)request.getParameter("email"));
    }
    @PostMapping("/auth/confrimPhoneNum")
    public boolean confrimPhoneNum(HttpServletRequest request,HttpServletResponse response) {
        return userService.confrimPhone((String)request.getParameter("phoneNum"));
    }
    @PostMapping("/auth/sendSms")
    public JSONObject sendSms(HttpServletRequest request,HttpServletResponse response) {
        return confrimService.sendPhone(request);
    }
    @PostMapping("/auth/cofrimSmsNum")
    public JSONObject cofrimSmsNum(@Valid @RequestBody phoneCofrimDto phoneCofrimDto,HttpServletResponse response) {
        return confrimService.cofrimTempNum(phoneCofrimDto);
    }
    @PostMapping("/auth/insertUser")
    public JSONObject insertUser(@Valid @RequestBody singupDto singupDto) {
        return userService.insertUser(singupDto);
    }
    @PostMapping("/login")
    public JSONObject login(HttpServletRequest request,HttpServletResponse response) {
        return userService.doLogin();
    }
    @PostMapping("/auth/findEmail")
    public JSONObject findEmail(HttpServletRequest request,HttpServletResponse response) {
        return userService.findLostEmail(request.getParameter("phoneNum"));
    }
    @PostMapping("/auth/sendEmail")
    public JSONObject sendEmail(HttpServletRequest request,HttpServletResponse response) {
        return confrimService.sendEmail(request.getParameter("email"));
    }
    @PostMapping("/auth/sendTempPwd")
    public JSONObject sendTempPwd(@Valid @RequestBody emailCofrimDto emailCofrimDto,HttpServletResponse response) {
        return confrimService.sendTempPwd(emailCofrimDto);
    }
    @PostMapping("/auth/naver")
    public String naverLogin() {
        return  naverLoingService.naverLogin();
    }
    @PostMapping("/auth/kakao")
    public String kakaoLogin(HttpServletRequest request,HttpServletResponse response) {
        return kakaoLoginservice.kakaoGetCode();
    }
    @PostMapping("/api/userInfor")
    public userDto getUserInfor(HttpServletRequest request,HttpServletResponse response) {
        return userService.sendUserDto();
    }
    @PostMapping("/auth/jwtex")
    public void TokenExpired() {
        System.out.println("auth/jwtex");
        throw new TokenExpiredException(null);
    }
    @PostMapping("/api/logout")
    public JSONObject logout(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("logout");
        return userService.logout(request,response);
    }
    @PostMapping("/api/updateAddress")
    public JSONObject updateAddress(@Valid @RequestBody addressDto addressDto,HttpServletResponse response) {
        System.out.println("updateAddress");
        return  userService.updateAddress(addressDto);
    }
    @PostMapping("/api/updatePhoneNum")
    public JSONObject changePhoneNum(@Valid @RequestBody phoneDto phoneDto,HttpServletResponse response) {
        System.out.println("updatePhoneNum");
        return userService.updatephoneNum(phoneDto);
    }
    @PostMapping("/api/updatePwd")
    public JSONObject changePhoneNum(@Valid @RequestBody pwdDto pwdDto,HttpServletResponse response) {
        System.out.println("updatePwd");
        return userService.updatePwd(pwdDto);
    }
    @PostMapping("/auth/onlyBearer")
    public void onlyBearer() {
        throw new JWTDecodeException(null);
    }
    @PostMapping("/api/getDateBySeat")
    public JSONObject getDateBySeat(@Valid @RequestBody getDateDto getDateDto,HttpServletResponse response) {
        System.out.println("getDateBySeat");
        return resevationService.getDateBySeat(getDateDto);
    }
    @PostMapping("/api/getTimeByDate")
    public JSONObject getTimeByDate(@Valid @RequestBody getTimeDto getTimeDto,HttpServletResponse response) {
        System.out.println("getTimeByDate");
        return resevationService.getTimeByDate(getTimeDto);
    }
    @PostMapping("/api/insertReservation")
    public JSONObject insertReservation(@Valid @RequestBody reservationInsertDto reservationInsertDto,HttpServletResponse response) {
        System.out.println("insertReservation");
       return resevationService.confrimPayment(reservationInsertDto);
    }
    @PostMapping("/api/getPrice")
    public JSONObject getPrice(@RequestBody seatPriceDto seatPriceDto,HttpServletResponse response) {
        System.out.println("getPrice");
       return priceService.getTotalSeatPrice(seatPriceDto);
    }
    @PostMapping("/auth/payment")
    public void bootPay(@RequestBody JSONObject jsonObject,HttpServletResponse response) {
        System.out.println("bootPay");
        System.out.println(jsonObject.get("imp_uid"));
        System.out.println(jsonObject+" bootPay");
       
    }
    @PostMapping("/auth/index2")
    public String hello2(@CookieValue(value = "refreshToken", required = false) Cookie rCookie,HttpServletResponse response) {
        System.out.println("index2");
        System.out.println(rCookie.getValue()); 
        return "index2";
    }

    @PostMapping("/api/v1/user/test")
    public JSONObject  user(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("user 입장");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("role", "user");
        jsonObject.put("hello", "world");
        return jsonObject;
    }
    @PostMapping("/api/v1/manage/test")
    public String  manage(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("manage 입장");
        return "manage";
    }
    @PostMapping("/api/v1/admin/test")
    public String  admin(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("admin 입장");
        return "admin";
    }

    
}
