package com.example.blog_kim_s_token.model.payment;

import com.nimbusds.jose.shaded.json.JSONObject;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class bootTokenDto {
    private int code;
    private JSONObject data;
    private String message;
    private int status;
}
