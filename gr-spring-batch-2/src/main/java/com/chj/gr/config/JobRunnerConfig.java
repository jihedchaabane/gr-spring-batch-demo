package com.chj.gr.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration pour le CommandLineRunner qui exécute le job en production.
 */
@Configuration
@Profile("!test") // Exclure le profil test sinon les tests unitaires seront lancés deux (2) fois.
public class JobRunnerConfig {

    private static final Logger log = LoggerFactory.getLogger(JobRunnerConfig.class);

    @Bean
    public CommandLineRunner runJob(JobLauncher jobLauncher, Job job) {
        return args -> {
            try {
                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters();
                jobLauncher.run(job, jobParameters);
            } catch (Exception e) {
                log.error("Le Batch job a échoué: {}.", e.getMessage());
            }
        };
    }
}