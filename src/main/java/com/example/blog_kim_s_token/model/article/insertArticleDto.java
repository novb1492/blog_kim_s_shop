package com.example.blog_kim_s_token.model.article;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class insertArticleDto {
    List<MultipartFile> images;
}
