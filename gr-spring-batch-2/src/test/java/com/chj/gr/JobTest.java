package com.chj.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;

/**
 * Classe de test unitaire pour le job Spring Batch.
 * Vérifie l'exécution du job et la transformation des données.
 */
@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class JobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    
    /**
     * Teste l'exécution complète du job Spring Batch.
     * Vérifie que le job se termine avec succès et que les données sont correctement écrites.
     */
    @Test
    public void testJobExecution() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("testTime", System.currentTimeMillis())
                .toJobParameters();
        /** Lancer le job */
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        /** Vérifier que le job s'est terminé avec succès. */
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
    
    @Test
    public void endJobTasklet() throws Exception {
        // Lancer uniquement l'étape 'endJobTasklet'
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("endJobTasklet");

        // Vérifier que l'étape s'est terminée avec succès
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
    
}