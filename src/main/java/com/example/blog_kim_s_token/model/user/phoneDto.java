package com.example.blog_kim_s_token.model.user;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class phoneDto {
    
    @NotBlank
    private String phoneNum;

    @NotBlank
    private String tempNum;
}
