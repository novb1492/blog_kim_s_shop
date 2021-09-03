package com.example.blog_kim_s_token.customException;

import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.dao.DataAccessException;

public class setBuyerExeption extends DataAccessException {

    
    private JSONObject buyerInfor;

    public setBuyerExeption(String msg,JSONObject buyerInfor) {
        super(msg);
        this.buyerInfor=buyerInfor;
    }
    public JSONObject getBuyerInfor() {
        return this.buyerInfor;
    }
    
}
