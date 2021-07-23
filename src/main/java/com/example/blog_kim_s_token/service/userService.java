package com.example.blog_kim_s_token.service;

import com.example.blog_kim_s_token.config.security;
import com.example.blog_kim_s_token.enums.userEnums;
import com.example.blog_kim_s_token.model.user.loginDto;
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
    public JSONObject  insertUser(loginDto loginDto) {
        if(confrimEmail(loginDto.getEmail())){
            if(loginDto.getPwd().equals(loginDto.getPwd2())){
                userDto userDto=new userDto(0, loginDto.getEmail(), loginDto.getName(),security.pwdEncoder().encode(loginDto.getPwd()), "ROLE_USER");
                userDao.save(userDto);
                return utillService.makeJson(userEnums.sucSingUp.getBool(), userEnums.sucSingUp.getMessege());
            }
        }
        return utillService.makeJson(userEnums.sucSingUp.getBool(), userEnums.sucSingUp.getMessege());
    }
}
