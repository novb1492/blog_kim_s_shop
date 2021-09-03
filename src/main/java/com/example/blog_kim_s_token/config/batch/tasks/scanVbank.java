package com.example.blog_kim_s_token.config.batch.tasks;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.example.blog_kim_s_token.model.payment.vBankDto;
import com.example.blog_kim_s_token.model.payment.vbankDao;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class scanVbank implements Tasklet  {
    @Autowired
    private vbankDao vbankDao;

    public scanVbank(vbankDao vbankDao){
        this.vbankDao=vbankDao;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
       System.out.println("hello execute");
       //List<vBankDto>array=vbankDao.findAll();
       List<vBankDto>array=vbankDao.innerfind(Timestamp.valueOf(LocalDateTime.now()));
      System.out.println(array.get(0).toString());
        return RepeatStatus.FINISHED;
    }
    
}