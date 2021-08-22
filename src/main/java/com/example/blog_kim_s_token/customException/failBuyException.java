package com.example.blog_kim_s_token.customException;

import org.springframework.dao.DataAccessException;

public class failBuyException extends DataAccessException {

    public failBuyException(String msg) {
        super(msg);
    }
}
