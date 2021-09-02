package com.example.blog_kim_s_token.service.payment;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import com.example.blog_kim_s_token.customException.failBuyException;
import com.example.blog_kim_s_token.model.payment.getVankDateDto;
import com.example.blog_kim_s_token.model.payment.paidDao;
import com.example.blog_kim_s_token.model.payment.paidDto;
import com.example.blog_kim_s_token.model.payment.vBankDto;
import com.example.blog_kim_s_token.model.payment.vbankDao;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.payment.iamPort.iamportService;
import com.example.blog_kim_s_token.service.payment.iamPort.nomalPayment;
import com.example.blog_kim_s_token.service.payment.iamPort.vbankPayment;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class paymentService {

    @Autowired
    private paidDao paidDao;
    @Autowired
    private vbankDao vbankDao;
    @Autowired
    private iamportService iamportService;
    @Value("${payment.period}")
    private  int period;
    @Value("${payment.minusHour}")
    private  int minusHour;
    
    
    public void cancleBuy(String paymentId,int price) {
        System.out.println("cancleBuy");
        iamportService.cancleBuy(paymentId, price);
    }
    public vBankDto selectVbankProduct(String paymentId) {
        return vbankDao.findByPaymentId(paymentId);
    }
    public paidDto selectPaidProduct(String paymentId) {
        return paidDao.findByPaymentId(paymentId);
    }
    public void insertPayment(nomalPayment nomalPayment,userDto userDto,int totalPrice) {
        System.out.println("insertPayment");
        paidDto dto=paidDto.builder().email(userDto.getEmail())
                                    .kind(nomalPayment.getKind())
                                    .name(userDto.getName())
                                    .payMethod(nomalPayment.getPayMethod())
                                    .paymentId(nomalPayment.getPaymentid())
                                    .status(nomalPayment.getStatus())
                                    .usedKind(nomalPayment.getUsedKind())
                                    .totalPrice(totalPrice)
                                    .build();
                                    paidDao.save(dto);
                                    System.out.println("결제테이블 저장 완료");

    }
    public void insertPayment(vbankPayment vbankPayment,userDto userDto,int totalPrice) {
        System.out.println("insertPayment");
        vBankDto dto=vBankDto.builder().email(userDto.getEmail())
                                    .name(userDto.getName())
                                    .bank(vbankPayment.getBank())
                                    .bankNum(vbankPayment.getVbankNum())
                                    .endDate(Timestamp.valueOf(vbankPayment.getEndDate()))
                                    .paymentId(vbankPayment.getPaymentid())
                                    .status("ready")
                                    .price(totalPrice)
                                    .build();
                                    
                                    vbankDao.save(dto);
                                    System.out.println("vbnk테이블 저장 완료");

    }
    public JSONObject  getVbankDate(getVankDateDto getVankDateDto) {
        System.out.println("getVbankDate");
        try { 
            Calendar getToday = Calendar.getInstance();
            getToday.setTime(new Date()); 
            String requestDate=getVankDateDto.getYear()+"-"+getVankDateDto.getMonth()+"-"+getVankDateDto.getDate();
            long diffDays = utillService.getDateGap(getToday, requestDate);
            Collections.sort(getVankDateDto.getTimes());
            int shortestTime=getVankDateDto.getTimes().get(0);
            return utillService.makeJson(true, getVbankDate(diffDays, shortestTime, requestDate));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getVbankDate error "+e.getMessage());
            throw new RuntimeException("가상계좌 일짜 계산 실패");
        }
    }
    public String getVbankDate(long diffDays,int shortestTime,String requestDate) {
        System.out.println("getVbankDate");   
        String expiredDate=null;
        if(diffDays<period){
            System.out.println(shortestTime+" 가장작은시간");
            expiredDate=requestDate+" "+(shortestTime-minusHour)+":00:00";
            System.out.println(expiredDate+" 새로만든 기한");
            String[]temp=expiredDate.split(" ");
            String time=temp[1];
            temp=temp[0].split("-");
            if(temp[1].length()<2){
                System.out.println("10월보다작음");
                temp[1]="0"+temp[1];
            }
            if(temp[2].length()<2){
                System.out.println("10일보다작음");
                temp[2]="0"+temp[2];
            }
            String[] splitTime=time.split(":");
            if(splitTime[0].length()<2){
                splitTime[0]="0"+splitTime[0];
                time=splitTime[0]+":"+splitTime[1]+":"+splitTime[2];
            }
            expiredDate=temp[0]+"-"+temp[1]+"-"+temp[2]+" "+time;
 
        }else{
            System.out.println("예약 일자가 "+period+"이상임");
            expiredDate=getVbankDate();
        }
        System.out.println(expiredDate+" 최종");
        return expiredDate;
    }
    private String getVbankDate() {
        System.out.println("getVbankDate");
        String expiredDate=LocalDateTime.now().plusDays(period).toString();
        expiredDate=expiredDate.replace("T", " ");
        return expiredDate;
    }
}
