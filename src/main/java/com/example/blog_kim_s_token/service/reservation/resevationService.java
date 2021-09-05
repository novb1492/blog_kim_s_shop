package com.example.blog_kim_s_token.service.reservation;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.example.blog_kim_s_token.customException.failBuyException;
import com.example.blog_kim_s_token.enums.reservationEnums;
import com.example.blog_kim_s_token.model.payment.paidDao;
import com.example.blog_kim_s_token.model.payment.paidDto;
import com.example.blog_kim_s_token.model.payment.vBankDto;
import com.example.blog_kim_s_token.model.reservation.*;
import com.example.blog_kim_s_token.model.reservation.getDateDto;
import com.example.blog_kim_s_token.model.reservation.getTimeDto;
import com.example.blog_kim_s_token.model.reservation.reservationInsertDto;
import com.example.blog_kim_s_token.service.priceService;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.payment.paymentService;
import com.example.blog_kim_s_token.service.payment.paymentabstract;
import com.example.blog_kim_s_token.service.payment.iamPort.iamportService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class resevationService {

    private final int openTime=9;
    private final int closeTime=22;
    private final int maxPeopleOfDay=60;
    private final int maxPeopleOfTime=6;
    private final int cantFlag=100;
    private final String kind="reservation";
    private final int pagingNum=3;

    @Value("${payment.minusHour}")
    private  int minusHour;
   
    @Value("${payment.limitedCancleHour}")
    private  int limitedCancleHour;

    @Autowired
    private iamportService iamportService;
  

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
    public JSONObject confrimContents(reservationInsertDto reservationInsertDto,HttpServletRequest request) {
        System.out.println("confrimContents");
        try {
            Collections.sort(reservationInsertDto.getTimes());
            List<Integer>times=reservationInsertDto.getTimes();
            int totalPrice=priceService.getTotalPrice(reservationInsertDto.getSeat(),times.size());
            paymentabstract paymentabstract=iamportService.confrimPayment(reservationInsertDto.getPaymentId(), totalPrice,kind,request);
            reservationInsertDto.setStatus(paymentabstract.getStatus());
            reservationInsertDto.setUsedKind(paymentabstract.getUsedKind());
            reservationInsertDto.setEmail(paymentabstract.getEmail());
            reservationInsertDto.setName(paymentabstract.getName());
            confrimInsert(reservationInsertDto);
            insertReservation(reservationInsertDto);
            
            String messege="예약이 완료되었습니다";
            if(reservationInsertDto.getStatus().equals("ready")){
                messege=messege+" 발급된 가상계좌는 예약내역페이지를 확인해주세요";
            }
            return utillService.makeJson(true, messege);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("confrimContents error");
            throw new failBuyException(e.getMessage(), reservationInsertDto.getPaymentId(),null);
        }
       
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
           throw new RuntimeException("예약 저장 실패");
        }
    }
    private void confrimInsert(reservationInsertDto reservationInsertDto){
        System.out.println("confrimInsert");
            List<mainReservationDto>array=reservationDao.findByEmailNative(reservationInsertDto.getEmail(),reservationInsertDto.getSeat());
            System.out.println(array.toString()+" 내역들");
            if(reservationInsertDto.getTimes().size()<=0){
                System.out.println("몇시간 쓸지 선택 되지 않음");
                throw new RuntimeException("시간을 선택하지 않았습니다");
            }
            if(array!=null){
                List<Integer>times=reservationInsertDto.getTimes();
                System.out.println("show");
                for(mainReservationDto m:array){
                    for(int i:times){
                        String date=reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+i+":00:00";
                        Timestamp DateAndTime=Timestamp.valueOf(date);
                        System.out.println(DateAndTime+" show");
                        if(m.getDateAndTime().equals(DateAndTime)||utillService.compareDate(DateAndTime, LocalDateTime.now())){
                            System.out.println("이미 예약한 시간 발견or지난 날짜 예약시도");
                            throw new RuntimeException("이미 예약한 시간 발견 이거나 지난 날짜 예약시도입니다 "+date);
                        }else if(getCountAlreadyInTime(DateAndTime,reservationInsertDto.getSeat())==maxPeopleOfTime){
                            System.out.println("예약이 다찬 시간입니다");
                            throw new RuntimeException("예약이 가득찬 시간입니다 "+date);
                        }else if(i<openTime||i>closeTime){
                            System.out.println("영업 시간외 예약시도");
                            throw new RuntimeException("영업 시간외 예약시도 입니다");
                        }
                    }
                }
            }
            if(reservationInsertDto.getStatus().equals("ready")){
                System.out.println("가상계좌 시간 검증");
                if(utillService.compareDate(Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+reservationInsertDto.getTimes().get(0)+":00:00"), LocalDateTime.now())==false){
                    LocalDateTime shortestTime=Timestamp.valueOf(reservationInsertDto.getYear()+"-"+reservationInsertDto.getMonth()+"-"+reservationInsertDto.getDate()+" "+reservationInsertDto.getTimes().get(0)+":00:00").toLocalDateTime();
                    if(LocalDateTime.now().plusHours(minusHour).isAfter(shortestTime)){
                        System.out.println("가상 계좌 제한시간은 최대 "+minusHour+"시간입니다");
                        throw new RuntimeException("가상 계좌 제한시간은 최대 "+minusHour+"시간입니다");
                    }
                }
            }
    }
    public JSONObject getClientReservation(JSONObject JSONObject) {
        System.out.println("getClientReservation");
        String startDate=(String) JSONObject.get("startDate");
        String endDate=(String) JSONObject.get("endDate");
        System.out.println("시작일"+startDate);
        System.out.println("종료일"+endDate);
        try {
            JSONObject respone=new JSONObject();
            int nowPage=(int) JSONObject.get("nowPage");
            reservationEnums enums=confrimDateAndPage(nowPage,startDate,endDate);
            if(enums.getBool()==false){
                System.out.println("조건 안맞음");
                return utillService.makeJson(enums.getBool(), enums.getMessege());
            }
            List<mainReservationDto>dtoArray=getClientReservationDTO(startDate,endDate,nowPage,respone);
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
    private reservationEnums confrimDateAndPage(int nowPage,String startDate,String endDate){
        System.out.println("confrimDate");
        String enumName="fail";
        String messege=null;
        if(nowPage<=0){
            System.out.println("페이지가 0보다 작거나 같습니다 ");
            messege="페이지가 0보다 작거나 같습니다";
        }
        else if(startDate.isEmpty()&&!endDate.isEmpty()){
            System.out.println("시작날이 없습니다 ");
            messege="시작날이 없습니다";
        }else if(!startDate.isEmpty()&&endDate.isEmpty()){
            System.out.println("끝나는 날이 없습니다 ");
            messege="끝나는 날이 없습니다";
        }else if(!startDate.isEmpty()&&!endDate.isEmpty()){
            if(LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE).isAfter(LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE))){
                System.out.println("날짜 선택이 잘못되었습니다 ");
                messege="날짜 선택이 잘못되었습니다";
            }else{
                enumName="yes";
            }
        }else{
            enumName="yes";
        }
        reservationEnums.valueOf(enumName).setMessete(messege);
        return reservationEnums.valueOf(enumName);
    }
    private List<mainReservationDto>getClientReservationDTO(String startDate,String endDate,int nowPage,JSONObject respone){
        System.out.println("getClientReservationDTO");
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
        String[][] array=new String[dtoArray.size()][9];
            int temp=0;
            DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            for(mainReservationDto m:dtoArray){
                array[temp][0]=Integer.toString(m.getId());
                array[temp][1]=m.getSeat();
                array[temp][2]=dateFormat.format(m.getCreated());
                array[temp][3]=dateFormat.format(m.getDateAndTime());
                if(m.getStatus().equals("ready")){
                    vBankDto vBankDto=paymentService.selectVbankProduct(m.getPaymentId());
                    array[temp][4]="미입금";
                    array[temp][5]=vBankDto.getBank()+" "+vBankDto.getBankNum();
                    array[temp][6]=vBankDto.getEndDate().toString();
                    array[temp][7]=vBankDto.getPrice()+"";
                    array[temp][8]=null;
                }else{
                    paidDto paidDto=paymentService.selectPaidProduct(m.getPaymentId());
                    array[temp][4]="결제완료";
                    array[temp][5]=m.getUsedPayKind();
                    array[temp][6]=m.getCreated().toString();
                    array[temp][7]=paidDto.getTotalPrice()+"";
                    if(LocalDateTime.now().plusHours(limitedCancleHour).isAfter(m.getDateAndTime().toLocalDateTime())){
                        System.out.println("현재시간이 사용시간 이후입니다");
                        array[temp][8]=Integer.toString(cantFlag);
                    }
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
                System.out.println(dto.toString()+" cancle");
                if(dto.getStatus().equals("paid")){
                    System.out.println("결제된 상품 취소시도");
                    reservationDao.deleteReservationPaidproduct(dto.getId());
                    iamportService.cancleBuy(paymentId, priceService.getTotalPrice(seat,1));
                }else if(dto.getStatus().equals("ready")){
                    System.out.println("미결제 상품 취소시도");
                    reservationDao.deleteReservationVbankproduct(dto.getId());
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
    public void readyTopaid(String paymentId) {
        System.out.println("readyTopaid");
        List<mainReservationDto>array=reservationDao.findByPaymentId(paymentId);
        for(mainReservationDto m:array){
            m.setStatus("paid");
        }
        System.out.println("예약테이블 paid로 변경완료");
    }
}
