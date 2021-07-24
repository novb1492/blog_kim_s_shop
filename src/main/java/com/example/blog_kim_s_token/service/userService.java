package com.example.blog_kim_s_token.service;

import com.example.blog_kim_s_token.config.security;
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
    public JSONObject insertUser(singupDto singupDto) {
        if(confrimEmail(singupDto.getEmail())){
            if(singupDto.getPwd().equals(singupDto.getPwd2())){
                userDto userDto=new userDto(0, singupDto.getEmail(), singupDto.getName(),security.pwdEncoder().encode(singupDto.getPwd()),role.USER.getValue(),singupDto.getPostcode(),singupDto.getAddress(),singupDto.getDetailAddress(),singupDto.getExtraAddress(),singupDto.getPhoneNum());
                userDao.save(userDto);
                return utillService.makeJson(userEnums.sucSingUp.getBool(), userEnums.sucSingUp.getMessege());
            }
            return utillService.makeJson(userEnums.notEqualsPwd.getBool(), userEnums.notEqualsPwd.getMessege());
        }
        return utillService.makeJson(userEnums.alreadyEmail.getBool(), userEnums.alreadyEmail.getMessege());
    }
}
