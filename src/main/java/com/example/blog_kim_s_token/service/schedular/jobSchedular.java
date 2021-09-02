package com.example.blog_kim_s_token.service.schedular;

import com.example.blog_kim_s_token.config.vbankConfig;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class jobSchedular {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private vbankConfig vbankConfig;

    @Scheduled(fixedDelay = 1000) // scheduler 끝나는 시간 기준으로 1000 간격으로 실행
    public void scheduleFixedDelayTask() {
        try {
            jobLauncher.run(vbankConfig.job(),null);

        } catch (Exception e) {

            
        }    
    }
}
