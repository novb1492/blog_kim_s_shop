package com.example.blog_kim_s_token.model.payment;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class reseponseSettleDto {
    private String mchtId;
    private int outStatCd;
    private int outRsltCd;
    private String outRsltMsg;
    private String method;
    private String mchtTrdNo;
    private String trdNo;
    private String trdAmt;
}
