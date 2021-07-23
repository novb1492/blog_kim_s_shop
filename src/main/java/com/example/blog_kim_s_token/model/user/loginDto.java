package com.example.blog_kim_s_token.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class loginDto {
    private String email;
    private String name;
    private String pwd;
    private String pwd2;
}
