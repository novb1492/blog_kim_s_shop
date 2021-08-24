package com.example.blog_kim_s_token.service.reservation;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

import com.example.blog_kim_s_token.customException.failBuyException;
import com.example.blog_kim_s_token.enums.reservationEnums;
import com.example.blog_kim_s_token.model.reservation.*;
import com.example.blog_kim_s_token.model.reservation.getDateDto;
import com.example.blog_kim_s_token.model.reservation.getTimeDto;
import com.example.blog_kim_s_token.model.reservation.reservationInsertDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.priceService;
import com.example.blog_kim_s_token.service.userService;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.payment.payMentInterFace;
import com.example.blog_kim_s_token.service.payment.paymentService;
import com.example.blog_kim_s_token.service.payment.bootPay.bootPayService;
import com.example.blog_kim_s_token.service.payment.iamPort.iamportService;
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
    private final String kind="reservation";
    private final int minusHour=1;
  
 

    @Autowired
    private userService userService;
    @Autowired
    private reservationDao reservationDao;
    @Autowired
    private iamportService iamportService;
    @Autowired
    private priceService priceService;
    @Autowired
    private paymentService paymentService;
    @Autowired
    private bootPayService bootPayService;

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
        Collections.sort(reservationInsertDto.getTimes());
        confrimInsert(reservationInsertDto);
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
        payMentInterFace payMentInterFace=paymentService.makePaymentInter(reservationInsertDto.getPaymentId(), reservationInsertDto.getEmail(),userDto.getName(), totalPrice,kind,times.get(0));
    
        if(payMentInterFace.getPayCompany().equals("iamport")){
            System.out.println("아임포트 결제시도");
            iamportService.confrimPayment(payMentInterFace);
            reservationInsertDto.setStatus("paid");
        }else{
            System.out.println("부트페이 결제시도");
            bootPayService.confrimPayment(payMentInterFace);
            reservationInsertDto.setStatus("ready");
        }
        reservationInsertDto.setUsedKind(payMentInterFace.getUsedKind());
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
                                        .usedPayKind(reservationInsertDto.getUsedKind())
                                        .build();
                                        reservationDao.save(dto);
            }
            return utillService.makeJson(reservationEnums.sucInsert.getBool(), reservationEnums.sucInsert.getMessege());
        } catch (Exception e) {
           e.printStackTrace();
           System.out.println("insertReservation error");
           throw new failBuyException("예약 저장 실패",reservationInsertDto.getPaymentId());
        }
    }
    private void confrimInsert(reservationInsertDto reservationInsertDto){
        System.out.println("confrimInsert");
        try {
            List<mainReservationDto>array=SelectByEmail(reservationInsertDto.getEmail(),reservationInsertDto.getSeat());
            if(array!=null){
                for(mainReservationDto m:array){
                    for(int i=0;i<reservationInsertDto.getTimes().size();i++){
                        String date=reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+reservationInsertDto.getTimes().get(i)+":00:00";
                        Timestamp DateAndTime=Timestamp.valueOf(date);
                        if(m.getDateAndTime().equals(DateAndTime)||utillService.compareDate(DateAndTime, LocalDateTime.now())){
                            System.out.println("이미 예약한 시간 발견or지난 날짜 예약시도");
                            throw new Exception("이미 예약한 시간 발견 이거나 지난 날짜 예약시도입니다 "+date);
                        }  
                        else if(getCountAlreadyInTime(DateAndTime,reservationInsertDto.getSeat())==maxPeopleOfTime){
                            System.out.println("예약이 다찬 시간입니다");
                            throw new Exception("이미 예약한 시간 발견 이거나 지난 날짜 예약시도입니다 "+date);
                        }
                    }
                }
            }
            
            if(utillService.compareDate(Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+reservationInsertDto.getTimes().get(0)+":00:00"), LocalDateTime.now())==false){
                LocalDateTime shortestTime=Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+reservationInsertDto.getTimes().get(0)+":00:00").toLocalDateTime();
                if(LocalDateTime.now().plusHours(minusHour).isAfter(shortestTime)){
                    System.out.println("가상 계좌 제한시간은 최대 "+minusHour+"시간입니다");
                    System.out.println(LocalDateTime.now().plusHours(minusHour)+" "+shortestTime+"시간");
                    throw new Exception("가상계좌는 최대 "+minusHour+"시간 전에 가능합니다");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("confrimInsert error");
            if(reservationInsertDto.getPaymentId().startsWith("imp")){
                System.out.println("환불 시작");
                throw new failBuyException(e.getMessage(),reservationInsertDto.getPaymentId());
            }
            throw new RuntimeException(e.getMessage());
        }
         
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
