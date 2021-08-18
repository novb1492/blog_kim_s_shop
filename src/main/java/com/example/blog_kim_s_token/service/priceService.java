package com.example.blog_kim_s_token.service;

import com.example.blog_kim_s_token.model.price.seatPriceDto;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.stereotype.Service;

@Service
public class priceService {
    
    public JSONObject getTotalSeatPrice(seatPriceDto seatPriceDto) {
        System.out.println("getTotalSeatPrice");
        System.out.println(seatPriceDto.getTimes().size()+"선택 자리수 ");
        return utillService.makeJson(true,seatPriceDto.getTimes().size()*500);
    }
}
