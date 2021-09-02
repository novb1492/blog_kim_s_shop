package com.example.blog_kim_s_token.config;

import com.example.blog_kim_s_token.config.tasks.scanVbank;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class vbankConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final String batchName="chekNonePaidVbank";
    
    @Bean 
    public Job job(){ 
        System.out.println("job");
        return jobBuilderFactory.get(batchName).start(readLines()).next(readLines()).build();
    } 
    @Bean
    protected Step readLines() {
        return stepBuilderFactory
            .get("readLines")
          .tasklet(new scanVbank())
          .build();
    }

    /*@Bean
    protected Step processLines() {
        return stepBuilderFactory
          .get("processLines")
          .tasklet(linesProcessor())
          .build();
    }

    @Bean
    protected Step writeLines() {
        return stepBuilderFactory
          .get("writeLines")
          .tasklet(linesWriter())
          .build();
    }*/
   
    
   




}
