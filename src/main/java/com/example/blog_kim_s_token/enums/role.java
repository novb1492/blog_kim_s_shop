package com.example.blog_kim_s_token.enums;

public enum role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    failEmailSmsCheck("false"),
    sucEmailSmsCheck("true");

    
    private final String value;
    
    role(String value){
        this.value = value;      
    }
    
    public String getValue(){
        return value;
    }
}
