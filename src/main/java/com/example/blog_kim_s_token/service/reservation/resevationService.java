package com.example.blog_kim_s_token.service.reservation;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import com.example.blog_kim_s_token.customException.failBuyException;
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
import com.example.blog_kim_s_token.service.payment.iamInter;
import com.example.blog_kim_s_token.service.payment.iamportService;
import com.example.blog_kim_s_token.service.payment.payMentInterFace;
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
            LocalDate selectDate=LocalDate.of(getDateDto.getYear(),month,1);
            YearMonth yearMonth=YearMonth.from(selectDate);
            int lastDay=yearMonth.lengthOfMonth();
            System.out.println(lastDay+" lastDay");
            int start=0;
            DayOfWeek dayOfWeek = selectDate.getDayOfWeek();
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
                        timesArray[i][2]=cantFlag;
                    }
                }
                else if(count==maxPeopleOfTime){
                    System.out.println("자리가 다찬시간");
                    timesArray[i][2]=cantFlag;
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
    public JSONObject confrimContents(reservationInsertDto reservationInsertDto) {
        reservationInsertDto.setEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        reservationEnums result=confrimInsert(reservationInsertDto);

        if(result.getBool()==false){
            return utillService.makeJson(result.getBool(),result.getMessege());
        }
        return confrimPayment(reservationInsertDto);
    }
    public JSONObject confrimPayment(reservationInsertDto reservationInsertDto) {
        System.out.println("confrimPayment");
        userDto userDto=userService.findEmail(reservationInsertDto.getEmail());
        String seat=reservationInsertDto.getSeat();
        List<Integer>times=reservationInsertDto.getTimes();
        int totalPrice=priceService.getTotalSeatPrice(seat,times.size());

        reservationInsertDto.setUserId(userDto.getId());
        reservationInsertDto.setName(userDto.getName());
        
        iamInter inter=iamInter.builder()
                                .BuyerEmail(reservationInsertDto.getEmail())
                                .BuyerName(userDto.getName())
                                .kind("reservation")
                                .payCompany("iamport")
                                .payMentId(reservationInsertDto.getPaymentId())
                                .totalPrice(totalPrice)
                                .build();
        payMentInterFace payMentInterFace=inter;
        paymentEnums paymentEnums=iamportService.confrimPayment(payMentInterFace);
        if(paymentEnums.getBool()==false){
            System.out.println("confrimPayment 검증실패");
            return utillService.makeJson(false,"결제 검증에 실패했습니다");
        }
        reservationInsertDto.setStatus(paymentEnums.getStatus());
        return insertReservation(reservationInsertDto);
    }
    public JSONObject insertReservation(reservationInsertDto reservationInsertDto) {
        System.out.println("insertReservation");
        List<Integer>times=reservationInsertDto.getTimes();
        try {  
            System.out.println(Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" 00:00:00")+" 사용예정일");
            for(int i=0;i<times.size();i++){
                mainReservationDto dto=mainReservationDto.builder()
                                        .email(reservationInsertDto.getEmail())
                                        .name(reservationInsertDto.getName())
                                        .userid(reservationInsertDto.getUserId())
                                        .time(times.get(i))
                                        .seat(reservationInsertDto.getSeat())
                                        .paymentId(reservationInsertDto.getPaymentId())
                                        .rDate(Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" 00:00:00"))
                                        .dateAndTime(Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+times.get(i)+":00:00"))
                                        .status(reservationInsertDto.getStatus())
                                        .build();
                                        reservationDao.save(dto);
            }
            return utillService.makeJson(reservationEnums.sucInsert.getBool(), reservationEnums.sucInsert.getMessege());
        } catch (Exception e) {
           e.printStackTrace();
           System.out.println("insertReservation error");
           throw new failBuyException(reservationInsertDto.getPaymentId());
        }
        
    }
    private reservationEnums confrimInsert(reservationInsertDto reservationInsertDto){
        System.out.println("confrimInsert");
         List<mainReservationDto>array=SelectByEmail(reservationInsertDto.getEmail(),reservationInsertDto.getSeat());
            if(array!=null){
                for(mainReservationDto m:array){
                    for(int i=0;i<reservationInsertDto.getTimes().size();i++){
                        if(m.getDateAndTime().equals(Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+reservationInsertDto.getTimes().get(i)+":00:00"))){
                            System.out.println("이미 예약한 시간 발견");
                            reservationEnums.findAlready.setMessete("이미 같은 시간에 예약이 있습니다 "+reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+reservationInsertDto.getTimes().get(i)+":00:00");
                            return reservationEnums.findAlready; 
                        }
                    }
                }
            }
        return reservationEnums.sucInsert;
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
