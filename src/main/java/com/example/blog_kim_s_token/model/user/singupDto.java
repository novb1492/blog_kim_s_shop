package com.example.blog_kim_s_token.model.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class singupDto {

    @Email(message = "이메일 형식으로 적어주세요")
    @NotBlank(message = "이메일이 공백입니다")
    private String email;
    @NotBlank(message = "이름이 공백입니다")
    private String name;
    @Size(min = 4,max = 10,message = "비밀번호는 최소 4자 최대 10자입니다")
    private String pwd;
    @Size(min = 4,max = 10,message = "비밀번호는 최소 4자 최대 10자입니다")
    private String pwd2; 
    @NotBlank(message = "우편번호가 공백입니다")
    private String postcode;
    @NotBlank(message = "주소가 공백입니다")
    private String address;
    @NotBlank(message = "상세주소가 공백입니다")
    private String detailAddress;
    @NotBlank(message = "참고항목이 공백입니다")
    private String extraAddress;
    @Positive(message = "전화번호가  공백입니다")
    private String phoneNum;
}
