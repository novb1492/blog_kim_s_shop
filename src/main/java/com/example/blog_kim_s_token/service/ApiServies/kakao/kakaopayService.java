package com.example.blog_kim_s_token.service.ApiServies.kakao;



import com.example.blog_kim_s_token.service.utillService;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class kakaopayService {
    private final String adminKey="ac5d7bd93834444767d1b59477e6f92f";
    private final String cid="TC0ONETIME";
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private MultiValueMap<String,Object> body=new LinkedMultiValueMap<>();
    
    public JSONObject getPayLink(JSONObject jsonObject) {
        System.out.println("getPayLink");
        try {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization","KakaoAK "+adminKey);
            body.add("cid", cid);
            body.add("partner_order_id",1234+"");
            body.add("partner_user_id", "kim@kim.com");
            body.add("item_name", "test상품");
            body.add("quantity", 1+"");
            body.add("total_amount", 1000+"");
            body.add("tax_free_amount", 0+"");
            body.add("approval_url", "http://localhost:8080/api/okKakaopay");
            body.add("cancel_url", "http://localhost:8080/api/cancleKakaopay");
            body.add("fail_url", "http://localhost:8080/api/failKakaopay");
            HttpEntity<MultiValueMap<String,Object>>entity=new HttpEntity<>(body,headers);
            JSONObject response=restTemplate.postForObject("https://kapi.kakao.com/v1/payment/ready", entity,JSONObject.class);
            System.out.println(response+" 카카오페이 통신요청 결과");
            return utillService.makeJson(true,  (String)response.get("next_redirect_pc_url"));
            //return (String)response.get("next_redirect_pc_url");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getPayLink error "+ e.getMessage());
            throw new RuntimeException("카카오 통신 실패");
        }
    }   
}
