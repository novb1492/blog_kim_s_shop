package com.example.blog_kim_s_token.service.schedular;



import java.time.LocalDateTime;
import com.example.blog_kim_s_token.config.vbankConfig;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
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

    @Scheduled(fixedDelay = 1000*3) // scheduler 끝나는 시간 기준으로 1000 간격으로 실행
    public void deletevbank() {
        System.out.println("deletevbank");
        try {
            JobParameters JobParameters=new JobParametersBuilder().addString("key","vbank"+LocalDateTime.now().toString()).toJobParameters();
            jobLauncher.run(vbankConfig.job(),JobParameters);

        } catch (Exception e) {

            
        }    
    }
}
