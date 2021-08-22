package com.example.blog_kim_s_token.customException;

import org.springframework.dao.DataAccessException;

public class failBuyException extends DataAccessException {

    private String payMentId;
    public failBuyException(String msg,String payMentId) {
        super(msg);
        this.payMentId=payMentId;
    }
    public String getPayMentId() {
        return this.payMentId;
    }
}
