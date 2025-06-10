package com.chj.gr.processors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chj.gr.config.JobExecutionHolder;
import com.chj.gr.entity.Transaction;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Processeur pour transformer les données de transaction.
 * Valider et mettre à jour le statut.
 */
@Component
@Slf4j
public class TransactionItemProcessor implements ItemProcessor<Transaction, Transaction> {
	@Autowired
	private JobExecutionHolder jobExecutionHolder;

    @Override
    public Transaction process(Transaction transaction) throws Exception {
    	
    	if ("APPROVED".equalsIgnoreCase(transaction.getStatus())
    			&& transaction.getAmount().doubleValue() <= 0) {
			throw new IllegalArgumentException("Amout value should not be negative or zero for an APPROVED transaction!");
		}
    	
    	transaction.setJobExecutionId(jobExecutionHolder.getStepExecution().getJobExecutionId());
    	transaction.setJobExecutionName(jobExecutionHolder.getStepExecution().getJobExecution().getJobInstance().getJobName());
    	transaction.setStepExecutionId(jobExecutionHolder.getStepExecution().getId());
    	transaction.setStepExecutionName(jobExecutionHolder.getStepExecution().getStepName());
    	
        // Exemple de transformation : Mettre le statut en majuscules
        transaction.setStatus(transaction.getStatus().toUpperCase());
        // Ajouter une description si vide
        if (StringUtils.isEmpty(transaction.getDescription())) {
            transaction.setDescription("Processed transaction");
        }
        return transaction;
    }
}