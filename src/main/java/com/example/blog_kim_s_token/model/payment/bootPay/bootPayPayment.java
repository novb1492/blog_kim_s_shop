package com.example.blog_kim_s_token.model.payment.bootPay;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class bootPayPayment {
   private String bankname;
   private String accountholder;
   private int account;
   private String expiredate;
   private String username;
   private String receipt_id;
   private String n;
   private int p;
   private String pg;
   private String pm;
   private String pg_a;
   private String pm_a;
   private String o_id;
   private int s;
   private int g;
}
