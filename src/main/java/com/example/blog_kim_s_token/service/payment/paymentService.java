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
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.payment.bootPay.bootPayInter;
import com.example.blog_kim_s_token.service.payment.bootPay.bootPayService;
import com.example.blog_kim_s_token.service.payment.iamPort.iamInter;
import com.example.blog_kim_s_token.service.payment.iamPort.iamportService;
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
    @Autowired
    private bootPayService bootPayService;
    @Value("${payment.period}")
    private  int period;
    @Value("${payment.minusHour}")
    private  int minusHour;
    
    
    public payMentInterFace makePaymentInter(String paymentId,String email,String name,int totalPrice,String kind,int shortestTime) {
        System.out.println("makePaymentInter");
        payMentInterFace payMentInterFace=null;
        if(paymentId.startsWith("imp")){
            System.out.println("아임포트 interface생성시도");
            iamInter inter=iamInter.builder()
                                .BuyerEmail(email)
                                .BuyerName(name)
                                .kind(kind)
                                .payMentId(paymentId)
                                .totalPrice(totalPrice)
                                .build();
            payMentInterFace=inter;
            return payMentInterFace;
        }else{
            System.out.println("부트페이 interface생성시도");
            bootPayInter inter=bootPayInter.builder()
                                            .BuyerEmail(email)
                                            .BuyerName(name)
                                            .kind(kind)
                                            .payMentId(paymentId)
                                            .totalPrice(totalPrice)
                                            .shortestTime(shortestTime)
                                            .build();
            payMentInterFace=inter;
            return payMentInterFace;
        }
    }
    public String confrimPayment(payMentInterFace payMentInterFace) {
        if(payMentInterFace.getPayCompany().equals("iamport")){
            System.out.println("아임포트 결제시도");
            iamportService.confrimPayment(payMentInterFace);
            return "paid";
        }else{
            System.out.println("부트페이 결제시도");
            bootPayService.confrimPayment(payMentInterFace);
            return "ready";
        }
    }
    public void insertPayment(payMentInterFace payMentInterFace) {
        System.out.println("insertPayment");
        try {
            paidDto dto=paidDto.builder()
            .email(payMentInterFace.getBuyerEmail())
            .name(payMentInterFace.getBuyerName())
            .paymentId(payMentInterFace.getPaymentId())
            .kind(payMentInterFace.getKind())
            .payCompany(payMentInterFace.getPayCompany())
            .totalPrice(payMentInterFace.getTotalPrice())
            .usedKind(payMentInterFace.getUsedKind())
            .status("paid").build();
            paidDao.save(dto);
        } catch (Exception e) {
            System.out.println("insertPayment error");
            throw new failBuyException("결제내역 저장 실패",payMentInterFace.getPaymentId());
        }
       
    }
    public void insertVbankPayment(payMentInterFace payMentInterFace,Timestamp endDate) {
        System.out.println("insertVbankPayment");
        try {
            vBankDto dto=vBankDto.builder()
                                            .bank(payMentInterFace.getUsedKind())
                                            .email(payMentInterFace.getBuyerEmail())
                                            .paymentId(payMentInterFace.getPaymentId())
                                            .price(payMentInterFace.getTotalPrice())
                                            .status("ready")
                                            .bankNum(payMentInterFace.getVankNum())
                                            .endDate(endDate).build();
                                            vbankDao.save(dto);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insertVbankPayment error");
            throw new failBuyException("가상계좌 결제내역 저장 실패",payMentInterFace.getPaymentId());
        }
       
    }
    public void cancleBuy(String paymentId,int price) {
        System.out.println("cancleBuy");
        iamportService.cancleBuy(paymentId, price);
    }
    public vBankDto selectVbankProduct(String paymentId) {
        return vbankDao.findByPaymentId(paymentId);
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
            expiredDate=temp[0]+"-"+temp[1]+"-"+temp[2]+" "+time;
 
        }else{
            System.out.println("예약 일자가 "+period+"이상임");
            expiredDate=LocalDateTime.now().plusDays(period).toString();
            expiredDate=expiredDate.replace("T", " ");
        }
        System.out.println(expiredDate+" 최종");
        return expiredDate;
    }
}
