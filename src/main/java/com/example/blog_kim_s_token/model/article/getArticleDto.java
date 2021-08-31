package com.example.blog_kim_s_token.model.article;

import javax.validation.constraints.Min;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class getArticleDto {
    @Min(value = 1,message = "잘못된 글번호입니다")
    private int articleId;
}
