package com.example.blog_kim_s_token.controller;





import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.blog_kim_s_token.model.article.articleDto;
import com.example.blog_kim_s_token.model.article.getArticleDto;
import com.example.blog_kim_s_token.model.article.insertArticleDto;
import com.example.blog_kim_s_token.model.confrim.emailCofrimDto;
import com.example.blog_kim_s_token.model.confrim.phoneCofrimDto;
import com.example.blog_kim_s_token.model.payment.getVankDateDto;
import com.example.blog_kim_s_token.model.payment.tryCanclePayDto;
import com.example.blog_kim_s_token.model.product.getPriceDto;
import com.example.blog_kim_s_token.model.reservation.getDateDto;
import com.example.blog_kim_s_token.model.reservation.getTimeDto;
import com.example.blog_kim_s_token.model.user.addressDto;
import com.example.blog_kim_s_token.model.user.phoneDto;
import com.example.blog_kim_s_token.model.user.pwdDto;
import com.example.blog_kim_s_token.model.user.singupDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.boardService;
import com.example.blog_kim_s_token.service.priceService;
import com.example.blog_kim_s_token.service.userService;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.ApiServies.kakao.kakaoLoginservice;
import com.example.blog_kim_s_token.service.ApiServies.kakao.kakaoService;
import com.example.blog_kim_s_token.service.ApiServies.kakao.kakaopayService;
import com.example.blog_kim_s_token.service.ApiServies.kakao.tryKakaoPayDto;
import com.example.blog_kim_s_token.service.ApiServies.naver.naverLoginService;
import com.example.blog_kim_s_token.service.confrim.confrimService;
import com.example.blog_kim_s_token.service.fileUpload.fileUploadService;
import com.example.blog_kim_s_token.service.payment.paymentService;
import com.example.blog_kim_s_token.service.payment.iamPort.tryImpPayDto;
import com.example.blog_kim_s_token.service.reservation.resevationService;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



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
    private kakaopayService kakaopayService;
    @Autowired
    private kakaoService kakaoService;
    @Autowired
    private resevationService resevationService;
    @Autowired
    private priceService priceService;
    @Autowired
    private fileUploadService fileUploadService;
    @Autowired
    private paymentService paymentService;
    @Autowired
    private boardService boardService;


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
        return kakaoService.kakaoGetLoginCode();
    }
    @PostMapping("/api/userInfor")
    public userDto getUserInfor(HttpServletRequest request,HttpServletResponse response) {
        return userService.sendUserDto();
    }
    @PostMapping("/api/email")
    public JSONObject getEmail(HttpServletRequest request,HttpServletResponse response) {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("email", userService.sendUserDto().getEmail());
        return jsonObject;
    }
    @RequestMapping("/auth/jwtex")
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
    @PostMapping("/auth/failOpenToken")
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
    @PostMapping("/api/buyImp")
    public JSONObject buyImp(@Valid @RequestBody tryImpPayDto tryImpPayDto,HttpServletRequest request,HttpServletResponse response){
        System.out.println("buyImp");
        return paymentService.confrimPayment(tryImpPayDto,request);
       
    }
    @PostMapping("/api/getPrice")
    public JSONObject getPrice(@Valid@RequestBody getPriceDto getPriceDto,HttpServletResponse response) {
        System.out.println("getPrice");
        return priceService.responeTotalprice(getPriceDto);
    }
    @PostMapping("/api/getVankDate")
    public JSONObject getVankDate(@Valid @RequestBody getVankDateDto getVankDateDto,HttpServletResponse response) {
        System.out.println("getVankDate");
        return paymentService.getVbankDate(getVankDateDto);
    }
    @PostMapping("/api/getClientReservation")
    public JSONObject getClientReservation(@RequestBody JSONObject JSONObject,HttpServletResponse response) {
        System.out.println("getClientReservation");
       return resevationService.getClientReservation(JSONObject);
    }
    @PostMapping("/auth/payment")
    public void bootPay(@RequestBody JSONObject jsonObject,HttpServletResponse response) {
        System.out.println("payment");
        paymentService.vbankOk(jsonObject);
    }
    @PostMapping("/api/kakaopay")
    public JSONObject getKakaoPayLink(@Valid @RequestBody tryKakaoPayDto tryKakaoPayDto,HttpServletRequest request,HttpServletResponse response) {
        System.out.println("getKakaoPayLink");
         return kakaopayService.doKakaoPay(tryKakaoPayDto,request);
    }
    @RequestMapping("/api/okKakaopay")
    public JSONObject okKakaopay(HttpServletRequest request,HttpSession session,HttpServletResponse response) {
        System.out.println("okKakaopay");
        return kakaopayService.insertPaymentForkakao(request.getParameter("pg_token"),session);
    }
    @PostMapping("/api/canclePay")
    public JSONObject canclePay( @RequestBody tryCanclePayDto tryCanclePayDto,HttpServletRequest request,HttpServletResponse response) {
        System.out.println("canclePay"); 
        return  paymentService.canclePay(tryCanclePayDto);
    }
    @PostMapping("/api/imageUpload")
    public JSONObject imageUpload(@RequestParam("file")MultipartFile multipartFile,HttpServletRequest request,HttpServletResponse response) {
        System.out.println("imageUpload"); 
        return fileUploadService.awsS3ImageUpload(multipartFile);
    }
    @PostMapping("/api/insertArticle")
    public JSONObject insertArticle(@Valid @RequestBody insertArticleDto insertArticleDto,HttpServletResponse response) {
        System.out.println("insertArticle"); 
        return boardService.insertArticle(insertArticleDto);
    }
    @PostMapping("/api/getArticle")
    public articleDto getArticle(@Valid @RequestBody getArticleDto getArticleDto,HttpServletResponse response) {
        System.out.println("getArticle"); 
        return boardService.getArticle(getArticleDto);
    }
    @PostMapping("/api/getAllArticle")
    public articleDto getAllArticle(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("getAllArticle"); 
        getArticleDto getArticleDto=new getArticleDto();
        getArticleDto.setArticleId(1);
        return boardService.getArticle(getArticleDto);
    }
    @PostMapping("/api/kakaoMore")
    public JSONObject kakaoMore(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("kakaoMore");
        return utillService.makeJson(true, kakaoService.getMoreOk(request)); 
    
    }
    @RequestMapping("/api/sendKakaoMessage")
    public void sendKakaoMessage(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("sendKakaoMessage");
        kakaoService.sendMessege();
    }
    @RequestMapping("/auth/kakaocallback")
    public void kakaoRollback(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("kakaologin요청");   
        kakaoService.kakaoLogin(request.getParameter("code"),response);
        doRedirect(response, "http://localhost:3030/kakaoplusOkPage.html");
    }
    @RequestMapping("/auth/kakaocallback2")
    public void kakaocallback2(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("kakaocallback2"+request.getHeader("REFERER"));
        doRedirect(response, "http://localhost:3030/kakaoPlusOkDetailPage.html");

    }
    @RequestMapping("/auth/navercallback")
    public void naverRollback(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("naverlogin요청");
        naverLoingService.LoginNaver(naverLoingService.getNaverToken(request.getParameter("code"), request.getParameter("state")),request,response);
        doRedirect(response, "http://localhost:3030/doneNaverLogin.html");
    }
    @PostMapping("/auth/test")
    public JSONObject test(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("test");
        Cookie[] cookies=request.getCookies();
        if(cookies==null){
            Cookie cookie=new Cookie("test","123");
            response.addCookie(cookie);
            return null;
        }else{
            System.out.println("기존쿠키 교체");
            Cookie cookie=new Cookie("test","456");
            response.addCookie(cookie);
        }
        if(cookies!=null){
            for(Cookie c:cookies){
                if(c.getName().equals("test")){
                    System.out.println(c.getValue());
                }
            }
         }
        return null;
    }
    @PostMapping("/auth/test2")
    public void test2(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("test2");
        Cookie[] cookies=request.getCookies();
        if(cookies!=null){
           for(Cookie c:cookies){
               if(c.getName().equals("test")){
                   System.out.println(c.getValue());
               }
           }
        }
    
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
    private void doRedirect(HttpServletResponse response,String url) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("doRedirect error"+e.getMessage());
        }
    }

    
}
