package com.example.blog_kim_s_token.service.fileUpload;

import java.io.File;
import java.util.UUID;

import com.example.blog_kim_s_token.service.fileUpload.aws.awsService;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class fileUploadService {
    private final String windowLocal="C:/Users/Administrator/Desktop/blog/blog_kim_s_shop/src/main/resources/static/image/";
    private final String serverImageUploadUrl="http://localhost:8080/static/image/";
    private final String awsS3Url="https://s3.ap-northeast-2.amazonaws.com/kimsshop/images/";
    private final String  imageBucktetName="kimsshop/images";
   

    JSONObject respone = new JSONObject();

    @Autowired
    private awsService awsService;

    public JSONObject awsS3ImageUpload(MultipartFile multipartFile) {
        String saveName=awsService.uploadAws(multipartFile,imageBucktetName);
        respone.put("uploaded",true ); //ckeditor5
        //respone.put("bool",true );// summernote
        respone.put("url",awsS3Url+saveName);
        return respone;
    }
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
            //respone.put("uploaded",true ); //ckeditor5
            respone.put("bool",true );// summernote
            respone.put("url",serverImageUploadUrl+savename);
            return respone;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException("사진업로드에 실패했습니다");
        }
        
    }
}
