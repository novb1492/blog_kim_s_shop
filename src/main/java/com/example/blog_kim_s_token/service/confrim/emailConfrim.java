package com.example.blog_kim_s_token.service.confrim;

import java.sql.Timestamp;

import com.example.blog_kim_s_token.model.confrim.confrimDto;

public class emailConfrim implements  confrimInterface {
    private confrimDto confrimDto;
    public emailConfrim(confrimDto confrimDto){
        this.confrimDto=confrimDto;
    }
    @Override
    public String TempNumAtDb() {
        return confrimDto.getEmailTempNum();
    }
    @Override
    public String unit() {
        return "email";
    }
    @Override
    public String valueOfUbit() {
        return confrimDto.getEmail();
    }
    @Override
    public Timestamp getCreated() {
        return confrimDto.getCreated();
    }
    @Override
    public int getRequestTime() {
        return confrimDto.getRequestTime();
    }
    @Override
    public boolean isNULL() {
       if(this.confrimDto==null){
           return true;
       }
        return false;
    }
    @Override
    public confrimDto getDto() {
        return this.confrimDto;
    }
  
 

    
}
