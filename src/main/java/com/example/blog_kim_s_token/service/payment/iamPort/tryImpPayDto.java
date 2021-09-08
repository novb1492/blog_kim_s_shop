package com.example.blog_kim_s_token.service.payment.iamPort;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class tryImpPayDto {
    @Min(value = 0,message = "금액이 0원입니다")
    private int totalPrice;

    @NotBlank(message = "kind가 비어 있습니다")
    private String kind;
    
    @Size(min = 0,message="아이템이 비어 있습니다")
    private String[][] itemArray;
    private String[] other;

    @NotBlank(message = "결제정보가 없습니다")
    private String impid;
}
