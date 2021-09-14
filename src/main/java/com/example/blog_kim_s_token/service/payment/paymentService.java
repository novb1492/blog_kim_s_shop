package com.example.blog_kim_s_token.service.payment;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;

import com.example.blog_kim_s_token.customException.failBuyException;
import com.example.blog_kim_s_token.enums.aboutPayEnums;
import com.example.blog_kim_s_token.model.payment.getHashInfor;
import com.example.blog_kim_s_token.model.payment.getVankDateDto;
import com.example.blog_kim_s_token.model.payment.paidDao;
import com.example.blog_kim_s_token.model.payment.paidDto;
import com.example.blog_kim_s_token.model.payment.tryCanclePayDto;
import com.example.blog_kim_s_token.model.payment.vBankDto;
import com.example.blog_kim_s_token.model.payment.vbankDao;
import com.example.blog_kim_s_token.model.product.productDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.priceService;
import com.example.blog_kim_s_token.service.utillService;
import com.example.blog_kim_s_token.service.ApiServies.kakao.kakaoService;
import com.example.blog_kim_s_token.service.hash.aes256;
import com.example.blog_kim_s_token.service.hash.sha256;
import com.example.blog_kim_s_token.service.payment.iamPort.iamportService;
import com.example.blog_kim_s_token.service.payment.iamPort.nomalPayment;
import com.example.blog_kim_s_token.service.payment.iamPort.tryImpPayDto;
import com.example.blog_kim_s_token.service.payment.iamPort.vbankPayment;
import com.example.blog_kim_s_token.service.reservation.reservationService;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class paymentService {

    @Autowired
    private paidDao paidDao;
    @Autowired
    private vbankDao vbankDao; 
    @Autowired
    private iamportService iamportService;
    @Autowired
    private reservationService resevationService;
    @Autowired
    private priceService priceService;
    @Value("${payment.period}")
    private  int period;
    @Value("${payment.minusHour}")
    private  int minusHour;
    @Value("${kakao.kakaoPay.cid}")
    private String kakaoPayCid;
    @Autowired
    private kakaoService kakaoService;
    @Autowired
    private sha256 sha256;
    @Autowired
    private aes256 aes256;

    
    public vBankDto selectVbankProduct(String paymentId) {
        return  vbankDao.findByPaymentId(paymentId).orElseThrow(()->new RuntimeException("입금대기를 찾을 수없습니다"+paymentId));
    }
    public paidDto selectPaidProduct(String paymentId) {
        return paidDao.findByPaymentId(paymentId).orElseThrow(()->new RuntimeException("입금확인을 찾을 수없습니다"+paymentId));
    }
    public void insertPayment(nomalPayment nomalPayment,userDto userDto,int totalPrice) {
        System.out.println("insertPayment");
        paidDto dto=paidDto.builder().email(userDto.getEmail())
                                    .kind(nomalPayment.getKind())
                                    .name(userDto.getName())
                                    .payMethod(nomalPayment.getPayMethod())
                                    .paymentId(nomalPayment.getPaymentid())
                                    .status(nomalPayment.getStatus())
                                    .usedKind(nomalPayment.getUsedKind())
                                    .totalPrice(totalPrice)
                                    .build();
                                    paidDao.save(dto);
                                    System.out.println("결제테이블 저장 완료");

    }
    public void insertPayment(nomalPayment nomalPayment,int totalPrice) {
        System.out.println("insertPayment");
        paidDto dto=paidDto.builder().email(nomalPayment.getEmail())
                                    .kind(nomalPayment.getKind())
                                    .name(nomalPayment.getName())
                                    .payMethod(nomalPayment.getPayMethod())
                                    .paymentId(nomalPayment.getPaymentid())
                                    .status(nomalPayment.getStatus())
                                    .usedKind(nomalPayment.getUsedKind())
                                    .totalPrice(totalPrice)
                                    .build();
                                    paidDao.save(dto);
                                    System.out.println("결제테이블 저장 완료");

    }
    public void insertPayment(vbankPayment vbankPayment,userDto userDto,int totalPrice) {
        System.out.println("insertPayment");
        vBankDto dto=vBankDto.builder().email(userDto.getEmail())
                                    .name(userDto.getName())
                                    .bank(vbankPayment.getBank())
                                    .bankNum(vbankPayment.getVbankNum())
                                    .endDate(Timestamp.valueOf(vbankPayment.getEndDate()))
                                    .paymentId(vbankPayment.getPaymentid())
                                    .status("ready")
                                    .kind(vbankPayment.getKind())
                                    .vbankTotalPrice(totalPrice)
                                    .bankCode(vbankPayment.getBankCode())
                                    .pgName(vbankPayment.getPgName())
                                    .endDateUnixTime(vbankPayment.getUnixTime())
                                    .merchant_uid(vbankPayment.getMerchantUid())
                                    .build();
                                    vbankDao.save(dto);
                                    System.out.println("vbnk테이블 저장 완료");
    }
    public JSONObject  getVbankDate(getVankDateDto getVankDateDto) {
        System.out.println("getVbankDate");
        try {
            if(getVankDateDto.getKind().equals(aboutPayEnums.reservation.getString())){
                Calendar getToday = Calendar.getInstance();
                getToday.setTime(new Date()); 
                String requestDate=getVankDateDto.getYear()+"-"+getVankDateDto.getMonth()+"-"+getVankDateDto.getDate();
                long diffDays = utillService.getDateGap(getToday, requestDate);
                Collections.sort(getVankDateDto.getTimes());
                int shortestTime=getVankDateDto.getTimes().get(0);
                checkTime(getVankDateDto.getYear(),getVankDateDto.getMonth(),getVankDateDto.getDate(),shortestTime);
                return utillService.makeJson(true,getVbankDate(diffDays, shortestTime, requestDate));
            }else{
                return utillService.makeJson(true,getVbankDate());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getVbankDate error "+e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    public void checkTime(int year,int month,int date,int time) {
        System.out.println("가상계좌 시간 검증" +time);
        LocalDateTime shortestTime=Timestamp.valueOf(year+"-"+month+"-"+date+" "+time+":00:00").toLocalDateTime();
        if(LocalDateTime.now().plusHours(minusHour).isAfter(shortestTime)){
            System.out.println("가상 계좌 제한시간은 최대 "+minusHour+"시간입니다");
            throw new RuntimeException("가상 계좌 제한시간은 최대 "+minusHour+"시간입니다");
        }
    }
    private String getVbankDate(long diffDays,int shortestTime,String requestDate) {
        System.out.println("getVbankDate");   
        String expiredDate=null;
        if(diffDays<period){
            System.out.println(shortestTime+" 가장작은시간");
            expiredDate=requestDate+" "+(shortestTime-minusHour)+":00:00";
            System.out.println(expiredDate+" 새로만든 기한");
            String[]temp=expiredDate.split(" ");
            String time=temp[1];
            temp=temp[0].split("-");
            if(temp[1].length()<2){
                System.out.println("10월보다작음");
                temp[1]="0"+temp[1];
            }
            if(temp[2].length()<2){
                System.out.println("10일보다작음");
                temp[2]="0"+temp[2];
            }
            String[] splitTime=time.split(":");
            if(splitTime[0].length()<2){
                splitTime[0]="0"+splitTime[0];
                time=splitTime[0]+":"+splitTime[1]+":"+splitTime[2];
            }
            expiredDate=temp[0]+"-"+temp[1]+"-"+temp[2]+" "+time;
 
        }else{
            System.out.println("예약 일자가 "+period+"이상임");
            expiredDate=getVbankDate();
        }
        System.out.println(expiredDate+" 최종");
        return expiredDate;
    }
    private String getVbankDate() {
        System.out.println("getVbankDate");
        String expiredDate=LocalDateTime.now().plusDays(period).toString();
        expiredDate=expiredDate.replace("T", " ");
        return expiredDate;
    }
    @Transactional(rollbackFor = Exception.class)
    public void vbankOk(JSONObject jsonObject) {
        System.out.println("vbankOk");
        System.out.println(jsonObject+" payment");
        try {
        String status=(String) jsonObject.get("status");
        String merchantUid=(String) jsonObject.get("merchant_uid");
        if(status.equals("paid")&&merchantUid.startsWith("vbank")){
            System.out.println("가상계좌가 입금 확인됨");
            String paymentId=(String) jsonObject.get("imp_uid");
            vBankDto vBankDto=selectVbankProduct(paymentId);
            if(vBankDto.getKind().equals("reservation")){
                System.out.println("예약시도 가상계좌였습니다");
                resevationService.readyTopaid(paymentId);
            }else{
                System.out.println("상품시도 가상계좌였습니다");
            }
            nomalPayment nomalPayment=new nomalPayment();
            nomalPayment.setEmail(vBankDto.getEmail());
            nomalPayment.setName(vBankDto.getName());
            nomalPayment.setUsedKind(vBankDto.getBank()+" "+vBankDto.getBankNum()+" "+vBankDto.getPgName()+" "+vBankDto.getBankCode());
            nomalPayment.setPayMethod("vbank");
            nomalPayment.setKind(vBankDto.getKind());
            nomalPayment.setPaymentid(vBankDto.getPaymentId());
            nomalPayment.setStatus("paid");
            insertPayment(nomalPayment, vBankDto.getVbankTotalPrice());
            vbankDao.delete(vBankDto);
            return;
        }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("vbankOk error"+e.getMessage());
            throw new RuntimeException(e.getMessage());
        } 
    }
    public void updatePaidProductForCancle(String paymentid,int minusPrice) {
        paidDto paidDto=selectPaidProduct(paymentid);
        int price=paidDto.getTotalPrice();
        int newPrice=price-minusPrice;
        if(newPrice==0){
            System.out.println("취소후 전액환불예정");
            paidDao.delete(paidDto);
            return;
        }
        paidDto.setTotalPrice(newPrice);
    }
    public int updateVbank(String paymentid,int minusPrice) {
        vBankDto vBankDto=selectVbankProduct(paymentid);
        int newPrice=minusPrice(vBankDto.getVbankTotalPrice(),minusPrice);
        if(newPrice==0){
            vbankDao.delete(vBankDto);
            System.out.println("잔액 0 채번 취소");
            
        }else{
            vBankDto.setVbankTotalPrice(newPrice);
        }
         return newPrice;
    }
    public int minusPrice(int totalPrice,int minusPrice) {
        int newPrice=totalPrice-minusPrice;
        if(newPrice==0||newPrice>0){
            return newPrice;
        }
        throw new RuntimeException("환불 잔액이 총액보다 큽니다");
    }
    public Map<String,Object> getTotalPriceAndOther(String[][] itemArray,String kind) {
        System.out.println("getTotalPriceAndOther");
        int itemArraySize=itemArray.length;
        int totalPrice=0;
        String itemName="";
        int count=0;
        List<Integer>timesOrSize=new ArrayList<>();
        Map<String,Object>result=new HashMap<>();
        for(int i=0;i<itemArraySize;i++){
            totalPrice+=priceService.getTotalPrice(itemArray[i][0],Integer.parseInt(itemArray[i][1]));
            itemName+=itemArray[i][0];
            if(i!=itemArraySize-1){
                itemName+=",";
            }
            count+=Integer.parseInt(itemArray[i][1]);
            if(kind.equals(aboutPayEnums.reservation.getString())){
                System.out.println("예약 상품 입니다 시간 분리 시작");
                timesOrSize.add(Integer.parseInt(itemArray[i][2]));
                if(i==itemArraySize-1){
                    System.out.println("시간 분리 완료");
                    result.put("timesOrSize", timesOrSize);
                }
            }else if(kind.equals(aboutPayEnums.product.getString())){
                System.out.println("일반 상품입니다 사이즈 분리시작");
            }
        }
        result.put("totalPrice", totalPrice);
        result.put("itemName", itemName);
        result.put("count", count);
        return result;
    }
    public void confrimProduct(int requestTotalPrice,int totalPrice,int count,String productName) {
        System.out.println("confrimProduct");
        String[] splitName=productName.split(",");
        int splitNameSize=splitName.length;
        String messege="검증실패";
        for(int i=0;i<splitNameSize;i++){
           productDto productDto=priceService.selectProduct(splitName[i]);
           int remainCount=productDto.getCount();
           if(requestTotalPrice!=totalPrice){
               System.out.println("가격이 변조되었습니다");
               messege="가격이 변조되었습니다";
               break;
           }else if(remainCount<=0||remainCount-count<=0){
               System.out.println("재고 부족");
               messege="재고가 없거나 요청수량 보다 적습니다"+splitName[i];
               break;
           }else{
               System.out.println("confrimProduct 통과");
               if(i==splitNameSize-1){
                System.out.println("confrimProduct 완전 통과");
                return;
               }
           }
        }
        throw new RuntimeException(messege);
    }
    @Transactional(rollbackFor = Exception.class)
    public JSONObject confrimPayment(tryImpPayDto tryImpPayDto,HttpServletRequest request) {
        System.out.println("confrimPayment");
        System.out.println(tryImpPayDto);
        String impid=tryImpPayDto.getImpid();
        try {
            String[][] itemArray=tryImpPayDto.getItemArray();
            String kind=aboutPayEnums.valueOf(tryImpPayDto.getKind()).getString();
            Map<String,Object>result=getTotalPriceAndOther(itemArray, kind);
            System.out.println(result+" 상품정보 가공");
            int totalPrice=(int)result.get("totalPrice");
            String itemName=(String)result.get("itemName");
            int count=(int)result.get("count");
            List<Integer>timeOrsize=(List<Integer>)result.get("timesOrSize");
            confrimProduct(tryImpPayDto.getTotalPrice(),totalPrice,count,itemName);
            paymentabstract paymentabstract=iamportService.confrimBuy(iamportService.getBuyInfor(impid),totalPrice,kind,request); 
            if(kind.equals(aboutPayEnums.reservation.getString())){
                System.out.println("예약 상품 결제");
                String status=paymentabstract.getStatus();
                if(status.equals(aboutPayEnums.statusReady.getString())){
                    Collections.sort(timeOrsize);
                }
                resevationService.doReservation(paymentabstract.getEmail(),paymentabstract.getName(), impid, itemArray,tryImpPayDto.getOther(), timeOrsize,status,paymentabstract.getUsedKind());
            }else if(kind.equals(aboutPayEnums.product.getString())){
                System.out.println("일반 상품 결제");
            }
            return utillService.makeJson(true, "완료되었습니다");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("confrimPayment error");
            throw new failBuyException(e.getMessage(),impid);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public JSONObject canclePay(tryCanclePayDto tryCanclePayDto ) {
        System.out.println("canclePay");
        try {
            String kind=aboutPayEnums.valueOf(tryCanclePayDto.getKind()).getString();
            List<Integer> idArray=tryCanclePayDto.getId();
            if(kind.equals(aboutPayEnums.reservation.getString())){
                System.out.println("예약 상품 취소 시도");
                resevationService.deleteReservation(idArray);
            }else if(kind.equals(aboutPayEnums.product.getString())){
                System.out.println("일반 상품 취소 시도");
            }
            return utillService.makeJson(true, "완료되었습니다");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("canclePay error"+ e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    public void requestUpdateVbankBeforePaid(String paymentid,int newPrice,String unixTime) {
        System.out.println("requestUpdateVbankBeforePaid");
        iamportService.requestUpdateVbank(paymentid, newPrice, unixTime);
    }
    public void requestCancleToKakaoPay(String tid,int price) {
        System.out.println("requestCancleToKakaoPay");
        MultiValueMap<String,Object> body=new LinkedMultiValueMap<>();
        body.add("cid", kakaoPayCid);
        body.add("tid", tid);
        body.add("cancel_amount", price);
        body.add("cancel_tax_free_amount",0);
        kakaoService.cancleKakaopay(body);
    }
    public void canclePay(JSONObject body) {
        iamportService.cancleBuy(body);
    }
    public Map<String,Object> getVankInforInDb(paidDto paidDto) {
        System.out.println("getVankInforInDb");
        try {
            String[] splitVankInfor=paidDto.getUsedKind().split(" ");
            Map<String,Object>map=new HashMap<>();
            map.put("refund_holder",splitVankInfor[2]);
            map.put("refund_bank",splitVankInfor[1] );
            map.put("refund_account",splitVankInfor[3] );
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getVankInforInDb errpr"+e.getMessage());
            throw new RuntimeException("가상계좌 정보 추출에 실패했습니다");
        }
    }
    public JSONObject makeTohash(getHashInfor getHashInfor) {
        System.out.println("makeTohash");
        JSONObject response=new JSONObject();
        try {
            String kind=aboutPayEnums.valueOf(getHashInfor.getKind()).getString();
            System.out.println(kind); 
            String mchtTrdNo=kind+utillService.GetRandomNum(10);
            getHashInfor.setMchtTrdNo(mchtTrdNo);
            getHashInfor.setRequestDate("20210913");
            getHashInfor.setRequestTime("132000");
            String pktHash=sha256.encrypt(getHashInfor);
            String hashPrice=aes256.encrypt(getHashInfor.getTotalPrice()+"");

            response.put("mchtTrdNo", mchtTrdNo);
            response.put("trdAmt", hashPrice);
            response.put("trdDt", getHashInfor.getRequestDate());
            response.put("trdTm", getHashInfor.getRequestTime());
            response.put("pktHash", pktHash);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("makeTohash error"+e.getMessage());
            throw new RuntimeException("구매정보 해시화 실패");
        }
    }
    public void okSettle() {
        
    }


}
