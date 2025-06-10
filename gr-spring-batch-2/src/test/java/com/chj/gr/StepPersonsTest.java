package com.chj.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Classe de test unitaire pour une Step spécifique Spring Batch.
 * Vérifie que l'étape s'exécute correctement.
 */
@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
public class StepPersonsTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    /**
     * Persons steps.
     */
    @Test
    public void personStepExecution() throws Exception {
        // Lancer uniquement l'étape 'personStep'
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("personStep");

        // Vérifier que l'étape s'est terminée avec succès
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
    
    @Test
    public void successPersonTasklet() throws Exception {
        // Lancer uniquement l'étape 'successPersonTasklet'
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("successPersonTasklet");

        // Vérifier que l'étape s'est terminée avec succès
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
    
    @Test
    public void warningPersonTasklet() throws Exception {
        // Lancer uniquement l'étape 'warningPersonTasklet'
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("warningPersonTasklet");

        // Vérifier que l'étape s'est terminée avec succès
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
    
}