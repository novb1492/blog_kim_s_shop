package com.example.blog_kim_s_token.service.reservation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.stereotype.Service;

@Service
public class resevationService {

    private final int lastDayIdNumber=38;

    public JSONObject getDateBySeat(int month) {
        try {
            System.out.println("getDateBySeat");
            LocalDate today=LocalDate.of(LocalDate.now().getYear(),month,1);
            YearMonth yearMonth=YearMonth.from(today);
            int lastDay=yearMonth.lengthOfMonth();
            int start=0;
            LocalDate date = LocalDate.of(today.getYear(),today.getMonthValue(),1);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            int temp=1;
            start=dayOfWeek.getValue();
            int endDayIdOfMonth=lastDay+start;
            JSONObject dates=new JSONObject();
            int []a=new int[lastDayIdNumber];
            for(int i=1;i<start;i++) {
                a[i]=0;
               
            }
            for(int i=start;i<endDayIdOfMonth;i++) {
                a[i]=temp;
                
                temp+=1;
            }
            if(endDayIdOfMonth<lastDayIdNumber) {
                for(int i=endDayIdOfMonth;i<=lastDayIdNumber;i++) {
                    a[i]=0;
                }
            }
            dates.put("dates", a);
            
            System.out.println(dates);
            return dates;
        } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException("getDateBySeat error");
        }
        
    
    }
}
