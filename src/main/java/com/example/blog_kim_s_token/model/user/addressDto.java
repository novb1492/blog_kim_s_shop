package com.example.blog_kim_s_token.model.user;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class addressDto {
    @NotBlank(message = "우편번호가 공백입니다")
    private String postcode;
    @NotBlank(message = "주소가 공백입니다")
    private String address;
    @NotBlank(message = "상세주소가 공백입니다")
    private String detailAddress;
    private String extraAddress;  
}
