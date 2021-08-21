package com.example.blog_kim_s_token.service.confrim;

import java.sql.Timestamp;

import com.example.blog_kim_s_token.model.confrim.confrimDto;



public interface confrimInterface {
    public String TempNumAtDb();
    public String unit();
    public String valueOfUbit();
    public Timestamp getCreated();
    public int getRequestTime();
    public boolean isNULL();
    public confrimDto getDto();
    public void setPassOneDay(boolean bool);
    public boolean getPassOneDay();
}
