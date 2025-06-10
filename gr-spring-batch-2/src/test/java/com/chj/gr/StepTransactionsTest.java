package com.chj.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.chj.gr.entity.BusinessObjectSkipped;
import com.chj.gr.entity.Transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

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
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    /**
     * Transactions steps.
     */
    @Test
    public void transactionStepExecution() throws Exception {
        // Lancer uniquement l'étape 'transactionStep'
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("transactionStep");

        // Vérifier que l'étape s'est terminée avec succès
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        
     // Récupérer le job_execution_id
        Long jobExecutionId = jobExecution.getJobId();
		String jobExecutionName = jobExecution.getJobInstance().getJobName();

        // Vérifier les métriques de lecture et de skip
        StepExecution stepExecution = jobExecution.getStepExecutions().stream().filter(se -> se.getStepName().equals("transactionStep")).findFirst().get();
        assertEquals("transactionStep", stepExecution.getStepName());
        assertEquals(6, stepExecution.getReadCount(), 			"6 transactions valides doivent être lu");		// 7 - 1 (Skip READ).
        assertEquals(3, stepExecution.getWriteCount(), 			"3 transactions valides doivent être écrites");	// 7 - (1  (Skip READ) + 2 (Skip PROCESS) + 1 (Skip WRITE)).
        
        assertEquals(1, stepExecution.getReadSkipCount(), 		"1 transactions invalides doivent être skippées au niveau READ");
        assertEquals(2, stepExecution.getProcessSkipCount(), 	"2 transactions invalides doivent être skippés au niveau PROCESS");
        assertEquals(1, stepExecution.getWriteSkipCount(), 		"1 transactions invalides doivent être skippés au niveau WRITE");
        assertEquals(4, stepExecution.getSkipCount(), 			"4 transactions invalides doivent être skippés au niveau READ + PROCESS + WRITE");
        
        assertEquals(4, stepExecution.getCommitCount(), 		"4 CommitCount prévus"); 					
        assertEquals(7, stepExecution.getRollbackCount(), 		"7 transactions doivent être Rollbacked");
        assertEquals(0, stepExecution.getFilterCount(), 		"0 transactions getFilterCount");
        
        // Vérifier les transactions valides et échouées
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            // Transactions valides dans la table transactions
            List<Transaction> transactions = entityManager
                    .createQuery("SELECT t FROM Transaction t "
                    		+ "WHERE t.jobExecutionId = :jobExecutionId "
                    		+ "AND   t.stepExecutionId = :stepExecutionId", Transaction.class)
                    .setParameter("jobExecutionId", jobExecutionId)
                    .setParameter("stepExecutionId", stepExecution.getId())
                    .getResultList();
            assertEquals(3, transactions.size(), "3 transactions doivent être dans la base");
            assertTrue(transactions.stream().anyMatch(t -> t.getTransactionId().equals("TX001-test")));
            assertTrue(transactions.stream().anyMatch(t -> t.getTransactionId().equals("TX002-test")));
            assertTrue(transactions.stream().anyMatch(t -> t.getTransactionId().equals("TX003-test")));
            assertFalse(transactions.stream().anyMatch(t -> t.getTransactionId().equals("TX004-test")));
            assertFalse(transactions.stream().anyMatch(t -> t.getTransactionId().equals("TX005-test")));
            
            assertTrue(transactions.stream().allMatch(t -> t.getJobExecutionId().equals(jobExecutionId)),
                    "Tous les enregistrements en succès doivent avoir le job_execution_id: " + jobExecutionId);
            assertTrue(transactions.stream().allMatch(t -> t.getJobExecutionName().equals(jobExecutionName)),
                    "Tous les enregistrements en succès doivent avoir le job_execution_name: " + jobExecutionName);
            assertTrue(transactions.stream().allMatch(t -> t.getStepExecutionId().equals(stepExecution.getId())),
                    "Tous les enregistrements en succès doivent avoir le step_execution_id: " + stepExecution.getId());
            assertTrue(transactions.stream().allMatch(t -> t.getStepExecutionName().equals(stepExecution.getStepName())),
                    "Tous les enregistrements en succès doivent avoir le step_execution_name: " + stepExecution.getStepName());

            // Vérifier une transaction spécifique
            Transaction transaction = transactions.stream().filter(t-> t.getTransactionId().equals("TX001-test")).findFirst().get();
            assertEquals("APPROVED", transaction.getStatus()); 
            assertEquals("Initial transaction-test", transaction.getDescription()); 
            assertEquals(new BigDecimal("100.50"), transaction.getAmount());

            assertTrue(transaction.getJobExecutionId().equals(jobExecutionId),
                    "La transaction enregistrée avec succès doit avoir doit avoir le job_execution_id: " + jobExecutionId);
            assertTrue(transaction.getJobExecutionName().equals(jobExecutionName),
                    "La transaction enregistrée avec succès doit avoir doit avoir le job_execution_name: " + jobExecutionName);
            assertTrue(transaction.getStepExecutionId().equals(stepExecution.getId()),
                    "La transaction enregistrée avec succès doit avoir doit avoir le step_execution_id: " + stepExecution.getId());
            assertTrue(transaction.getStepExecutionName().equals(stepExecution.getStepName()),
                    "La transaction enregistrée avec succès doit avoir doit avoir le step_execution_name: " + stepExecution.getStepName());
            /**
             *  Transactions échouées dans transaction_read_failed
             */
            List<BusinessObjectSkipped> failedRecords = entityManager
                    .createQuery("SELECT t FROM BusinessObjectSkipped t "
                    		+ "WHERE t.jobExecutionId = :jobExecutionId "
                    		+ "AND   t.stepExecutionId = :stepExecutionId", BusinessObjectSkipped.class)
                    .setParameter("jobExecutionId", jobExecutionId)
                    .setParameter("stepExecutionId", stepExecution.getId())
                    .getResultList();
            assertEquals(7, failedRecords.size(), "7 transactions échouées doivent être enregistrées");
            // SkipInRead
            assertTrue(failedRecords.stream().anyMatch(t -> t.getErrorLevel().equals("SkipInRead")));
            assertTrue(failedRecords.stream().anyMatch(t -> t.getRawData().contains("150.AB")));		// Amount is invalid number.
            assertTrue(failedRecords.stream().anyMatch(t -> t.getErrorMessage().contains("Character A is neither a decimal digit number")));
            // SkipInProcess
            assertTrue(failedRecords.stream().anyMatch(t -> t.getErrorLevel().equals("SkipInProcess")));
            assertTrue(failedRecords.stream().anyMatch(t -> t.getRawData().contains("TX005-test,0,APPROVED") && t.getRawData().contains("APPROVED"))); 		// Amount is zero 	  for APPROVED transaction.
            assertTrue(failedRecords.stream().anyMatch(t -> t.getRawData().contains("TX006-test,-1.2,APPROVED") && t.getRawData().contains("APPROVED"))); 	// Amount is negative for APPROVED transaction.
            assertTrue(failedRecords.stream().anyMatch(t -> t.getErrorMessage().contains("Amout value should not be negative or zero for an APPROVED transaction!")));
            // SkipInWrite
            assertTrue(failedRecords.stream().anyMatch(t -> t.getErrorLevel().equals("SkipInWrite")));
            assertTrue(failedRecords.stream().anyMatch(t -> t.getRawData().contains("TX001-test")));
            assertTrue(failedRecords.stream().anyMatch(t -> t.getErrorMessage().contains("Violation d'index unique ou clé primaire")));
            
           
            //
            assertTrue(failedRecords.stream().allMatch(t -> t.getJobExecutionId().equals(jobExecutionId)),
                    "Tous les enregistrements échoués doivent avoir le job_execution_id: " + jobExecutionId);
            assertTrue(failedRecords.stream().allMatch(t -> t.getJobExecutionName().equals(jobExecutionName)),
                    "Tous les enregistrements échoués doivent avoir le job_execution_name: " + jobExecutionName);
            assertTrue(failedRecords.stream().allMatch(t -> t.getStepExecutionId().equals(stepExecution.getId())),
                    "Tous les enregistrements échoués doivent avoir le step_execution_id: " + stepExecution.getId());
            assertTrue(failedRecords.stream().allMatch(t -> t.getStepExecutionName().equals(stepExecution.getStepName())),
                    "Tous les enregistrements échoués doivent avoir le step_execution_name: " + stepExecution.getStepName());
        } finally {
            entityManager.close();
        }
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