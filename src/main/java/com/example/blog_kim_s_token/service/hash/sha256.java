package com.example.blog_kim_s_token.service.hash;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import org.springframework.stereotype.Service;

@Service
public class sha256 {

    public String encrypt(){
        try {
           //가상계좌"nxva_sb_il 일반nxca_jt_il
            String text=String.format("%s%s%s%s%s%s%s","nxva_sb_il","card","TEST7845","20210913","132000","500","ST1009281328226982205");
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
