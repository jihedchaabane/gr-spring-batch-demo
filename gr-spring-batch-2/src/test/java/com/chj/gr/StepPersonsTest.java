package com.chj.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.chj.gr.entity.Person;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Classe de test unitaire pour une Step spécifique Spring Batch.
 * Vérifie que l'étape s'exécute correctement.
 */
@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
public class StepPersonsTest {
	@Autowired
    private EntityManagerFactory entityManagerFactory;
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
        } catch (Exception e) {
			e.printStackTrace();
			assertTrue(!e.getMessage().isEmpty());
		} finally {
            entityManager.close();
        }
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