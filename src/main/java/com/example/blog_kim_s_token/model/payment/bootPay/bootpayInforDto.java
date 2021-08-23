package com.example.blog_kim_s_token.model.payment.bootPay;

import com.nimbusds.jose.shaded.json.JSONObject;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class bootpayInforDto {
    private int code;
    private JSONObject data;
    private String message;
    private int status;
}
