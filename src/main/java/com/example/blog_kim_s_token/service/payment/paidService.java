package com.example.blog_kim_s_token.service.payment;

import com.example.blog_kim_s_token.model.payment.paidDao;
import com.example.blog_kim_s_token.model.payment.paidDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class paidService {
    @Autowired
    private paidDao paidDao;

    
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
            .status("paid").build();
           
            paidDao.save(dto);
        } catch (Exception e) {
            System.out.println("insertPayment error");
            throw new RuntimeException("insertPayment 결제저장에 실패했습니다");
        }
       
    }
}
