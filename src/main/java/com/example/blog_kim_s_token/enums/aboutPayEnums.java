package com.example.blog_kim_s_token.enums;

public enum aboutPayEnums {
    
    statusPaid("paid"),
    statusReady("ready"),
    reservationKind("reservation"),
    productKind("product");

    private String messege;

    aboutPayEnums(String messege){
        this.messege=messege;
    
    }
    public String getString() {
        return messege;
    }
}
