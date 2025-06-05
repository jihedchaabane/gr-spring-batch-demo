package com.chj.gr.deciders;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

/**
 * Décideur qui choisit le prochain step en fonction du nombre de persons traitées.
 * Retourne "SUCCESS" si égal à 24 persons, sinon "WARNING".
 */
@Component
public class PersonCountDecider implements JobExecutionDecider {

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        // Récupérer le nombre de perons écrites depuis le contexte.
        long writeCount = stepExecution.getWriteCount();

        // Décider en fonction du nombre de persons.
        if (writeCount == 24) {
            return new FlowExecutionStatus("SUCCESS");
        } else {
            return new FlowExecutionStatus("WARNING");
        }
    }
}
