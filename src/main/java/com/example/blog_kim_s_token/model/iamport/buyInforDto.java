package com.example.blog_kim_s_token.model.iamport;

import com.nimbusds.jose.shaded.json.JSONObject;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class buyInforDto {
    private String code;
    private String message;
    private JSONObject response=new JSONObject();
}
