package com.example.blog_kim_s_token.enums;

public enum confrimTrue {
    yes(1),
    no(0);

    
    private final int value;
    
    confrimTrue(int value){
        this.value = value;      
    }
    
    public int getValue(){
        return value;
    }
}
