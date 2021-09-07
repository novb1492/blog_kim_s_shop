package com.example.blog_kim_s_token.service.payment;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import com.example.blog_kim_s_token.enums.paymentEnums;
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
import com.example.blog_kim_s_token.service.reservation.resevationService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class paymentService {

    @Autowired
    private paidDao paidDao;
    @Autowired
    private vbankDao vbankDao;
    @Autowired
    private iamportService iamportService;
    @Autowired
    private resevationService resevationService;
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
    public void insertPayment(nomalPayment nomalPayment,int totalPrice) {
        System.out.println("insertPayment");
        paidDto dto=paidDto.builder().email(nomalPayment.getEmail())
                                    .kind(nomalPayment.getKind())
                                    .name(nomalPayment.getName())
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
                                    .kind(vbankPayment.getKind())
                                    .vbankTotalPrice(totalPrice)
                                    .bankCode(vbankPayment.getBankCode())
                                    .pgName(vbankPayment.getPgName())
                                    .endDateUnixTime(vbankPayment.getUnixTime())
                                    .merchant_uid(vbankPayment.getMerchantUid())
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
            paymentEnums enums=checkTime(getVankDateDto);
            if(enums.getBool()==false){
                return utillService.makeJson(enums.getBool(), enums.getperiod());
            }
            return utillService.makeJson(true,getVbankDate(diffDays, shortestTime, requestDate));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getVbankDate error "+e.getMessage());
            throw new RuntimeException("가상계좌 일짜 계산 실패");
        }
    }
    private paymentEnums checkTime(getVankDateDto getVankDateDto) {
        System.out.println("checkTime");
        if(Timestamp.valueOf(getVankDateDto.getYear()+"-"+getVankDateDto.getMonth()+"-"+getVankDateDto.getDate()+" "+(getVankDateDto.getTimes().get(0)-minusHour)+":00:00").toLocalDateTime().isBefore(LocalDateTime.now())){
            paymentEnums.failCheck.setperiod("가상계좌는 최소 "+minusHour+"전에 가능합니다");
            return paymentEnums.failCheck;
        }
        return paymentEnums.sucCheck;
    }
    private String getVbankDate(long diffDays,int shortestTime,String requestDate) {
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
    @Transactional(rollbackFor = Exception.class)
    public void vbankOk(JSONObject jsonObject) {
        System.out.println("vbankOk");
        System.out.println(jsonObject+" payment");
        try {
        String status=(String) jsonObject.get("status");
        String merchantUid=(String) jsonObject.get("merchant_uid");
        if(status.equals("paid")&&merchantUid.startsWith("vbank")){
            System.out.println("가상계좌가 입금 확인됨");
            String paymentId=(String) jsonObject.get("imp_uid");
            vBankDto vBankDto=vbankDao.findByPaymentId(paymentId);
            if(vBankDto.getKind().equals("reservation")){
                System.out.println("예약시도 가상계좌였습니다");
                resevationService.readyTopaid(paymentId);
            }else{
                System.out.println("상품시도 가상계좌였습니다");
            }
            nomalPayment nomalPayment=new nomalPayment();
            nomalPayment.setEmail(vBankDto.getEmail());
            nomalPayment.setName(vBankDto.getName());
            nomalPayment.setUsedKind(vBankDto.getBank()+" "+vBankDto.getBankNum()+" "+vBankDto.getPgName()+" "+vBankDto.getBankCode());
            nomalPayment.setPayMethod("vbank");
            nomalPayment.setKind(vBankDto.getKind());
            nomalPayment.setPaymentid(vBankDto.getPaymentId());
            nomalPayment.setStatus("paid");
            insertPayment(nomalPayment, vBankDto.getVbankTotalPrice());
            vbankDao.delete(vBankDto);
            return;
        }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("vbankOk error"+e.getMessage());
            throw new RuntimeException(e.getMessage());
        } 
    }
    public void updatePaidProductForCancle(String paymentid,int minusPrice) {
        paidDto paidDto=paidDao.findByPaymentId(paymentid);
        int price=paidDto.getTotalPrice();
        int newPrice=price-minusPrice;
        if(newPrice==0){
            System.out.println("취소후 전액환불예정");
            paidDao.delete(paidDto);
            return;
        }
        paidDto.setTotalPrice(newPrice);
    }
    public int minusPrice(int totalPrice,int minusPrice) {
        return totalPrice-minusPrice;
    }
}
