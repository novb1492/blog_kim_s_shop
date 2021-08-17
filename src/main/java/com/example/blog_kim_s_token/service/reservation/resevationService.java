package com.example.blog_kim_s_token.service.reservation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.stereotype.Service;

@Service
public class resevationService {

    public JSONObject getDateBySeat(int month) {
        try {
            System.out.println("getDateBySeat"+month);
            LocalDate today=LocalDate.of(LocalDate.now().getYear(),month,1);
            YearMonth yearMonth=YearMonth.from(today);
            int lastDay=yearMonth.lengthOfMonth();
            System.out.println(lastDay+" lastDay");
            int start=0;
            LocalDate date = LocalDate.of(today.getYear(),today.getMonthValue(),1);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            int temp=1;
            start=dayOfWeek.getValue();
            System.out.println(start+" start");
            int endDayIdOfMonth=lastDay+start;
            System.out.println(endDayIdOfMonth+" endDayIdOfMonth");
            JSONObject dates=new JSONObject();
            int [][]dateAndValue=new int[endDayIdOfMonth][2];
            for(int i=1;i<start;i++) {
                dateAndValue[i][0]=0;
                dateAndValue[i][1]=0;
            }
            for(int i=start;i<endDayIdOfMonth;i++) {
                dateAndValue[i][0]=temp;
                dateAndValue[i][1]=temp;
                temp+=1;
            }
            dates.put("dates", dateAndValue);
            
            System.out.println(dates);
            return dates;
        } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException("getDateBySeat error");
        }
        
    
    }
}
