package com.example.blog_kim_s_token.service.confrim;


public class confrimDtos<T> {
    private T dto;
    public confrimDtos(T dto,String messege) {
        this.dto=dto;
        if(this.dto==null){
            throw new IllegalArgumentException(messege);
        }
    }
}
