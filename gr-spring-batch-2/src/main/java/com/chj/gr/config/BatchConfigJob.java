package com.chj.gr.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.chj.gr.deciders.PersonCountDecider;
import com.chj.gr.deciders.TransactionCountDecider;
import com.chj.gr.listeners.JobNotificationListener;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration principale de Spring Batch.
 * Définit le job à exécuter.
 */
@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfigJob {
    /**
     * Configuration du job.
     * Inclut un listener pour suivre l'exécution.
     */
    @Bean
    public Job job(JobRepository jobRepository, 
    		Step personStep,
            PersonCountDecider personDecider,
            Step successPersonTasklet, 
            Step warningPersonTasklet,
            
    		Step transactionStep,
    		TransactionCountDecider transactionCountDecider,
    		Step successTransactionTasklet, 
            Step warningTransactionTasklet,
    		
            Step successJobTasklet,
    		JobNotificationListener jobNotificationListener) {
        return new JobBuilder("importDataJob", jobRepository)
        		.listener(jobNotificationListener)
                .start(personStep)
                .next(personDecider)
	                .on("SUCCESS").to(successPersonTasklet)
	                	.next(transactionStep)
	                		.next(transactionCountDecider)
	                			.on("SUCCESS").to(successTransactionTasklet)
	                			.from(transactionCountDecider).on("WARNING").to(warningTransactionTasklet)
	                .from(personDecider).on("WARNING").to(warningPersonTasklet)
	             .next(successJobTasklet)
	            .end()
                .build();
    }
    
    @Bean
    public Step successJobTasklet(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("successJobTasklet", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("SUCCESS Job Step: ALL GOOD.");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public CommandLineRunner runJob(JobLauncher jobLauncher, Job job) {
        return args -> {
            try {
                // Create unique JobParameters
                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())  // unique parameter
                        .toJobParameters();

                jobLauncher.run(job, jobParameters);
                log.info("Le Batch job est invoké.");
            } catch (JobExecutionException e) {
            	log.error("Le Batch job a échoué: {}.", e.getMessage());
            }
        };
    }

}
