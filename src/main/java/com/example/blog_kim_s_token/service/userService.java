package com.example.blog_kim_s_token.service;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.example.blog_kim_s_token.config.security;
import com.example.blog_kim_s_token.enums.confirmEnums;
import com.example.blog_kim_s_token.enums.role;
import com.example.blog_kim_s_token.enums.userEnums;
import com.example.blog_kim_s_token.jwt.jwtService;
import com.example.blog_kim_s_token.model.confrim.confrimDto;
import com.example.blog_kim_s_token.model.user.addressDto;
import com.example.blog_kim_s_token.model.user.phoneDto;
import com.example.blog_kim_s_token.model.user.pwdDto;
import com.example.blog_kim_s_token.model.user.singupDto;
import com.example.blog_kim_s_token.model.user.userDao;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.confrim.confrimInterface;
import com.example.blog_kim_s_token.service.confrim.confrimService;
import com.example.blog_kim_s_token.service.confrim.phoneConfrim;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class userService {

    private final int yes=1;
    @Value("${jwt.refreshToken.name}")
    private String refreshTokenName;
    
    @Autowired
    private userDao userDao;
    @Autowired
    private security security;
    @Autowired
    private confrimService confrimService;
    @Autowired
    private jwtService jwtService;
    @Autowired
    private csrfTokenService csrfTokenService;


    public boolean confrimEmail(String email) {
        if(findEmail(email)==null){
            return true;
        }
        return false;
    }
    public userDto findEmail(String email) {
        System.out.println("findEmail 조회 이메일 "+email);
        return userDao.findByEmail(email);
    }
    public boolean confrimPhone(String phoneNum) {
            if(userDao.findByPhoneNum(phoneNum)==null){
                return true;
            }
            return false;
    }
    public JSONObject insertUser(singupDto singupDto) {
        confrimDto confrimDto=confrimService.findConfrim(singupDto.getPhoneNum());
       if(confrimDto!=null){
            if(confrimDto.getPhoneNum().equals(singupDto.getPhoneNum())){
                if(confrimDto.getPhoneCheck()==yes){
                    if(confrimEmail(singupDto.getEmail())){
                        if(singupDto.getPwd().equals(singupDto.getPwd2())){
                            userDao.save(new userDto(singupDto.getEmail(), singupDto.getName(),security.pwdEncoder().encode(singupDto.getPwd()), role.USER.getValue(),singupDto.getPostcode(),singupDto.getAddress(), singupDto.getDetailAddress(), singupDto.getExtraAddress(), singupDto.getPhoneNum()));
                            confrimService.deleteCofrim(confrimDto);
                            return utillService.makeJson(userEnums.sucSingUp.getBool(),userEnums.sucSingUp.getMessege());
                        }
                        return utillService.makeJson(confirmEnums.notEqualsPwd.getBool(),confirmEnums.notEqualsPwd.getMessege());
                    }
                    return utillService.makeJson(confirmEnums.alreadyEmail.getBool(),confirmEnums.alreadyEmail.getMessege());
                }
                return utillService.makeJson(confirmEnums.notTruePhoneCheck.getBool(), confirmEnums.notTruePhoneCheck.getMessege());
            }
            return utillService.makeJson(confirmEnums.notEqulsPhoneNum.getBool(), confirmEnums.notEqulsPhoneNum.getMessege());
       }
       return utillService.makeJson(confirmEnums.nullPhoneNumInDb.getBool(),confirmEnums.nullPhoneNumInDb.getMessege());
    }
    public JSONObject doLogin() {
        try {
            String email=SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("시큐리티 확인"+email);
            return utillService.makeJson(userEnums.sucLogin.getBool(),email);
        } catch (Exception e) {
            return utillService.makeJson(userEnums.failLogin.getBool(),userEnums.failLogin.getMessege());
        }
    }
    public JSONObject findLostEmail(String phoneNum) {
        userDto userDto=userDao.findByPhoneNum(phoneNum);
        confrimService.deleteCofrim(phoneNum);
        if(userDto==null){
            return utillService.makeJson(userEnums.failFindEmailByPheon.getBool(),userEnums.failFindEmailByPheon.getMessege());
        }
        return utillService.makeJson(true, userDto.getEmail());
    }
    public void updatePwd(String email,String pwd) {
        System.out.println("updatePwd 입장");
        userDao.updatePwd(security.pwdEncoder().encode(pwd), email);
    }
    public JSONObject logout(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("logout 입장");
         csrfTokenService.deleteCsrfToken(SecurityContextHolder.getContext().getAuthentication().getName());
        return jwtService.deleteRefreshToken(request.getHeader(refreshTokenName));
    }
    public userDto sendUserDto() {
        System.out.println("sendUserDto");
        userDto userDto=userDao.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        userDto.setPwd(null);
        return userDto;
    }
    public JSONObject updateAddress(addressDto addressDto) {
        System.out.println("updateAddress");
        try {
            userDto userDto=userDao.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            String email=userDto.getEmail();
            userDao.updateAddress(addressDto.getPostcode(), addressDto.getAddress(), addressDto.getDetailAddress(), addressDto.getExtraAddress(), email);
            return utillService.makeJson(userEnums.sucUpdateAddress.getBool(), userEnums.sucUpdateAddress.getMessege());
        } catch (Exception e) {
            System.out.println("updateAddress 에러발생");
            e.printStackTrace();
            throw new RuntimeException("updateAddress 에러발생");
        }  
    }
    @Transactional
    public JSONObject updatephoneNum(phoneDto phoneDto) {
        System.out.println("updatephoneNum");
        try {
            String phoneNum=phoneDto.getPhoneNum();
            confrimDto confrimDto=confrimService.findConfrim(phoneNum);
            confrimInterface confrimInterface=new phoneConfrim(confrimDto);
            confirmEnums result=confrimService.compareTempNum(confrimInterface,phoneDto.getTempNum());
            if(result.getBool()){
                confrimService.deleteCofrim(confrimDto);
                userDto dto=findEmail(SecurityContextHolder.getContext().getAuthentication().getName());
                dto.setPhoneNum(phoneNum);
                return utillService.makeJson(userEnums.sucUpdatePhone.getBool(), userEnums.sucUpdatePhone.getMessege());
            }
            return utillService.makeJson(result.getBool(), result.getMessege());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("updatephoneNum error");
            throw new RuntimeException("updatephoneNum 도중 에러가 발생했습니다");
        }
    }
    @Transactional
    public JSONObject updatePwd(pwdDto pwdDto) {
        System.out.println("updatePwd");
        try {
            userDto userDto=userDao.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            BCryptPasswordEncoder passwordEncoder=security.pwdEncoder();
            if(!passwordEncoder.matches(pwdDto.getNowPwd(), userDto.getPwd())){
                System.out.println("현재 비밀번호 불일치");
                return utillService.makeJson(userEnums.notEqualsPwd.getBool(), userEnums.notEqualsPwd.getMessege());
            }
            if(!pwdDto.getNewPwd().equals(pwdDto.getNewPwd2())){
                System.out.println("새 비밀번호 불일치");
                return utillService.makeJson(userEnums.notEqualsNpwd.getBool(), userEnums.notEqualsNpwd.getMessege());
            }
            System.out.println("비밀번호 변경");
            userDto.setPwd(passwordEncoder.encode(pwdDto.getNewPwd()));
            return utillService.makeJson(userEnums.sucUpdatePwd.getBool(),userEnums.sucUpdatePwd.getMessege());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("updatePwd error");
            throw new RuntimeException("updatePwd 에러발생");
        }
    }
    

}
