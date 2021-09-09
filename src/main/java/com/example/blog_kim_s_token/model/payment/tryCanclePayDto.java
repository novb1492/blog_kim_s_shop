package com.example.blog_kim_s_token.model.payment;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class tryCanclePayDto {

    @NotBlank(message = "상품 고유값이 없습니다")
    private String paymentid;
    @NotBlank(message = "상품 종류가 없습니다")
    private String kind;
}
