package com.example.blog_kim_s_token.model.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class pwdDto {
    
    @NotBlank
    @Size(min=4,max = 10,message = "비밀번호는 4자이상 10자 이하입니다")
    private String nowPwd;
    @NotBlank
    @Size(min=4,max = 10,message = "비밀번호는 4자이상 10자 이하입니다")
    private String newPwd;
    @NotBlank
    @Size(min=4,max = 10,message = "비밀번호는 4자이상 10자 이하입니다")
    private String newPwd2;
    
}
