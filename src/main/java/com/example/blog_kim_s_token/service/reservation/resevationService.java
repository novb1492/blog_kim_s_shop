package com.example.blog_kim_s_token.service.reservation;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;


import com.example.blog_kim_s_token.enums.reservationEnums;
import com.example.blog_kim_s_token.model.reservation.*;
import com.example.blog_kim_s_token.model.reservation.getDateDto;
import com.example.blog_kim_s_token.model.reservation.getTimeDto;
import com.example.blog_kim_s_token.model.reservation.reservationInsertDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.iamportService;
import com.example.blog_kim_s_token.service.priceService;
import com.example.blog_kim_s_token.service.userService;
import com.example.blog_kim_s_token.service.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class resevationService {

    private final int openTime=9;
    private final int closeTime=18;

    @Autowired
    private userService userService;
    @Autowired
    private reservationDao reservationDao;
    @Autowired
    private iamportService iamportService;
    @Autowired
    private priceService priceService;

    public JSONObject getDateBySeat(getDateDto getDateDto) {
        System.out.println("getDateBySeat");
        try {
            int month=getDateDto.getMonth();
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
    public JSONObject getTimeByDate(getTimeDto getTimeDto) {
        System.out.println("getTimeByDate");
        try {
            JSONObject timesJson=new JSONObject();
            int totalHour=closeTime-openTime;
            System.out.println(totalHour+" totalHour");
            int[][] timesArray=new int[totalHour+1][2];
            for(int i=0;i<=totalHour;i++){
                timesArray[i][0]=i+openTime;
                timesArray[i][1]=i;
            }
            timesJson.put("times", timesArray);
            return timesJson;
        } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException("getTimeByDate error");
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public JSONObject insertReservation(reservationInsertDto reservationInsertDto) {
        System.out.println("insertReservation");
        String impId=reservationInsertDto.getImpId();
        String seat=reservationInsertDto.getSeat();
        List<Integer>times=reservationInsertDto.getTimes();
        try {  
            int totalPrice=priceService.getTotalSeatPrice(seat,times.size());
            if(iamportService.confrimPayment(impId, times, seat, totalPrice)==false){
                System.out.println("insertReservation 검증실패");
                return utillService.makeJson(false,"결제 검증에 실패했습니다");
            }
            String email= SecurityContextHolder.getContext().getAuthentication().getName();
            userDto userDto=userService.findEmail(email);
            System.out.println(Timestamp.valueOf("2021-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" 00:00:00")+" 사용예정일");
            for(int i=0;i<times.size();i++){
                mainReservationDto dto=mainReservationDto.builder()
                                        .email(email)
                                        .name(userDto.getName())
                                        .userid(userDto.getId())
                                        .time(times.get(i))
                                        .seat(seat)
                                        .impId(impId)
                                        .rDate(Timestamp.valueOf("2021-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" 00:00:00"))
                                        .dateAndTime(Timestamp.valueOf("2021-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+times.get(i)+":00:00"))
                                        .build();
                                        reservationDao.save(dto);
            }

            return utillService.makeJson(reservationEnums.sucInsert.getBool(), reservationEnums.sucInsert.getMessege());
        } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException("getTimeByDate error");
        }
    }
    private int makeTotalPrice(String seat,List<Integer>times) {
        return 500*times.size();
    }
}
