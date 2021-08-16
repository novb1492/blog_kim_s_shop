package com.example.blog_kim_s_token.service;


import com.example.blog_kim_s_token.model.csrf.csrfDao;
import com.example.blog_kim_s_token.model.csrf.csrfDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class csrfTokenService {
    
    private static final int csrfLength=10;
   
    @Autowired
    private csrfDao csrfDao;
    
    public static String getCsrfToken() {
        return utillService.GetRandomNum(csrfLength);
    }
    public void insertCsrfToken(int userID,String csrfToken,String email) {
        System.out.println("insertCsrfToken 입장");
        if(csrfDao.findByUserId(userID)==null){
            csrfDto dto=csrfDto.builder().csrfToken(csrfToken).userId(userID).email(email).build();
            csrfDao.save(dto);
            return;
        }
        System.out.println("기존 csrf있음");
        csrfDao.updateCsrfToken(csrfToken, userID);
    }
    public void deleteCsrfToken(String email) {
        System.out.println("deleteCsrfToken");
        csrfDto dto=csrfDao.findByEmail(email);
        if(dto==null){
            System.out.println("이미 로그아웃 사용자입니다 ");
            return;
        }
        if(!dto.getEmail().equals(email)){
            System.out.println("일치 하지 않는 로그아웃 시도");
            return;
        }
        csrfDao.delete(dto);
        System.out.println("csrf삭제완료");
    }  
}
