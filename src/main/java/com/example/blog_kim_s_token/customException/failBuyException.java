package com.example.blog_kim_s_token.customException;



import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.dao.DataAccessException;

public class failBuyException extends DataAccessException {


    private String paymentid;


    public failBuyException(String msg,String paymentid,JSONObject buyerInfor) {
        super(msg);
        this.paymentid=paymentid;
    }
    public String getpaymentid() {
        return this.paymentid;
    }
}
