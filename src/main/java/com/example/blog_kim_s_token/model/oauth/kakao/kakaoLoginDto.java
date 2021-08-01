package com.example.blog_kim_s_token.model.oauth.kakao;

import com.nimbusds.jose.shaded.json.JSONObject;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class kakaoLoginDto {
    private String id;
    private String connected_at;
    private JSONObject kakao_account;
}
