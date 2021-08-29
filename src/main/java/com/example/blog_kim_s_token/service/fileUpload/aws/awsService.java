package com.example.blog_kim_s_token.service.fileUpload.aws;


import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.amazonaws.services.s3.AmazonS3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class awsService {
    private final String  bucktetName="kimsshop/images";
    
    @Autowired
    private AmazonS3 amazonS3;
    
    public String  uploadAws(MultipartFile multipartFile) {
        System.out.println("uploadAws");
        File file=convert(multipartFile);
        String saveName=file.getName();
        amazonS3.putObject(bucktetName,saveName, file);
        file.delete();
        System.out.println("파일업로드 완료");
        return saveName;
    }
    public void deleteFile(String fileName) {
        amazonS3.deleteObject(bucktetName, fileName);
    }
    private File convert(MultipartFile multipartFile) {
        System.out.println("convert");
        File file=new File(LocalDate.now().toString()+UUID.randomUUID()+multipartFile.getOriginalFilename());
        try(FileOutputStream fileOutputStream=new FileOutputStream(file)){
            fileOutputStream.write(multipartFile.getBytes()); 
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException("파일형식변환에 실패했습니다");
        }
        return file;
    }
}
