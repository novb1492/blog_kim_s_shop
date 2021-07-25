package com.example.blog_kim_s_token.service;

import javax.servlet.http.HttpServletRequest;

import com.example.blog_kim_s_token.enums.cofirmEnums;
import com.example.blog_kim_s_token.enums.userEnums;
import com.example.blog_kim_s_token.model.confrim.confimDao;
import com.example.blog_kim_s_token.model.confrim.confrimDto;
import com.nimbusds.jose.shaded.json.JSONObject;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class cofrimService {

    private final int f=0;
    @Value("${jwt.refreshToken.validity}")
    private int coolTime=1;


    @Autowired
    private confimDao confimDao;
    @Autowired
    private coolSmsService coolSmsService;
    @Autowired
    private utillService utillService;
    @Autowired
    private userService userService;

    private confrimDto findConfrim(String phoneNum) {
        return confimDao.findByPhoneNum(phoneNum);
    }
    private void insertConfrim(String phoneNum,String tempNum){
        confimDao.save(new confrimDto(0, null, phoneNum, null,tempNum,f,f,1,null));
    }
    private void updateconfrim(confrimDto confrimDto,String tempNum) {
        System.out.println("updateconfrim"+tempNum+confrimDto.getPhoneNum());
        int requestTime=confrimDto.getRequestTime();
        confimDao.updatePhoneTempNum(tempNum,requestTime+=1, confrimDto.getPhoneNum());
    }
    private void deleteCofrim(confrimDto confrimDto){
        confimDao.delete(confrimDto);
    }
    public JSONObject sendMessege(HttpServletRequest request) {
        System.out.println("sendMessege 입장"+request.getParameter("phoneNum"));
        String phoneNum=request.getParameter("phoneNum");
        String tempNum=utillService.GetRandomNum(6);

        if(userService.confrimPhone(phoneNum)){
            confrimDto confrimDto=findConfrim(phoneNum);
            if(confrimDto==null){
                System.out.println("처음 인증요청");
                insertConfrim(phoneNum, tempNum);
                sendSms(phoneNum, tempNum);
            }
            else{
                System.out.println("요청 기록존재");
                if(utillService.checkDate(confrimDto.getCreated(),coolTime)){
                    System.out.println(utillService.checkDate(confrimDto.getCreated())+"여부");
                    deleteCofrim(confrimDto);
                    insertConfrim(phoneNum, tempNum);
                    sendSms(phoneNum, tempNum);
                }
                else{
                    if(confrimDto.getRequestTime()<=10){
                        updateconfrim(confrimDto, tempNum);
                        sendSms(phoneNum, tempNum);
                    }else{
                        return utillService.makeJson(cofirmEnums.tooManyTime.getBool(), cofirmEnums.tooManyTime.getMessege());  
                    }
                }
            }
            return utillService.makeJson(userEnums.sendSmsNum.getBool(), userEnums.sendSmsNum.getMessege());
        }
        return utillService.makeJson(userEnums.sendSmsNum.getBool(), userEnums.sendSmsNum.getMessege());//sendMessege(coolSmsDto.getPhoneNum(),"인증번호는 "+SmsNum+"입니다");
    }
    private void sendSms(String phoneNum,String tempNum){
        coolSmsService.sendMessege(phoneNum,"인증번호는 "+tempNum+"입니다");
    }
}
