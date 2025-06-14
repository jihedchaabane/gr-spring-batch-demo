package com.chj.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.chj.gr.entity.BusinessObjectSkipped;
import com.chj.gr.entity.Person;
import com.chj.gr.entity.Transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
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
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    
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
        
        this.validatePersons(jobExecution);
        this.validateTransactions(jobExecution);
    }
    
    
    private void validatePersons(JobExecution jobExecution) {
    	// Récupérer le job_execution_id
        Long jobExecutionId = jobExecution.getJobId();
		String jobExecutionName = jobExecution.getJobInstance().getJobName();
    	
		StepExecution stepExecution = jobExecution.getStepExecutions().stream().filter(se -> se.getStepName().equals("personStep")).findFirst().get();
        assertEquals("personStep", stepExecution.getStepName());
        assertEquals(26, stepExecution.getReadCount(), 			"28 persons valides doivent être lu");		// 28 - 2 (Skip READ).
        assertEquals(24, stepExecution.getWriteCount(), 		"24 persons valides doivent être écrits");	// 28 - (2  (Skip READ) + 2 (Skip PROCESS) + 0 (Skip WRITE)).
        
        assertEquals(2, stepExecution.getReadSkipCount(), 		"2 persons invalides doivent être skippés au niveau READ");
        assertEquals(2, stepExecution.getProcessSkipCount(), 	"2 persons invalides doivent être skippés au niveau PROCESS");
        assertEquals(0, stepExecution.getWriteSkipCount(), 		"0 persons invalides doivent être skippés au niveau WRITE");
        assertEquals(4, stepExecution.getSkipCount(), 			"4 persons invalides doivent être skippés au niveau READ + PROCESS + WRITE");
        
        assertEquals(3, stepExecution.getCommitCount(), 		"3 CommitCount prévus"); 				// nombre des personnes valid (24) divisé par la Taille du chunk (ici c'est 10).
        assertEquals(4, stepExecution.getRollbackCount(), 		"4 persons doivent être Rollbacked");	// égale au skipCount normallement.
        assertEquals(0, stepExecution.getFilterCount(), 		"0 persons getFilterCount");			// ??
		
    	/** Vérifier les données écrites dans la base */
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
        	
        	// Persons valides dans la table transactions
            List<Person> persons = entityManager
                    .createQuery("SELECT p FROM Person p "
                    		+ "WHERE p.jobExecutionId  = :jobExecutionId "
                    		+ "AND   p.stepExecutionId = :stepExecutionId", Person.class)
                    .setParameter("jobExecutionId", jobExecutionId)
                    .setParameter("stepExecutionId", stepExecution.getId())
                    .getResultList();
            assertEquals(24, persons.size(), "24 persons doivent être dans la base");
            assertTrue(persons.stream().anyMatch(p -> p.getFirstName().equals("JIHED-TEST-1")));
            assertTrue(persons.stream().anyMatch(p -> p.getLastName().equals("CHAABANE-TEST-1")));

            assertTrue(persons.stream().allMatch(t -> t.getJobExecutionId().equals(jobExecutionId)),
                    "Tous les enregistrements en succès doivent avoir le job_execution_id: " + jobExecutionId);
            assertTrue(persons.stream().allMatch(t -> t.getJobExecutionName().equals(jobExecutionName)),
                    "Tous les enregistrements en succès doivent avoir le job_execution_name: " + jobExecutionName);
            assertTrue(persons.stream().allMatch(t -> t.getStepExecutionId().equals(stepExecution.getId())),
                    "Tous les enregistrements en succès doivent avoir le step_execution_id: " + stepExecution.getId());
            assertTrue(persons.stream().allMatch(t -> t.getStepExecutionName().equals(stepExecution.getStepName())),
                    "Tous les enregistrements en succès doivent avoir le step_execution_name: " + stepExecution.getStepName());
            
            // Vérifier une personne spécifique
            Person person = persons.stream().filter(t-> t.getFirstName().equals("JIHED-TEST-3")).findFirst().get();
            assertEquals("JIHED-TEST-3", person.getFirstName()); 
            assertEquals("CHAABANE-TEST-3", person.getLastName()); 
            
            assertTrue(person.getJobExecutionId().equals(jobExecutionId),
                    "La personne enregistrée avec succès doit avoir doit avoir le job_execution_id: " + jobExecutionId);
            assertTrue(person.getJobExecutionName().equals(jobExecutionName),
                    "La personne enregistrée avec succès doit avoir doit avoir le job_execution_name: " + jobExecutionName);
            assertTrue(person.getStepExecutionId().equals(stepExecution.getId()),
                    "La personne enregistrée avec succès doit avoir doit avoir le step_execution_id: " + stepExecution.getId());
            assertTrue(person.getStepExecutionName().equals(stepExecution.getStepName()),
                    "La personne enregistrée avec succès doit avoir doit avoir le step_execution_name: " + stepExecution.getStepName());
            
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
            assertEquals(4, failedRecords.size(), "4 persons échouées doivent être enregistrées");
            // SkipInRead
            assertTrue(failedRecords.stream().anyMatch(t -> t.getErrorLevel().equals("SkipInRead")));
            assertTrue(failedRecords.stream().anyMatch(t -> t.getRawData().contains("Jihed-test-25"))); 	// lastname is null
            assertTrue(failedRecords.stream().anyMatch(t -> t.getRawData().contains("chaabane-test-26")));	// firstname is null
            assertTrue(failedRecords.stream().anyMatch(t -> t.getErrorMessage().contains("FirstName is either null or empty!")));
            assertTrue(failedRecords.stream().anyMatch(t -> t.getErrorMessage().contains("LastName is either null or empty!")));
            // SkipInProcess
            assertTrue(failedRecords.stream().anyMatch(t -> t.getErrorLevel().equals("SkipInProcess")));
            assertTrue(failedRecords.stream().anyMatch(t -> t.getRawData().contains("chaabane-test-27")));	// firstname is unknown
            assertTrue(failedRecords.stream().anyMatch(t -> t.getRawData().contains("Jihed-test-28")));		// lastname is unknown
            assertTrue(failedRecords.stream().anyMatch(t -> t.getErrorMessage().contains("FirstName value should not be UNKNOWN/unknown for a person!")));
            assertTrue(failedRecords.stream().anyMatch(t -> t.getErrorMessage().contains("LastName value should not be UNKNOWN/unknown for a person!")));
            //...
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
    
    private void validateTransactions(JobExecution jobExecution) {
    	// Récupérer le job_execution_id
        Long jobExecutionId = jobExecution.getJobId();
		String jobExecutionName = jobExecution.getJobInstance().getJobName();

        // Vérifier les métriques de lecture et de skip
        StepExecution stepExecution = jobExecution.getStepExecutions().stream().filter(se -> se.getStepName().equals("transactionStep")).findFirst().get();
        assertEquals("transactionStep", stepExecution.getStepName());
        assertEquals(5, stepExecution.getReadCount(), 			"5 transactions valides doivent être lu");		// 6 - 1 (Skip READ).
        assertEquals(3, stepExecution.getWriteCount(), 			"3 transactions valides doivent être écrites");	// 6 - (1  (Skip READ) + 2 (Skip PROCESS) + 0 (Skip WRITE)).
        
        assertEquals(1, stepExecution.getReadSkipCount(), 		"1 transactions invalides doivent être skippées au niveau READ");
        assertEquals(2, stepExecution.getProcessSkipCount(), 	"2 transactions invalides doivent être skippés au niveau PROCESS");
        assertEquals(0, stepExecution.getWriteSkipCount(), 		"0 transactions invalides doivent être skippés au niveau WRITE");
        assertEquals(3, stepExecution.getSkipCount(), 			"3 transactions invalides doivent être skippés au niveau READ + PROCESS + WRITE");
        
        assertEquals(1, stepExecution.getCommitCount(), 		"1 CommitCount prévus"); 					
        assertEquals(4, stepExecution.getRollbackCount(), 		"4 transactions doivent être Rollbacked");
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
            assertEquals(3, failedRecords.size(), "3 transactions échouées doivent être enregistrées");
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
    public void endJobTasklet() throws Exception {
        // Lancer uniquement l'étape 'endJobTasklet'
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("endJobTasklet");

        // Vérifier que l'étape s'est terminée avec succès
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
    
}