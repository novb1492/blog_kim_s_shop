package com.example.blog_kim_s_token.model.payment;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class getHashInfor {
    @Min(value = 0,message = "값이 0보다 작습나다")
    private int totalPrice;

    @NotBlank(message = "mchtId가 공백입니다")
    private String mchtId;

    @NotBlank(message = "method가 공백입니다")
    private String method;

    @NotBlank(message = "kind가 공백입니다")
    private String kind;

    private String mchtTrdNo;
    private String requestDate;
    private String requestTime;
    

}
