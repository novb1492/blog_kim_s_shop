package com.example.blog_kim_s_token.service.payment;

import com.example.blog_kim_s_token.service.payment.bootPay.bootPayInter;
import com.example.blog_kim_s_token.service.payment.iamPort.iamInter;

import org.springframework.stereotype.Service;

@Service
public class paymentService {
    
    public payMentInterFace makePaymentInter(String paymentId,String email,String name,int totalPrice,String kind) {
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
                                            .build();
            payMentInterFace=inter;
            return payMentInterFace;
        }
    }
}
