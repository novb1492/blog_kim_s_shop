package com.example.blog_kim_s_token.service.confrim;

import javax.servlet.http.HttpServletRequest;

import com.example.blog_kim_s_token.enums.confirmEnums;
import com.example.blog_kim_s_token.model.confrim.confrimDao;
import com.example.blog_kim_s_token.model.confrim.confrimDto;
import com.example.blog_kim_s_token.model.confrim.emailCofrimDto;
import com.example.blog_kim_s_token.model.confrim.phoneCofrimDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.coolSmsService;
import com.example.blog_kim_s_token.service.sendEmailService;
import com.example.blog_kim_s_token.service.userService;
import com.example.blog_kim_s_token.service.utillService;
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
    private final int tempNumLength=6;
    private final int tempPwdLength=8;

    @Autowired
    private confrimDao confimDao;
    @Autowired
    private userService userService;
    @Autowired
    private sendEmailService sendEmailService;


    public confrimDto findConfrim(String phoneNum) {
        return confimDao.findByPhoneNum(phoneNum);
    }
    private confrimDto findConfrimEmai(String email){
        return confimDao.findByEmail(email);
    }
    private void insertConfrim(String phoneNum,String email,String emailTempNum,String phoneTempNum){
        confimDao.save(new confrimDto(0,email, phoneNum,emailTempNum,phoneTempNum,f,f,1,null));
    }
    private void updateconfrim(confrimDto confrimDto,String tempNum) {
        System.out.println("updateconfrim"+tempNum+confrimDto.getPhoneNum());
        int requestTime=confrimDto.getRequestTime();
        confimDao.updatePhoneTempNum(tempNum,requestTime+=1,utillService.getNowTimestamp(),confrimDto.getPhoneNum());
    }
    private void updateconfrimEmail(confrimDto confrimDto,String tempNum) {
        System.out.println("updateconfrimEmail"+tempNum+confrimDto.getEmail());
        int requestTime=confrimDto.getRequestTime();
        confimDao.updateEmailTempNum(tempNum,requestTime+=1,utillService.getNowTimestamp(),confrimDto.getEmail());
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
        String tempNum=utillService.GetRandomNum(tempNumLength);
        if(phoneNum!=null){
                confrimDto confrimDto=findConfrim(phoneNum);
                if(confrimDto==null){
                    System.out.println("처음 인증요청"); 
                    insertConfrim(phoneNum,null,null,tempNum);
                    //sendSms(phoneNum, tempNum);
                }
                else{
                    System.out.println("요청 기록존재");
                    if(utillService.checkDate(confrimDto.getCreated(),coolTime)){
                        System.out.println(utillService.checkDate(confrimDto.getCreated())+"여부");
                        deleteCofrim(confrimDto);
                        insertConfrim(phoneNum,null,null,tempNum);
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
    public JSONObject cofrimTempNum(phoneCofrimDto phoneCofrimDto) {
        System.out.println("cofrimTempNum 제출 "+phoneCofrimDto.getTempNum()+phoneCofrimDto.getPhoneNum());
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
    public JSONObject sendEmail(String email) {
        userDto userDto=userService.findEmail(email);
        String tempNum=utillService.GetRandomNum(tempNumLength);
        if(userDto!=null){
            confrimDto confrimDto=findConfrimEmai(email);
            if(confrimDto==null){
                System.out.println("처음 인증요청"); 
                insertConfrim(null, email,tempNum,null);
            }else{
                System.out.println("요청기록 존재"); 
                updateconfrimEmail(confrimDto, tempNum);
            }
            sendEmailService.sendEmail(email,"안녕하세요 kim's Shop입니다","인증번호는 "+tempNum+" 입니다.");
            return utillService.makeJson(confirmEnums.sendEmail.getBool(), confirmEnums.sendEmail.getMessege());
        }
        return utillService.makeJson(confirmEnums.notFindEmail.getBool(), confirmEnums.notFindEmail.getMessege());

    }
    public void updateconfrimEmail(String email){
            System.out.println("updateconfrimEmail 입장 이메일인증 완료");
            confimDao.updateEmailCheckTrue(t, email);
    }
    public JSONObject sendTempPwd(emailCofrimDto emailCofrimDto) {
        System.out.println("sendTempPwd");
        confrimDto confrimDto=confimDao.findByEmail(emailCofrimDto.getEmail());
        confrimInterface confrimInterface=new emailConfrim(confrimDto);
        JSONObject result=compareTempNum(confrimInterface,emailCofrimDto.getTempNum());
        if((boolean) result.get("bool")==false){
            return result;
        }
        String tempPwd=utillService.GetRandomNum(tempPwdLength);
                        System.out.println(tempPwd+"임시비밀번호");
                        userService.updatePwd(confrimDto.getEmail(),tempPwd);
                        deleteCofrim(confrimDto);
                        sendEmailService.sendEmail(confrimDto.getEmail(),"안녕하세요 kim's Shop입니다","임시비밀번호는 "+tempPwd+" 입니다.");
        return utillService.makeJson(true, "임시 비밀번호를 메일로 보내드렸습니다");
    }
    public JSONObject compareTempNum(confrimInterface confrimInterface,String requestTempNum) {
        System.out.println("confrimTempNum 입장");
        if(confrimInterface.isNULL()){
            return utillService.makeJson(confirmEnums.notReuestConfrim.getBool(), confirmEnums.notReuestConfrim.getMessege());
        }
        if(utillService.checkTime(confrimInterface.getCreated(),overTime)){
            return utillService.makeJson(confirmEnums.overTime.getBool(), confirmEnums.overTime.getMessege()); 
        }
        if(!requestTempNum.trim().equals(confrimInterface.TempNumAtDb())){
            return utillService.makeJson(confirmEnums.notEqulsTempNum.getBool(), confirmEnums.notEqulsTempNum.getMessege());
        }
        return utillService.makeJson(confirmEnums.EqulsTempNum.getBool(),confirmEnums.EqulsTempNum.getMessege());  
       
    }
    /*public JSONObject confrimTempNum(emailCofrimDto emailCofrimDto) {
        System.out.println("confrimTempNum Email 입장");
            confrimDto confrimDto=confimDao.findByEmail(emailCofrimDto.getEmail());
            if(confrimDto!=null){
                if(utillService.checkTime(confrimDto.getCreated(),overTime)==false){
                    if(emailCofrimDto.getTempNum().trim().equals(confrimDto.getEmailTempNum())){
                        String tempPwd=utillService.GetRandomNum(tempPwdLength);
                        userService.updatePwd(confrimDto.getEmail(),tempPwd);
                        deleteCofrim(confrimDto);
                        sendEmailService.sendEmail(confrimDto.getEmail(),"안녕하세요 kim's Shop입니다","임시비밀번호는 "+tempPwd+" 입니다.");
                        return utillService.makeJson(confirmEnums.EqulsTempNum.getBool(),"임시비밀번호를 발송해 드렸습니다");
                    }
                    return utillService.makeJson(confirmEnums.notEqulsTempNum.getBool(), confirmEnums.notEqulsTempNum.getMessege());
                }
                return utillService.makeJson(confirmEnums.overTime.getBool(), confirmEnums.overTime.getMessege());
            }
            return utillService.makeJson(confirmEnums.notReuestConfrim.getBool(), confirmEnums.notReuestConfrim.getMessege());
    }*/

         
       
    
    
}
