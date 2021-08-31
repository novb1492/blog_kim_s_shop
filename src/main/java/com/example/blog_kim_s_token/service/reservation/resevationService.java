package com.example.blog_kim_s_token.service.reservation;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.blog_kim_s_token.customException.failBuyException;
import com.example.blog_kim_s_token.enums.reservationEnums;
import com.example.blog_kim_s_token.model.payment.vBankDto;
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
    private final int pagingNum=3;
    private final int limitedCancleHour=1;
  
 

    @Autowired
    private userService userService;
    @Autowired
    private reservationDao reservationDao;
    @Autowired
    private priceService priceService;
    @Autowired
    private paymentService paymentService;

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
                if(LocalDateTime.now().getDayOfMonth()==getTimeDto.getDate()&&LocalDate.now().getYear()==getTimeDto.getYear()&&LocalDate.now().getMonthValue()==getTimeDto.getMonth()){
                    System.out.println(getTimeDto.getDate()+" "+getTimeDto.getYear()+"월년");
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
        System.out.println("confrimContents");
        try {
            reservationInsertDto.setEmail(SecurityContextHolder.getContext().getAuthentication().getName());
            Collections.sort(reservationInsertDto.getTimes());
            confrimInsert(reservationInsertDto);
            payMentInterFace payMentInterFace=confrimPayment(reservationInsertDto);
            System.out.println(reservationInsertDto.getStatus()+" ready라면 가상계좌");
            if(reservationInsertDto.getStatus().equals("ready")){
                reservationEnums enums=checkVankTime(reservationInsertDto);
                if(enums.getBool()){
                    throw new Exception(enums.getMessege());
                }
            }
            insertReservation(reservationInsertDto);
            
            JSONObject result=new JSONObject();
            result.put("messege","예약에 성공했습니다");
            result.put("totalPrice",payMentInterFace.getTotalPrice());
            System.out.println(payMentInterFace.getExiredDate()+ "제일위");
            if(reservationInsertDto.getStatus().equals("ready")){
                System.out.println("응답에 가상계좌 추가");
                result.put("vbankNum", payMentInterFace.getVankNum());
                result.put("vbank", payMentInterFace.getUsedKind());
                result.put("expiredDate", payMentInterFace.getExiredDate());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("confrimContents error");
            throw new failBuyException(e.getMessage(), reservationInsertDto.getPaymentId());
        }
       
    }
    private payMentInterFace confrimPayment(reservationInsertDto reservationInsertDto) {
        System.out.println("confrimPayment");
        userDto userDto=userService.findEmail(reservationInsertDto.getEmail());
        List<Integer>times=reservationInsertDto.getTimes();
        int totalPrice=priceService.getTotalPrice(reservationInsertDto.getSeat(),times.size());

        reservationInsertDto.setUserId(userDto.getId());
        reservationInsertDto.setName(userDto.getName());
        payMentInterFace payMentInterFace=paymentService.makePaymentInter(reservationInsertDto.getPaymentId(), reservationInsertDto.getEmail(),userDto.getName(), totalPrice,kind,times.get(0));
        String status=paymentService.confrimPayment(payMentInterFace);
        System.out.println(status+" status+");
        reservationInsertDto.setStatus(status);
        reservationInsertDto.setUsedKind(payMentInterFace.getUsedKind());
        return payMentInterFace;
    }
    private void insertReservation(reservationInsertDto reservationInsertDto) {
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
        } catch (Exception e) {
           e.printStackTrace();
           System.out.println("insertReservation error");
           throw new failBuyException("예약 저장 실패",reservationInsertDto.getPaymentId());
        }
    }
    private void confrimInsert(reservationInsertDto reservationInsertDto){
        System.out.println("confrimInsert");
        try {
            List<mainReservationDto>array=reservationDao.findByEmailNative(reservationInsertDto.getEmail(),reservationInsertDto.getSeat());
            System.out.println(array.toString()+" 내역들");
            if(reservationInsertDto.getTimes().size()<=0){
                System.out.println("몇시간 쓸지 선택 되지 않음");
                throw new Exception("시간을 선택하지 않았습니다");
            }
            if(array!=null){
                for(mainReservationDto m:array){
                    for(int i=0;i<reservationInsertDto.getTimes().size();i++){
                        int hour=reservationInsertDto.getTimes().get(i);
                        String date=reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+hour+":00:00";
                        Timestamp DateAndTime=Timestamp.valueOf(date);
                        if(m.getDateAndTime().equals(DateAndTime)||utillService.compareDate(DateAndTime, LocalDateTime.now())){
                            System.out.println("이미 예약한 시간 발견or지난 날짜 예약시도");
                            throw new Exception("이미 예약한 시간 발견 이거나 지난 날짜 예약시도입니다 "+date);
                        }else if(getCountAlreadyInTime(DateAndTime,reservationInsertDto.getSeat())==maxPeopleOfTime){
                            System.out.println("예약이 다찬 시간입니다");
                            throw new Exception("예약이 가득찬 시간입니다 "+date);
                        }else if(hour<openTime||hour>closeTime){
                            System.out.println("영업 시간외 예약시도");
                            throw new Exception("영업 시간외 예약시도 입니다");
                        }
                    }
                }
            }
 
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("confrimInsert error");
            throw new failBuyException(e.getMessage(),reservationInsertDto.getPaymentId());
        }  
    }
    private reservationEnums checkVankTime(reservationInsertDto reservationInsertDto) {
        System.out.println("checkVankTime");

        if(utillService.compareDate(Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+reservationInsertDto.getTimes().get(0)+":00:00"), LocalDateTime.now())==false){
            LocalDateTime shortestTime=Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+reservationInsertDto.getTimes().get(0)+":00:00").toLocalDateTime();
            if(LocalDateTime.now().plusHours(minusHour).isAfter(shortestTime)){
                System.out.println("가상 계좌 제한시간은 최대 "+minusHour+"시간입니다");
                System.out.println(LocalDateTime.now().plusHours(minusHour)+" "+shortestTime+"시간");
                reservationEnums.yes.setMessete("가상 계좌 제한시간은 최대 "+minusHour+"시간입니다");
                return reservationEnums.yes;
            }
        }
        return reservationEnums.no;
    }
    public JSONObject getClientReservation(JSONObject JSONObject) {
        System.out.println("getClientReservation");
        System.out.println("시작일"+JSONObject.get("startDate"));
        System.out.println("종료일"+JSONObject.get("endDate"));
        try {
            JSONObject respone=new JSONObject();
            int nowPage=(int) JSONObject.get("nowPage");
            if(nowPage<=0){
                respone.put("bool", false);
                respone.put("messege", "페이지가 0보다 작습니다");
                return respone;
            }
            List<mainReservationDto>dtoArray=getClientReservationDTO(JSONObject, nowPage,respone);
            String[][] array=makeResponse(respone, dtoArray);

            respone.put("bool", true);
            respone.put("nowPage", nowPage);
            respone.put("reservations", array);
            return respone;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getClientReservation error");
            throw new RuntimeException("예약조회에 실패했습니다");
        }
    }
    private List<mainReservationDto>getClientReservationDTO(JSONObject jsonObject,int nowPage,JSONObject respone){
        System.out.println("getClientReservationDTO");
        String startDate=(String) jsonObject.get("startDate");
        String endDate=(String) jsonObject.get("endDate");
        List<mainReservationDto>dtoArray=new ArrayList<>();
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        int totalPage=0;
        int fisrt=0;
            if(startDate.isEmpty()&&endDate.isEmpty()){
                totalPage=utillService.getTotalpages(reservationDao.countByEmail(email), pagingNum);
                fisrt=utillService.getFirst(nowPage, pagingNum);
                dtoArray=reservationDao.findByEmailOrderByIdDescNative(email,fisrt-1,utillService.getEnd(fisrt, pagingNum)-fisrt+1);
            }else{
                totalPage=utillService.getTotalpages(reservationDao.countByEmailNative(email,Timestamp.valueOf(startDate+" "+"00:00:00"),Timestamp.valueOf(endDate+" 00:00:00")), pagingNum);
                fisrt=utillService.getFirst(nowPage, pagingNum);
                dtoArray=reservationDao.findByEmailOrderByIdBetweenDescNative(email,Timestamp.valueOf(startDate+" "+"00:00:00"),Timestamp.valueOf(endDate+" 00:00:00"),fisrt-1,utillService.getEnd(fisrt, pagingNum)-fisrt+1);
            }
        respone.put("totalPage", totalPage);
        return dtoArray;
    }
    private String[][] makeResponse(JSONObject jsonObject,List<mainReservationDto>dtoArray) {
        System.out.println("makeResponse");
        String[][] array=new String[dtoArray.size()][7];
            int temp=0;
            DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            for(mainReservationDto m:dtoArray){
                array[temp][0]=Integer.toString(m.getId());
                array[temp][1]=m.getSeat();
                array[temp][2]=dateFormat.format(m.getCreated());
                array[temp][3]=dateFormat.format(m.getDateAndTime());
                if(LocalDateTime.now().plusHours(limitedCancleHour).isAfter(m.getDateAndTime().toLocalDateTime())){
                    System.out.println("현재시간이 사용시간 이후입니다");
                    array[temp][4]=Integer.toString(cantFlag);
                }
                if(m.getStatus().equals("ready")){
                    vBankDto vBankDto=paymentService.selectVbankProduct(m.getPaymentId());
                    array[temp][4]="미입금";
                    array[temp][5]=vBankDto.getBank()+" "+vBankDto.getBankNum();
                    array[temp][6]=vBankDto.getEndDate().toString();
                }else{
                    array[temp][4]="결제완료";
                    array[temp][5]="결제가 완료된 상태입니다";
                    array[temp][6]="결제가 완료된 상태입니다";
                }
                temp++;
            }
        return array;
    }
    @Transactional(rollbackFor = Exception.class)
    public JSONObject deleteReservation(JSONObject jsonObject) {
        System.out.println("deleteReservation");
        try {
            List<String>ridArray=(List<String>)jsonObject.get("rid");
            if(ridArray.size()<=0){
                System.out.println("선택한 예약이없습니다");
                return utillService.makeJson(false, "선택한 예약이 없습니다"); 
            }
            List<mainReservationDto>dtoArray=new ArrayList<>();
            for(int i=0;i<ridArray.size();i++){
                System.out.println(ridArray.get(i)+" 취소예약시도 번호");
                dtoArray.add(reservationDao.findById(Integer.parseInt(ridArray.get(i))).orElseThrow(()->new IllegalArgumentException("존재하지 않는 예약입니다")));
            }
            for(mainReservationDto dto:dtoArray){
                reservationEnums enums=confrimCancle(dto);
                if(enums.getBool()==false){
                    System.out.println("예약환불 중 조건에 맞지 않는 예약발견");
                    throw new Exception(enums.getMessege());
                }
                String paymentId=dto.getPaymentId();
                String seat=dto.getSeat();
                reservationDao.delete(dto);
                if(dto.getStatus().equals("paid")){
                    paymentService.cancleBuy(paymentId, priceService.getTotalPrice(seat,1));
                }
            }
          
            return utillService.makeJson(true, "예약취소 성공");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    private reservationEnums confrimCancle(mainReservationDto mainReservationDto) {
        String messege=null;
        String enumValue="fail";
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        if(LocalDateTime.now().plusHours(limitedCancleHour).isAfter(mainReservationDto.getDateAndTime().toLocalDateTime())){
            messege="예약시간 한시간 전까지 취소가능합니다";
        }else if(!email.equals(mainReservationDto.getEmail())){
            messege="예약과 예약자 이메일이 다릅니다";
        }else{
            enumValue="can";
        }
        reservationEnums.valueOf(enumValue).setMessete(messege);
        return reservationEnums.valueOf(enumValue);
    }
}
