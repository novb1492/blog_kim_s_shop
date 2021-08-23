package com.example.blog_kim_s_token.service.payment.iamPort;

import com.example.blog_kim_s_token.service.payment.payMentInterFace;

import lombok.Builder;

@Builder
public class iamInter implements payMentInterFace {

    private String payMentId;
    private String BuyerName;
    private String BuyerEmail;
    private String kind;
    private int totalPrice;
    private String usedKind;

    @Override
    public String getPaymentId() {
        return payMentId;
    }

    @Override
    public String getBuyerName() {
        return BuyerName;
    }

    @Override
    public String getBuyerEmail() {
        return BuyerEmail;
    }

    @Override
    public int getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String getPayCompany() {
        return "iamport";
    }

    @Override
    public String getKind() {
        return kind;
    }

    @Override
    public String getUsedKind() {
        return this.usedKind;
    }
    @Override
    public void setUsedKind(String usedKind) {
        this.usedKind=usedKind;        
    }
    
}
