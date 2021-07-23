package com.example.blog_kim_s_token.model.oauth.naver;

import com.nimbusds.jose.shaded.json.JSONObject;

import lombok.Data;

@Data
public class naverDto {
    private String resultcode;
    private String message;
    private JSONObject response;
}
