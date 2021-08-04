package com.example.blog_kim_s_token.service.cookie;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

@Service
public class cookieService {
    
    public void cookieFactory(HttpServletResponse response,String[] cookiesNames,String[] cookiesValues) {
        for(int i=0;i<cookiesNames.length;i++){
            Cookie cookie=new Cookie(cookiesNames[i],cookiesValues[i]);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
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
