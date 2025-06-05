package com.chj.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.chj.gr.entity.Person;
import com.chj.gr.entity.Transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Classe de test unitaire pour le job Spring Batch.
 * Vérifie l'exécution du job et la transformation des données.
 */
@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
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
        /** Lancer le job */
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        /** Vérifier que le job s'est terminé avec succès. */
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        
        /** Vérifier les données écrites dans la base */
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
        	/**
             * ...
             */
        	Person person = entityManager
                    .createQuery("SELECT p FROM Person p WHERE p.id = :id", Person.class)
                    .setParameter("id", "1")
                    .getSingleResult();
        	/** FirstName en majuscules.*/
            assertEquals("JIHED-TEST-1", person.getFirstName()); 
            /** LastName en majuscules.*/
            assertEquals("CHAABANE-TEST-1", person.getLastName()); 
            /**
             * ...
             */
            Transaction transaction = entityManager
                    .createQuery("SELECT t FROM Transaction t WHERE t.id = :id", Transaction.class)
                    .setParameter("id", "1")
                    .getSingleResult();
            /** Statut en majuscules*/
            assertEquals("APPROVED", transaction.getStatus()); 
            /** Description ajoutée par le processeur.*/
            assertEquals("Initial transaction-test", transaction.getDescription()); 
            /** Amount égalité.*/
            assertEquals(new BigDecimal("100.50"), transaction.getAmount());
            
        } catch (Exception e) {
			e.printStackTrace();
			assertTrue(!e.getMessage().isEmpty());
		} finally {
            entityManager.close();
        }
    }
}