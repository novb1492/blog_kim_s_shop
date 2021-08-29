package com.example.blog_kim_s_token.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class webConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/static/image/**")
            .addResourceLocations("file:///C:/Users/Administrator/Desktop/blog/blog_kim_s_shop/src/main/resources/static/image/");
    }
}

