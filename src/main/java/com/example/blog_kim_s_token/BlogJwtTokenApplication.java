package com.example.blog_kim_s_token;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableBatchProcessing
@SpringBootApplication
public class BlogJwtTokenApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogJwtTokenApplication.class, args);
	}

}
