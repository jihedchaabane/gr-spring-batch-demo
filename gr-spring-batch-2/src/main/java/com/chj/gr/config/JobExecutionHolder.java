package com.chj.gr.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.chj.gr.entity.BusinessObjectSkipped;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * Composant pour stocker le stepExecution courant.
 * Utilisé pour partager divers informations entre les divers composants (listeners, processors, etc ...)
 */
@Component
@Slf4j
public class JobExecutionHolder {

	private StepExecution stepExecution;
    private final EntityManagerFactory entityManagerFactory;
    
    private List<BusinessObjectSkipped> failedRecords = new ArrayList<>();
    
    @Value("${skipped.batchSize}")
    private int batchSize;
    
    public JobExecutionHolder(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public StepExecution getStepExecution() {
		return stepExecution;
	}

	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	public synchronized void addFailedBusinessObject(BusinessObjectSkipped failedRecord) {
		failedRecord.setJobExecutionId(stepExecution.getJobExecutionId());
		failedRecord.setJobExecutionName(stepExecution.getJobExecution().getJobInstance().getJobName());
		failedRecord.setStepExecutionId(stepExecution.getId());
		failedRecord.setStepExecutionName(stepExecution.getStepName());
		
		failedRecords.add(failedRecord);
		if (failedRecords.size() >= batchSize) {
            saveBatch();
        }
	}
	
	/**
	 * Enregistrer dans la table batch_entity_business_object_skipped.
	 */
	public synchronized void saveBatch() {
		if (failedRecords != null && !failedRecords.isEmpty()) {
			EntityManager entityManager = entityManagerFactory.createEntityManager();
		    try {
		        entityManager.getTransaction().begin();
		        for (BusinessObjectSkipped record : failedRecords) {
		            entityManager.persist(record);
		            log.info("Saved failed business object to batch_entity_business_object_skipped table: ", record.getRawData());
		        }
		        entityManager.getTransaction().commit();
		        failedRecords.clear();
		    } catch (Exception e) {
		        entityManager.getTransaction().rollback();
		        log.error("Failed to save records to batch_entity_business_object_skipped table due to : {}", e.getMessage());
		    } finally {
		        entityManager.close();
		    }
		}
	}
    
    
}