package com.example.blog_kim_s_token.service;

import javax.servlet.http.HttpServletRequest;

import com.example.blog_kim_s_token.enums.confirmEnums;
import com.example.blog_kim_s_token.model.confrim.confimDao;
import com.example.blog_kim_s_token.model.confrim.confrimDto;
import com.example.blog_kim_s_token.model.confrim.phoneCofrimDto;
import com.nimbusds.jose.shaded.json.JSONObject;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class confrimService {

    @Value("${confrim.coolTime}")
    private int coolTime;
    
    @Value("${confrim.overTime}")
    private int overTime;

    private final int f=0;
    private final int t=1;

    @Autowired
    private confimDao confimDao;
    @Autowired
    private coolSmsService coolSmsService;
    @Autowired
    private utillService utillService;


    public confrimDto findConfrim(String phoneNum) {
        return confimDao.findByPhoneNum(phoneNum);
    }
    private void insertConfrim(String phoneNum,String tempNum){
        confimDao.save(new confrimDto(0, null, phoneNum, null,tempNum,f,f,1,null));
    }
    private void updateconfrim(confrimDto confrimDto,String tempNum) {
        System.out.println("updateconfrim"+tempNum+confrimDto.getPhoneNum());
        int requestTime=confrimDto.getRequestTime();
        confimDao.updatePhoneTempNum(tempNum,requestTime+=1,utillService.getNowTimestamp(),confrimDto.getPhoneNum());
    }
    public void deleteCofrim(confrimDto confrimDto){
        confimDao.delete(confrimDto);
    }
    public void deleteCofrim(String phoneNum){
        confimDao.deleteByPhoneNum(phoneNum);
    }
    private void sendSms(String phoneNum,String tempNum){
        coolSmsService.sendMessege(phoneNum,"인증번호는 "+tempNum+"입니다");
    }
    public JSONObject sendMessege(HttpServletRequest request) {
        System.out.println("sendMessege 입장"+request.getParameter("phoneNum"));
        String phoneNum=request.getParameter("phoneNum");
        String tempNum=utillService.GetRandomNum(6);
        if(phoneNum!=null){
                confrimDto confrimDto=findConfrim(phoneNum);
                if(confrimDto==null){
                    System.out.println("처음 인증요청"); 
                    insertConfrim(phoneNum, tempNum);
                    //sendSms(phoneNum, tempNum);
                }
                else{
                    System.out.println("요청 기록존재");
                    if(utillService.checkDate(confrimDto.getCreated(),coolTime)){
                        System.out.println(utillService.checkDate(confrimDto.getCreated())+"여부");
                        deleteCofrim(confrimDto);
                        insertConfrim(phoneNum, tempNum);
                        //sendSms(phoneNum, tempNum);
                    }
                    else{
                        if(confrimDto.getRequestTime()<=10){
                            updateconfrim(confrimDto, tempNum);
                            //sendSms(phoneNum, tempNum);
                        }else{
                            return utillService.makeJson(confirmEnums.tooManyTime.getBool(), confirmEnums.tooManyTime.getMessege());  
                        }
                    }
                }
                return utillService.makeJson(confirmEnums.sendSmsNum.getBool(), confirmEnums.sendSmsNum.getMessege());
        }
        return utillService.makeJson(confirmEnums.nullPhoneNum.getBool(), confirmEnums.nullPhoneNum.getMessege());
    }
    public JSONObject cofrimSmsNum(phoneCofrimDto phoneCofrimDto) {
        System.out.println("cofrimSmsNum 제출 "+phoneCofrimDto.getTempNum()+phoneCofrimDto.getPhoneNum());
        confrimDto confrimDto=confimDao.findByPhoneNum(phoneCofrimDto.getPhoneNum());
        if(confrimDto!=null){
            if(confrimDto.getPhoneNum().equals(phoneCofrimDto.getPhoneNum())){
                if(utillService.checkTime(confrimDto.getCreated(),overTime)==false){
                    if(confrimDto.getPhoneTempNum().equals(phoneCofrimDto.getTempNum().trim())){
                        confimDao.updatePhoneCheckTrue(t, phoneCofrimDto.getPhoneNum());
                        return utillService.makeJson(confirmEnums.EqulsTempNum.getBool(), confirmEnums.EqulsTempNum.getMessege());
                    }
                    return utillService.makeJson(confirmEnums.notEqulsTempNum.getBool(), confirmEnums.notEqulsTempNum.getMessege());
                }
               return utillService.makeJson(confirmEnums.overTime.getBool(), confirmEnums.overTime.getMessege());
            }
           return utillService.makeJson(confirmEnums.notEqulsPhoneNum.getBool(), confirmEnums.notEqulsPhoneNum.getMessege());
        }
        return utillService.makeJson(confirmEnums.nullPhoneNumInDb.getBool(),confirmEnums.nullPhoneNumInDb.getMessege());
    }
}
