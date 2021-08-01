package com.example.blog_kim_s_token.model.oauth.kakao;

import java.util.LinkedHashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class kakaoAccountDto {
    private boolean email_needs_agreement;
    private boolean profile_nickname_needs_agreement;
    private LinkedHashMap<String,String> profile;
    private boolean is_email_valid;
    private boolean is_email_verified;
    private boolean has_email;
    private String email;
}
