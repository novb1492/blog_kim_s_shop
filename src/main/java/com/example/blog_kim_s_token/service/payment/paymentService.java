package com.example.blog_kim_s_token.service.payment;

import java.sql.Timestamp;

import com.example.blog_kim_s_token.customException.failBuyException;
import com.example.blog_kim_s_token.model.payment.paidDao;
import com.example.blog_kim_s_token.model.payment.paidDto;
import com.example.blog_kim_s_token.model.payment.vBankDto;
import com.example.blog_kim_s_token.model.payment.vbankDao;
import com.example.blog_kim_s_token.service.payment.bootPay.bootPayInter;
import com.example.blog_kim_s_token.service.payment.bootPay.bootPayService;
import com.example.blog_kim_s_token.service.payment.iamPort.iamInter;
import com.example.blog_kim_s_token.service.payment.iamPort.iamportService;

import org.springframework.beans.factory.annotation.Autowired;
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
                                            .endDate(endDate).build();
                                            vbankDao.save(dto);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insertVbankPayment error");
            throw new RuntimeException("가상계좌 저장에 실패했습니다");
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
}
