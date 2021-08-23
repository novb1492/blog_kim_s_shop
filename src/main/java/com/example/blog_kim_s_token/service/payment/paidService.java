package com.example.blog_kim_s_token.service.payment;

import com.example.blog_kim_s_token.customException.failBuyException;
import com.example.blog_kim_s_token.model.payment.paidDao;
import com.example.blog_kim_s_token.model.payment.paidDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            .usedKind(payMentInterFace.getUsedKind())
            .status("paid").build();
            paidDao.save(dto);
        } catch (Exception e) {
            System.out.println("insertPayment error");
            throw new failBuyException("결제내역 저장 실패",payMentInterFace.getPaymentId());
        }
       
    }
    public void insertVbankPayment(payMentInterFace payMentInterFace) {
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
}
