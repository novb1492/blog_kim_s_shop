package com.example.blog_kim_s_token.config.tasks;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class scanVbank implements Tasklet,StepExecutionListener  {
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
       System.out.println("hello dsd");
        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
    System.out.println("hello dsd") ;  
        for(int i=1;i<9;i++){
            System.out.println(i);
        }     
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
            System.out.println("hello dsd") ;       
            return null;
    }
    
}
