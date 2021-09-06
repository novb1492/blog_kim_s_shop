package com.example.blog_kim_s_token.config.batch;


import com.example.blog_kim_s_token.config.batch.tasks.scanVbank;
import com.example.blog_kim_s_token.model.payment.vbankDao;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@Configuration
public class vbankConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final String batchName="chekNonePaidVbank";

    @Autowired
    private vbankDao vbankDao;
    
    
    @Bean 
    public Job job(){ 
        System.out.println("job");
        return jobBuilderFactory.get(batchName).start(doClearNonePaidReservation()).build();
    } 
    @Bean
    protected Step doClearNonePaidReservation() {
        return stepBuilderFactory
            .get("doClearNonePaidReservation")
          .tasklet(new scanVbank(vbankDao))
          .build();
    }


}
