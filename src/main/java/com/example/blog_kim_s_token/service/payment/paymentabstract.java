package com.example.blog_kim_s_token.service.payment;


import lombok.Data;
import lombok.NoArgsConstructor;



@NoArgsConstructor
@Data
public abstract class paymentabstract {
    private String email;
    private String name;
    private String paymentid;
    private String status;
}
