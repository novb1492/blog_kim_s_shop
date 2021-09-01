package com.example.blog_kim_s_token.service.payment.iamPort;

import com.example.blog_kim_s_token.service.payment.paymentabstract;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class vbankPayment extends paymentabstract {
    private String bank;
    private String vbankNum;
    private String endDate; 
    private String payMethod;
}
