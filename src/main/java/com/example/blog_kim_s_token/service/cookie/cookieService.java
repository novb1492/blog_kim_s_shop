package com.example.blog_kim_s_token.service.cookie;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

@Service
public class cookieService {
    
    public void cookieFactory(HttpServletResponse response,String[][] namesAndValues) {
        for(int i =0;i<namesAndValues.length;i++){
            for(int ii=0;ii<namesAndValues[i].length;){
                Cookie cookie=new Cookie(namesAndValues[i][ii],namesAndValues[i][ii+1]);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                response.addCookie(cookie);
                break;
            }
        }
    }
    public List<Object> openCookie(Cookie[] cookies,List<String>cookiesName) {
        System.out.println("openCookie");
        List<Object>cList=new ArrayList<>();
        for(String s:cookiesName){
            for(Cookie c:cookies){
                if(c.getName().equals(s)){
                    cList.add(c.getValue());
                    break;
                }
                
            }
        }
        return cList;
    }
}
