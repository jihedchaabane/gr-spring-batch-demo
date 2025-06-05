package com.chj.gr;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
public class SchemaTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    /**
     * Teste que le schéma de la base de données est correctement créé.
     * Vérifie que la table 'transactions' existe dans H2.
     */
    @Test
    public void testDatabaseSchemaCreation() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
        	/**
        	 * Vérifier que la table 'batch_entity_person' existe en exécutant une requête simple.
        	 */
        	Long count = entityManager
                    .createQuery("SELECT COUNT(p) FROM Person p", Long.class)
                    .getSingleResult();
            assertNotNull(count); // La table doit exister, même si elle est vide
        	
        	/**
        	 * Vérifier que la table 'batch_entity_transaction' existe en exécutant une requête simple.
        	 */
            count = entityManager
                    .createQuery("SELECT COUNT(t) FROM Transaction t", Long.class)
                    .getSingleResult();
            assertNotNull(count); // La table doit exister, même si elle est vide
        } finally {
            entityManager.close();
        }
    }
    
    /**
     * Teste que les tables de métadonnées de Spring Batch sont correctement créées dans H2.
     * Liste les tables et vérifie la présence des tables spécifiques de Spring Batch.
     */
    @Test
    public void testSpringBatchMetadataTables() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            // Récupérer la liste des tables dans la base H2
            @SuppressWarnings("unchecked")
			List<String> tableNames = entityManager
                    .createNativeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'")
                    .getResultList();

            // Liste des tables de métadonnées attendues pour Spring Batch
            List<String> expectedBatchTables = Arrays.asList(
                    "BATCH_JOB_INSTANCE",
                    "BATCH_JOB_EXECUTION",
                    "BATCH_JOB_EXECUTION_PARAMS",
                    "BATCH_STEP_EXECUTION",
                    "BATCH_STEP_EXECUTION_CONTEXT",
                    "BATCH_JOB_EXECUTION_CONTEXT"
            );

            // Vérifier que chaque table de métadonnées Spring Batch est présente
            for (String expectedTable : expectedBatchTables) {
                assertTrue(tableNames.contains(expectedTable),
                        "La table de métadonnées Spring Batch '" + expectedTable + "' n'a pas été trouvée.");
            }

            // Afficher les tables pour débogage
            log.info("Tables trouvées dans la base H2 : {} ", tableNames);
        } finally {
            entityManager.close();
        }
    }
}