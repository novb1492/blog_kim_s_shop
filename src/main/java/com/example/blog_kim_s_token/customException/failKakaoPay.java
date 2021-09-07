package com.example.blog_kim_s_token.customException;

import org.springframework.dao.DataAccessException;

import lombok.Getter;

@Getter
public class failKakaoPay extends DataAccessException {

    private String cid;
    private String tid;
    private int totalPrice;

    public failKakaoPay(String msg,String cid,String tid,int totalPrice) {
        super(msg);
        this.cid=cid;
        this.tid=tid;
        this.totalPrice=totalPrice;
    }
    
}
