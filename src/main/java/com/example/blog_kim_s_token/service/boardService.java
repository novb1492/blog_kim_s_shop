package com.example.blog_kim_s_token.service;

import java.util.List;

import com.amazonaws.services.managedblockchain.model.IllegalActionException;
import com.example.blog_kim_s_token.model.article.articleDao;
import com.example.blog_kim_s_token.model.article.articleDto;
import com.example.blog_kim_s_token.model.article.getArticleDto;
import com.example.blog_kim_s_token.model.article.insertArticleDto;
import com.nimbusds.jose.shaded.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class boardService {

    private final int paging=5;
    @Autowired
    private articleDao articleDao;

    public JSONObject insertArticle(insertArticleDto insertArticleDto) {
        System.out.println("insertArticle");
        try {
            articleDao.save(makeArticleDto(insertArticleDto, "후기 게시판"));
            return utillService.makeJson(true, "글등록 완료");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insertArticle error"+e.getMessage());
            throw new RuntimeException("글등록에 실패했습니다");
        }
    }
    private articleDto makeArticleDto(insertArticleDto insertArticleDto,String kind) {
        System.out.println("makeArticleDto");
        articleDto dto=articleDto.builder()
                                    .email(SecurityContextHolder.getContext().getAuthentication().getName())
                                    .kind(kind)
                                    .textarea(insertArticleDto.getTextarea())
                                    .title(insertArticleDto.getTitle())
                                    .clicked(0)
                                    .build();
                                    return dto;
    }
    public List<articleDto> getArticle() {
        return articleDao.findAll();
    }
    public articleDto getArticle(getArticleDto getArticleDto) {
        return articleDao.findById(getArticleDto.getArticleId()).orElseThrow(()-> new IllegalActionException("존재하지 않는 게시물입니다"));
    }
}
