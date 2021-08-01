package com.example.blog_kim_s_token.model.oauth.kakao;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class kakaoTokenDto {
    private String token_type;
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String refresh_token_expires_in;
    private String scope;
}
