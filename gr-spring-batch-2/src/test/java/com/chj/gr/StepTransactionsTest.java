package com.chj.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
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
public class StepTransactionsTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    /**
     * Transactions steps.
     */
    @Test
    @Disabled
    public void transactionStepExecution() throws Exception {
        // Lancer uniquement l'étape 'transactionStep'
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("transactionStep");

        // Vérifier que l'étape s'est terminée avec succès
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
    
    @Test
    public void successTransactionTasklet() throws Exception {
        // Lancer uniquement l'étape 'successTransactionTasklet'
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("successTransactionTasklet");

        // Vérifier que l'étape s'est terminée avec succès
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @Test
    public void warningTransactionTasklet() throws Exception {
        // Lancer uniquement l'étape 'warningTransactionTasklet'
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("warningTransactionTasklet");

        // Vérifier que l'étape s'est terminée avec succès
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
}