package com.example.blog_kim_s_token.model.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class singupDto {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String name;
    @Size(min = 4,max = 10)
    private String pwd;
    @Size(min = 4,max = 10)
    private String pwd2; 
    @NotBlank
    private String postcode;
    @NotBlank
    private String address;
    @NotBlank
    private String detailAddress;
    @NotBlank
    private String extraAddress;
    @Positive
    private String phoneNum;
}
