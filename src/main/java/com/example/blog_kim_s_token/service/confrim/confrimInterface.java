package com.example.blog_kim_s_token.service.confrim;

import java.sql.Timestamp;



public interface confrimInterface {
    public String TempNumAtDb();
    public String unit();
    public String valueOfUbit();
    public Timestamp getCreated();
    public int getRequestTime();
    public boolean isNULL();
}
