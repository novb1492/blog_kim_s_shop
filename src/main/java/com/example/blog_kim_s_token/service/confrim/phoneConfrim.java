package com.example.blog_kim_s_token.service.confrim;

import java.sql.Timestamp;

import com.example.blog_kim_s_token.model.confrim.confrimDto;

public class phoneConfrim implements confrimInterface{
    private confrimDto confrimDto;
    private boolean passOneDay=false;
    public phoneConfrim(confrimDto confrimDto){
        this.confrimDto=confrimDto;
    }
    @Override
    public String TempNumAtDb() {
        return confrimDto.getPhoneTempNum();
    }
    @Override
    public String unit() {
        return "phone";
    }
    @Override
    public String valueOfUbit() {
        return confrimDto.getPhoneNum();
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
    @Override
    public void setPassOneDay(boolean bool) {
       this.passOneDay=bool;
        
    }
    @Override
    public boolean getPassOneDay() {
        return  this.passOneDay;
    }
}
