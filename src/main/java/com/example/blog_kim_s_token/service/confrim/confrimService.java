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
    private final int maxOfday=3;

    @Autowired
    private confrimDao confrimDao;
    @Autowired
    private userService userService;
    @Autowired
    private sendEmailService sendEmailService;


    public confrimDto findConfrim(String phoneNum) {
        return confrimDao.findByPhoneNum(phoneNum);
    }
    public confrimDto findConfrimEmai(String email){
        return confrimDao.findByEmail(email);
    }
    public void insertConfrim(confrimInterface confrimInterface,String tempNum){
        confrimDto dto=confrimInterface.getDto();
        if(confrimInterface.unit().equals("phone")){
            dto.setRequestTime(1); 
            dto.setPhoneTempNum(tempNum);
        }else{
            dto.setEmailRequestTime(1);
            dto.setEmailTempNum(tempNum);
        }
        confrimDao.save(dto);
    }
    public void updateconfrim(confrimInterface confrimInterface,String tempNum) {
        System.out.println("updateconfrim");
        int requestTime=confrimInterface.getRequestTime();
        requestTime+=1;
        if(confrimInterface.unit().equals("phone")){
            confrimDao.updatePhoneTempNum(tempNum,requestTime,utillService.getNowTimestamp(),confrimInterface.valueOfUbit());
        }else{
            confrimDao.updateEmailTempNum(tempNum, requestTime, utillService.getNowTimestamp(), confrimInterface.valueOfUbit());
        }  
    }
    public void updateconfrimEmail(confrimDto confrimDto,String tempNum) {
        System.out.println("updateconfrimEmail"+tempNum+confrimDto.getEmail());
        int requestTime=confrimDto.getRequestTime();
        confrimDao.updateEmailTempNum(tempNum,requestTime+=1,utillService.getNowTimestamp(),confrimDto.getEmail());
    }
    public void deleteCofrim(confrimDto confrimDto){
        confrimDao.delete(confrimDto);
    }
    public void deleteCofrim(String phoneNum){
        confrimDao.deleteByPhoneNum(phoneNum);
    }
    public void sendSms(String phoneNum,String tempNum){
        coolSmsService.sendMessege(phoneNum,"인증번호는 "+tempNum+"입니다");
    }
    public JSONObject sendPhone(HttpServletRequest request) {
        System.out.println("sendMessege 입장"+request.getParameter("phoneNum"));
        String phoneNum=request.getParameter("phoneNum");
        if(phoneNum.isEmpty()||phoneNum==null){
            System.out.println("공백입니다");
            return utillService.makeJson(confirmEnums.nullPhoneNum.getBool(), confirmEnums.nullPhoneNum.getMessege());
        }
        confrimDto dto=findConfrim(phoneNum);
        if(dto==null){
            System.out.println("dto is null");
            dto=confrimDto.builder().phoneNum(phoneNum).build();
        }
        confrimInterface confrimInterface=new phoneConfrim(dto);
        String tempNum=utillService.GetRandomNum(tempNumLength);
        JSONObject result=sendSms(confrimInterface,tempNum);
        if((boolean)result.get("bool")){
           //sendSms(phoneNum, tempNum);
        };  
       return result;
    }
    public JSONObject sendSms(confrimInterface confrimInterface,String tempNum) {
            if(confrimInterface.getRequestTime()==0){
                System.out.println("처음 인증요청"); 
                insertConfrim(confrimInterface,tempNum);
            }
            else{
                System.out.println("요청 기록존재");
                if(utillService.checkDate(confrimInterface.getCreated(),coolTime)){
                    System.out.println(maxOfday+"회 초과후 쿨타임지남 or 그냥 하루지남");
                    deleteCofrim(confrimInterface.getDto());
                    insertConfrim(confrimInterface,tempNum);
                }
                else{
                    if(confrimInterface.getRequestTime()<maxOfday){
                        System.out.println(maxOfday+"회 이하");
                        updateconfrim(confrimInterface, tempNum);
                    }else{
                        System.out.println(maxOfday+"초과후 하루 안지남");
                        return utillService.makeJson(confirmEnums.tooManyTime.getBool(),"하루 "+maxOfday+"회 제한입니다");
                    }
                }
            }
        return utillService.makeJson(confirmEnums.sendSmsNum.getBool(), confirmEnums.sendSmsNum.getMessege());
    }
    public JSONObject cofrimTempNum(phoneCofrimDto phoneCofrimDto) {
        System.out.println("cofrimTempNum");
        confrimDto confrimDto=confrimDao.findByPhoneNum(phoneCofrimDto.getPhoneNum());
        confrimInterface confrimInterface=new phoneConfrim(confrimDto);
        JSONObject result=compareTempNum(confrimInterface,phoneCofrimDto.getTempNum());
        if((boolean) result.get("bool")==false){
            return result;
        }
        confrimDao.updatePhoneCheckTrue(t, phoneCofrimDto.getPhoneNum());
        return utillService.makeJson(confirmEnums.EqulsTempNum.getBool(), confirmEnums.EqulsTempNum.getMessege());
                   
    }
    public JSONObject sendEmail(String email) {
        System.out.println("sendEmail");
        userDto userDto=userService.findEmail(email);
        if(userDto==null){
            System.out.println("존재하지 않는 이메일");
            return utillService.makeJson(confirmEnums.notFindEmail.getBool(), confirmEnums.notFindEmail.getMessege());
        }
        String tempNum=utillService.GetRandomNum(tempNumLength);
        confrimDto dto=findConfrimEmai(email);
        if(dto==null){
            dto=confrimDto.builder().email(email).build();
        }
        confrimInterface confrimInterface=new emailConfrim(dto);
        JSONObject result=sendSms(confrimInterface, tempNum);
        if((boolean) result.get("bool")){
            sendEmailService.sendEmail(email,"안녕하세요 kim's Shop입니다","인증번호는 "+tempNum+" 입니다.");
        }
        return result;

    }
    public void updateconfrimEmail(String email){
        System.out.println("updateconfrimEmail 입장 이메일인증 완료");
        confrimDao.updateEmailCheckTrue(t, email);
    }
    public JSONObject sendTempPwd(emailCofrimDto emailCofrimDto) {
        System.out.println("sendTempPwd");
        confrimDto confrimDto=confrimDao.findByEmail(emailCofrimDto.getEmail());
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
}
