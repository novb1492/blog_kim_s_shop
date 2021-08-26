package com.example.blog_kim_s_token.service;

import com.example.blog_kim_s_token.model.product.getPriceDto;
import com.example.blog_kim_s_token.model.product.productDao;
import com.example.blog_kim_s_token.model.product.productDto;

import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class priceService {
    @Autowired
    private productDao productDao;
    
    private productDto selectProduct(String productName) {
        return productDao.findByProductName(productName);
    }
    public JSONObject responeTotalprice(getPriceDto getPriceDto) {
        System.out.println("responeTotalprice");
        JSONObject jsonObject=new JSONObject();
        if(getPriceDto.getCount().size()<=0){
            System.out.println("수량이 0임");
            jsonObject.put("totalPrice", 0);
            jsonObject.put("messege", "수량이 1보다 작습니다");
            return jsonObject;
        }

        productDto productDto=selectProduct(getPriceDto.getProductName());
        jsonObject.put("price",productDto.getPrice());
        jsonObject.put("totalPrice",getTotalPrice(productDto.getPrice(),getPriceDto.getCount().size()));
        return jsonObject;
    }
    private int getTotalPrice(int  price, int count) {
        System.out.println("getTotalPrice");
        return price*count;
    }
    public int getTotalPrice(String  productName, int count) {
        System.out.println("getTotalPrice");
        productDto productDto=selectProduct(productName);
        return getTotalPrice(productDto.getPrice(),count);
    }
}
