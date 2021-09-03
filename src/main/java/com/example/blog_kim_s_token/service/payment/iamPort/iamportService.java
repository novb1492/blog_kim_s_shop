package com.example.blog_kim_s_token.service.payment.iamPort;






import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.example.blog_kim_s_token.customException.setBuyerExeption;
import com.example.blog_kim_s_token.model.iamport.buyInforDto;
import com.example.blog_kim_s_token.model.iamport.impTokenDto;
import com.example.blog_kim_s_token.model.user.userDto;
import com.example.blog_kim_s_token.service.userService;
import com.example.blog_kim_s_token.service.payment.paymentService;
import com.example.blog_kim_s_token.service.payment.paymentabstract;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class iamportService {
    private final String imp_key="7336595505277037";
    private final String imp_secret="19412b4bca453060662162083d1ccc8ee7c53bd98a2f33faedd7ebc3e6ad4c359c36f899ebd6ddec";
    private final String impGetTokenUrl="https://api.iamport.kr/users/getToken";
    private final String impGetInforUrl="https://api.iamport.kr/payments/";
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private JSONObject body=new JSONObject();

    @Autowired
    private paymentService paymentService;
    @Autowired
    private userService userService;


    public paymentabstract confrimPayment(String impId,int totalPrice,String kind,HttpServletRequest request) {
        System.out.println("confrimPayment");
        return confrimBuy(getBuyInfor(impId),totalPrice,kind,request);
    }
    private String getToken() {
        System.out.println("getToken");
        headers.setContentType(MediaType.APPLICATION_JSON);
        body.put("imp_key", imp_key);
        body.put("imp_secret", imp_secret);
        try {
            HttpEntity<JSONObject>entity=new HttpEntity<JSONObject>(body, headers);
            impTokenDto impTokenDto=restTemplate.postForObject(impGetTokenUrl,entity, impTokenDto.class);
            System.out.println(impTokenDto.toString()+"결제토큰");
            return impTokenDto.getResponse().getAsString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getToken error");
            throw new RuntimeException("아임포트 토큰 발급에 실패했습니다");
        }finally{
            headers.clear();
            body.clear();
        }
    }
    public JSONObject getBuyInfor(String impId){
        System.out.println("getBuyInfor");
        String token=getToken();
        headers.add("Authorization", token);
        HttpEntity<JSONObject>entity=new HttpEntity<JSONObject>(headers);
        try {
            buyInforDto buyInforDto=restTemplate.postForObject(impGetInforUrl+impId, entity,buyInforDto.class);
            System.out.println(buyInforDto.toString()+" 결제정보");
            return buyInforDto.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getBuyInfor error");
            throw new RuntimeException("getBuyInfor 결제 정보 불러오기 실패");
        }finally{
            headers.clear();
            body.clear();
        }
    }
    private paymentabstract confrimBuy(JSONObject buyInfor,int totalPrice,String kind,HttpServletRequest request) {
        System.out.println("confrimBuy");
        String status=(String) buyInfor.get("status");
        String paymentId=(String)buyInfor.get("imp_uid");
        System.out.println(buyInfor.get("amount")+"결제총량"+totalPrice+" 결제되어야 하는 금액"+ status+" 결제상태");
        userDto userDto=userService.sendUserDto();
        if(confrimBuyerinfor(userDto, buyInfor, totalPrice)){
            paymentabstract paymentabstract=null;
            if(status.equals("paid")){
                System.out.println("결제된 상품");
                nomalPayment nomalPayment=new nomalPayment();
                selectPayCompany(buyInfor,nomalPayment);
                nomalPayment.setPaymentid(paymentId);
                nomalPayment.setPaymentid("paymentid");
                paymentService.insertPayment(nomalPayment, userDto, totalPrice);
                paymentabstract=nomalPayment;
            }else if(status.equals("ready")){
                System.out.println("가상계좌 요청 상품");
                String bankName=(String)buyInfor.get("vbank_name");
                String exprireDate=unixtimeToString(Long.parseLong(buyInfor.get("vbank_date").toString()));
                vbankPayment vbankPayment=new vbankPayment();
                vbankPayment.setBank(bankName);
                vbankPayment.setVbankNum((String)buyInfor.get("vbank_num"));
                vbankPayment.setPaymentid(paymentId);
                vbankPayment.setPayMethod("pay_method");
                vbankPayment.setStatus("ready");
                vbankPayment.setKind(kind);
                vbankPayment.setEndDate(exprireDate);
                vbankPayment.setUsedKind(bankName);
                paymentService.insertPayment(vbankPayment, userDto, totalPrice);
                HttpSession httpSession=request.getSession();
                httpSession.setAttribute("merchantUid", (String)buyInfor.get("merchant_uid"));
                httpSession.setAttribute("vbankDue",buyInfor.get("vbank_date"));
                httpSession.setAttribute("bank", (String)buyInfor.get("vbank_code"));
                httpSession.setAttribute("vbankHolder", (String)buyInfor.get("vbank_holder"));
                httpSession.setAttribute("amount", (int)buyInfor.get("amount"));
                paymentabstract=vbankPayment;
            }
            paymentabstract.setEmail(userDto.getEmail());
            paymentabstract.setName(userDto.getName());
            return paymentabstract;
        }
        System.out.println("결제 검증실패");
        throw new setBuyerExeption("결제검증 실패", buyInfor);
    } 
    private boolean confrimBuyerinfor(userDto userDto,JSONObject buyerInfor,int totalPrice) {
        if(totalPrice!=(int)buyerInfor.get("amount")){
            System.out.println("총액이 맞지않음");
        }else if(!userDto.getEmail().equals((String)buyerInfor.get("buyer_email"))){
            System.out.println("이메일이 일치하지않음");
        }else if(!userDto.getName().equals((String)buyerInfor.get("buyer_name"))){
            System.out.println("이름이 일치하지않음");
        }else{
            return true;
        }
        return false;
    }  
    private String unixtimeToString(long unixTime) {
        System.out.println("unixtimeToString");
        Date date = new Date(unixTime*1000L); 
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+9")); 
        return sdf.format(date);
    } 
    private void selectPayCompany(JSONObject buyInfor,nomalPayment nomalPayment) {
        System.out.println("selectPayCompany");
        String paymentMethod=(String)buyInfor.get("pay_method");
        String usedKind=null;
        if(paymentMethod.equals("point")){
            System.out.println("카카오결제");
            usedKind=(String)buyInfor.get("emb_pg_provider");
        }else if(paymentMethod.equals("card")){
            System.out.println("카드결제");
            usedKind=(String)buyInfor.get("card_name");
        }
        nomalPayment.setPayMethod(paymentMethod);
        nomalPayment.setStatus("paid");
        nomalPayment.setUsedKind(usedKind);
    }

    public boolean cancleBuy(String impId,int zeorOrPrice) {
        System.out.println("cancleBuy");
        try {
            String token=getToken();
            headers.add("Authorization",token);
            body.put("imp_uid", impId);
            if(zeorOrPrice!=0){
                body.put("amount", zeorOrPrice);
            }
            HttpEntity<JSONObject>entity=new HttpEntity<JSONObject>(body, headers);
            JSONObject respone= restTemplate.postForObject("https://api.iamport.kr/payments/cancel",entity,JSONObject.class);
            if((int)respone.get("code")==1){
                System.out.println(respone.get("message")+" 가상계좌 채번취소로 이동합니다");
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cancleBuy가 실패 했습니다 직접 환불 바랍니다");
            throw new RuntimeException("환불에 실패 했습니다 다시시도 바랍니다");
        }finally{
            headers.clear();
            body.clear();

        }

    }
    public void cancleVbank(String paymentid,JSONObject jsonObject) {
        System.out.println("cancleVbank");
        try {
            String token=getToken();
            headers.setContentType(MediaType.APPLICATION_JSON);  
            headers.add("Authorization", token);
            body.put("merchant_uid", jsonObject.get("merchant_uid"));
            body.put("vbank_due", jsonObject.get("vbank_due"));
            body.put("vbank_code",89);
            body.put("vbank_holder", jsonObject.get("vbank_holder"));
            body.put("amount", jsonObject.get("amount"));
            HttpEntity<JSONObject>entity=new HttpEntity<JSONObject>(body,headers);
            JSONObject respone= restTemplate.postForObject("https://api.iamport.kr/vbanks/"+paymentid,entity,JSONObject.class);
            System.out.println(respone);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cancleVbank error"+e.getMessage());
            throw new RuntimeException("가상계좌 채번 취소에 실패했습니다");
        }finally{
            headers.clear();
            body.clear();
        }
    

    }

    
}
