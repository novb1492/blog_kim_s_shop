package com.example.blog_kim_s_token.service.reservation;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import com.example.blog_kim_s_token.enums.paymentEnums;
import com.example.blog_kim_s_token.enums.reservationEnums;
import com.example.blog_kim_s_token.model.reservation.*;
import com.example.blog_kim_s_token.model.reservation.getDateDto;
import com.example.blog_kim_s_token.model.reservation.getTimeDto;
import com.example.blog_kim_s_token.model.reservation.reservationInsertDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.priceService;
import com.example.blog_kim_s_token.service.userService;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.payment.iamportService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class resevationService {

    private final int openTime=9;
    private final int closeTime=18;
    private final int maxPeopleOfDay=60;
    private final int maxPeopleOfTime=6;
    private final int cantFlag=100;
 

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
            LocalDate today=LocalDate.of(getDateDto.getYear(),month,1);
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
            int [][]dateAndValue=new int[endDayIdOfMonth][3];
            for(int i=1;i<start;i++) {
                dateAndValue[i][0]=0;
                dateAndValue[i][1]=0;
                dateAndValue[i][2]=cantFlag;
            }
            for(int i=start;i<endDayIdOfMonth;i++) {
                Timestamp  timestamp=Timestamp.valueOf(getDateDto.getYear()+"-"+month+"-"+temp+" 00:00:00");
                int countAlready=getCountAlreadyInDate(timestamp,getDateDto.getSeat());
                dateAndValue[i][0]=temp;
                dateAndValue[i][1]=countAlready;
                if(countAlready>=maxPeopleOfDay||utillService.compareDate(timestamp, LocalDateTime.now())){
                    dateAndValue[i][2]=cantFlag; 
                }
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
    private int getCountAlreadyInDate(Timestamp timestamp,String seat) {
        System.out.println("getCountAlreadyIn");
        System.out.println(timestamp);
        return reservationDao.findByRdate(timestamp,seat);
    }
    public JSONObject getTimeByDate(getTimeDto getTimeDto) {
        System.out.println("getTimeByDate");
        try {
            JSONObject timesJson=new JSONObject();
            int totalHour=closeTime-openTime;
            System.out.println(totalHour+" totalHour");
            int[][] timesArray=new int[totalHour+1][3];
            for(int i=0;i<=totalHour;i++){
                Timestamp timestamp=Timestamp.valueOf(getTimeDto.getYear()+"-"+getTimeDto.getMonth()+"-"+getTimeDto.getDate()+" "+(i+openTime)+":00:00");
                int count=getCountAlreadyInTime(timestamp,getTimeDto.getSeat());
                timesArray[i][0]=i+openTime;
                timesArray[i][1]=count;
                System.out.println(count);
                if(LocalDateTime.now().getDayOfMonth()==getTimeDto.getDate()){
                    if((i+openTime)<=LocalDateTime.now().getHour()){
                        System.out.println("지난시간");
                        timesArray[i][2]=100;
                    }
                }
                else if(count==maxPeopleOfTime){
                    System.out.println("자리가 다찬시간");
                    timesArray[i][2]=100;
                }
            }
            timesJson.put("times", timesArray);
            return timesJson;
        } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException("getTimeByDate error");
        }
    }
    public int getCountAlreadyInTime(Timestamp timestamp,String seat) {
        System.out.println("getCountAlreadyInTime");
        System.out.println(timestamp);
        return reservationDao.findByTime(timestamp,seat);
    }
    @Transactional(rollbackFor = Exception.class)
    public JSONObject insertReservation(reservationInsertDto reservationInsertDto) {
        System.out.println("insertReservation");
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        String impId=reservationInsertDto.getPaymentId();
        String seat=reservationInsertDto.getSeat();
        List<Integer>times=reservationInsertDto.getTimes();
        JSONObject result=confrimInsert(reservationInsertDto,email);
        if((boolean)result.get("bool")==false){
            return result;
        }
        try {  
            int totalPrice=priceService.getTotalSeatPrice(seat,times.size());
            paymentEnums paymentEnums=iamportService.confrimPayment(impId, times, seat, totalPrice);
            if(paymentEnums.getBool()==false){
                System.out.println("insertReservation 검증실패");
                return utillService.makeJson(false,"결제 검증에 실패했습니다");
            }
            userDto userDto=userService.findEmail(email);
            System.out.println(Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" 00:00:00")+" 사용예정일");
            for(int i=0;i<times.size();i++){
                mainReservationDto dto=mainReservationDto.builder()
                                        .email(email)
                                        .name(userDto.getName())
                                        .userid(userDto.getId())
                                        .time(times.get(i))
                                        .seat(seat)
                                        .paymentId(impId)
                                        .rDate(Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" 00:00:00"))
                                        .dateAndTime(Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+times.get(i)+":00:00"))
                                        .status(paymentEnums.getStatus())
                                        .build();
                                        reservationDao.save(dto);
            }

            return utillService.makeJson(reservationEnums.sucInsert.getBool(), reservationEnums.sucInsert.getMessege());
        } catch (Exception e) {
           e.printStackTrace();
           throw new RuntimeException("insertReservation error");
        }
        
    }
    private JSONObject confrimInsert(reservationInsertDto reservationInsertDto,String email){
        System.out.println("confrimInsert");
         List<mainReservationDto>array=SelectByEmail(email,reservationInsertDto.getSeat());
            if(array!=null){
                for(mainReservationDto m:array){
                    for(int i=0;i<reservationInsertDto.getTimes().size();i++){
                        if(m.getDateAndTime().equals(Timestamp.valueOf(LocalDate.now().getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+reservationInsertDto.getTimes().get(i)+":00:00"))){
                            System.out.println("이미 예약한 시간 발견");
                            return utillService.makeJson(reservationEnums.findAlready.getBool(),reservationEnums.findAlready.getMessege()+LocalDate.now().getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+reservationInsertDto.getTimes().get(i)+":00:00"); 
                        }
                    }
                }
            }
        return utillService.makeJson(true, "");
    }
    public List<mainReservationDto> SelectByEmail(String email,String seat) {
        try {
            return reservationDao.findByEmail(email,seat);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SelectByEmail error");
            throw new RuntimeException("SelectByEmail error");
        }
    }
}
