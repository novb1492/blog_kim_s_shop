package com.example.blog_kim_s_token.model.iamport;



import com.nimbusds.jose.shaded.json.JSONObject;


import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class iamportDto {
    
    private String code;
    private String messege;
    private JSONObject response=new JSONObject();
}
