package com.example.blog_kim_s_token.service.payment;

import lombok.Builder;

@Builder
public class iamInter implements payMentInterFace {

    private String payMentId;
    private String BuyerName;
    private String BuyerEmail;
    private String kind;
    private String payCompany;
    private int totalPrice;

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
        return payCompany;
    }

    @Override
    public String getKind() {
        return kind;
    }
    
}
