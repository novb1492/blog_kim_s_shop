package com.example.blog_kim_s_token.service.fileUpload;

import java.io.File;
import java.util.UUID;

import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class fileUploadService {
    private final String windowLocal="C:/Users/Administrator/Desktop/blog/blog_kim_s_shop/src/main/resources/static/image/";
    private final String serverImageUploadUrl="http://localhost:8080/static/image/";
    JSONObject respone = new JSONObject();
    public JSONObject localImageUpload(MultipartFile multipartFile) {
        System.out.println("localImageUpload");
        try {
            String savename = null;
            String filename=multipartFile.getOriginalFilename();
            savename=UUID.randomUUID()+filename;
            String localLocation=windowLocal+savename;
            System.out.println(localLocation+" locallocation");
            System.out.println(savename+"sa");
                
            multipartFile.transferTo(new File(localLocation));
            Thread.sleep(1000);

            respone.put("bool",true );
            respone.put("url",serverImageUploadUrl+savename);
            return respone;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException("사진업로드에 실패했습니다");
        }
        
    }
}
