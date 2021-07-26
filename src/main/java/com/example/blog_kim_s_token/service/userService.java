package com.example.blog_kim_s_token.service;

import javax.servlet.http.HttpSession;

import com.example.blog_kim_s_token.config.security;
import com.example.blog_kim_s_token.enums.confirmEnums;
import com.example.blog_kim_s_token.enums.role;
import com.example.blog_kim_s_token.enums.userEnums;
import com.example.blog_kim_s_token.model.user.singupDto;
import com.example.blog_kim_s_token.model.user.userDao;
import com.example.blog_kim_s_token.model.user.userDto;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class userService {
    @Autowired
    private userDao userDao;
    @Autowired
    private utillService utillService;
    @Autowired
    private security security;

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
    public JSONObject insertUser(singupDto singupDto,HttpSession httpSession) {
        if(confrimEmail(singupDto.getEmail())){
            if(confrimPhone(singupDto.getPhoneNum())){
                if(httpSession.getAttribute("insertPhone").equals(singupDto.getPhoneNum())){
                    if((boolean)httpSession.getAttribute("phoneCheck")){
                        if(singupDto.getPwd().equals(singupDto.getPwd2())){
                            userDto userDto=new userDto(0, singupDto.getEmail(), singupDto.getName(),security.pwdEncoder().encode(singupDto.getPwd()),role.USER.getValue(),singupDto.getPostcode(),singupDto.getAddress(),singupDto.getDetailAddress(),singupDto.getExtraAddress(),singupDto.getPhoneNum(),false,false);
                            userDao.save(userDto);
                            httpSession.removeAttribute("insertPhone");
                            httpSession.removeAttribute("insertRandNum");
                            httpSession.removeAttribute("phoneCheck");
                            return utillService.makeJson(userEnums.sucSingUp.getBool(), userEnums.sucSingUp.getMessege());
                        }
                        return utillService.makeJson(userEnums.notConfrimPhone.getBool(), userEnums.notConfrimPhone.getMessege());
                    }
                    return utillService.makeJson(userEnums.notEqualsPwd.getBool(), userEnums.notEqualsPwd.getMessege());
                }
              return utillService.makeJson(userEnums.notEqualsPhone.getBool(),userEnums.notEqualsPhone.getMessege());
            }
           return utillService.makeJson(confirmEnums.alreadyPhone.getBool(),confirmEnums.alreadyPhone.getMessege());
        }
        return utillService.makeJson(confirmEnums.alreadyEmail.getBool(), confirmEnums.alreadyEmail.getMessege());
    }
}
