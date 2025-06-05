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
 * Classe de test unitaire pour la Step Spring Batch.
 * Vérifie l'exécution du Step et la transformation des données.
 */
@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
public class StepTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    /**
     * Teste une étape spécifique du job.
     * Vérifie que l'étape s'exécute correctement.
     */
    @Test
    public void testStepExecution() throws Exception {
        // Lancer uniquement l'étape 'transactionStep'
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("transactionStep");

        // Vérifier que l'étape s'est terminée avec succès
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
}