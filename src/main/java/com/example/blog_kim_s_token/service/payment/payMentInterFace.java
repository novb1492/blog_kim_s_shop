package com.example.blog_kim_s_token.service.payment;


public interface payMentInterFace {
    String getPaymentId();
    String getBuyerName();
    String getBuyerEmail();
    int getTotalPrice();
    String getPayCompany();
    String getKind();
    void setUsedKind(String usedKind);
    String getUsedKind();
    int getShortestTime();

}
