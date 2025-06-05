package com.chj.gr.deciders;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

/**
 * Décideur qui choisit le prochain step en fonction du nombre de transactions traitées.
 * Retourne "SUCCESS" si égal à 3 transactions, sinon "WARNING".
 */
@Component
public class TransactionCountDecider implements JobExecutionDecider {

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        // Récupérer le nombre de perons écrites depuis le contexte.
        long writeCount = stepExecution.getWriteCount();

        // Décider en fonction du nombre de transactions
        if (writeCount == 3) {
            return new FlowExecutionStatus("SUCCESS");
        } else {
            return new FlowExecutionStatus("WARNING");
        }
    }
}
