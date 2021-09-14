package com.example.blog_kim_s_token.model.payment;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class reseponseSettleDto {
    private String mchtId;
    private String outStatCd;
    private String outRsltCd;
    private String outRsltMsg;
    private String method;
    private String mchtTrdNo;
    private String trdNo;
    private String trdAmt;
    private String mchtCustNm;
    private String cardNm;
    private String mchtParam;
    private String authDt;
    private String authNo;
    private String reqIssueDt;
    private String intMon;
    private String fnNm;
    private String fnCd;
    private String pointTrdNo;
    private String pointTrdAmt;
    private String cardTrdAmt;
    private String vtlAcntNo;
    private String expireDt;
    private String cphoneNo;
    private String billKey;
}
