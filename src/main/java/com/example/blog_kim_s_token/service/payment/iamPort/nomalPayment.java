package com.example.blog_kim_s_token.service.payment.iamPort;

import com.example.blog_kim_s_token.service.payment.paymentabstract;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class nomalPayment extends paymentabstract{
    private String usedKind;
    private String payMethod;
}
