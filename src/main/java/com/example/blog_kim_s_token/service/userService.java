package com.example.blog_kim_s_token.service;


import com.example.blog_kim_s_token.config.security;
import com.example.blog_kim_s_token.enums.confirmEnums;
import com.example.blog_kim_s_token.enums.role;
import com.example.blog_kim_s_token.enums.userEnums;
import com.example.blog_kim_s_token.model.confrim.confrimDto;
import com.example.blog_kim_s_token.model.user.singupDto;
import com.example.blog_kim_s_token.model.user.userDao;
import com.example.blog_kim_s_token.model.user.userDto;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class userService {

    private final int yes=1;
    private final int no=0;

    @Autowired
    private userDao userDao;
    @Autowired
    private utillService utillService;
    @Autowired
    private security security;
    @Autowired
    private confrimService confrimService;

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
                        userDao.save(new userDto(0, singupDto.getEmail(), singupDto.getName(),security.pwdEncoder().encode(singupDto.getPwd()), role.USER.getValue(),singupDto.getPostcode(),singupDto.getAddress(), singupDto.getDetailAddress(), singupDto.getExtraAddress(), singupDto.getPhoneNum(),no, yes));
                        confrimService.deleteCofrim(confrimDto);
                        return utillService.makeJson(userEnums.sucSingUp.getBool(),userEnums.sucSingUp.getMessege());
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
            if(SecurityContextHolder.getContext().getAuthentication().getName()!=null){
                return sucLogin();
            }else{
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("로그인 실패");
            return failLogin();
        }
    }
    private JSONObject sucLogin() {
        return utillService.makeJson(userEnums.sucLogin.getBool(),userEnums.sucLogin.getMessege());
    }
    private JSONObject failLogin() {
        return utillService.makeJson(userEnums.failLogin.getBool(),userEnums.failLogin.getMessege());
    }
}
