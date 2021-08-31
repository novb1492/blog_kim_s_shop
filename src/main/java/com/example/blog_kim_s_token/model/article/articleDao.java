package com.example.blog_kim_s_token.model.article;

import org.springframework.data.jpa.repository.JpaRepository;

public interface articleDao extends JpaRepository<articleDto,Integer> {
    
}
