package com.example.blog_kim_s_token.service.hash;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class sha256 {

    public String encrypt(){
        try {
            String text=String.format("%s%s%s%s%s%s%s","nxca_jt_il","card","TEST0123456789","20210913","132000","500원","ST1009281328226982205");
            StringBuffer sb = new StringBuffer();
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            if (text != null) {
                sh.update(text.getBytes("UTF-8"));
                byte[] byteData = sh.digest();
                for (int i = 0; i < byteData.length; i++) {
                    sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                }
                return sb.toString();
            }
            return null;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("암호화 실패");
        }
    }
}
