package com.chj.gr.listeners;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import com.chj.gr.config.JobExecutionHolder;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StepExecutionNotificationListener implements StepExecutionListener {

	private final JobExecutionHolder jobExecutionHolder;

    public StepExecutionNotificationListener(JobExecutionHolder jobExecutionHolder) {
        this.jobExecutionHolder = jobExecutionHolder;
    }
    
	@Override
	public void beforeStep(StepExecution stepExecution) {
		/**
		 * Capturer et stocker le stepExecution.
		 */
        jobExecutionHolder.setStepExecution(stepExecution);
        
        log.debug("afterStep Id {}", stepExecution.getId());
		log.debug("afterStep StepName {}", stepExecution.getStepName());
		log.debug("afterStep JobExecutionId {}", stepExecution.getJobExecutionId());
		log.debug("afterStep Summary {}", stepExecution.getSummary());
		log.debug("afterStep Version {}", stepExecution.getVersion());
		log.debug("afterStep CreateTime {}", stepExecution.getCreateTime());
		log.debug("afterStep StartTime {}", stepExecution.getStartTime());
		log.debug("afterStep EndTime {}", stepExecution.getEndTime());
		log.debug("afterStep LastUpdated {}", stepExecution.getLastUpdated());
		log.debug("afterStep Status {}", stepExecution.getStatus());
//		log.debug("afterStep ExecutionContext {}", stepExecution.getExecutionContext());
		log.debug("afterStep ExitStatus {}", stepExecution.getExitStatus());
//		log.debug("afterStep FailureExceptions {}", stepExecution.getFailureExceptions());
//		log.debug("afterStep JobExecution {}", stepExecution.getJobExecution());
//		log.debug("afterStep JobParameters {}", stepExecution.getJobParameters());

		log.debug("afterStep ReadCount {}", stepExecution.getReadCount());
		log.debug("afterStep WriteCount {}", stepExecution.getWriteCount());
		log.debug("afterStep CommitCount {}", stepExecution.getCommitCount());
		log.debug("afterStep FilterCount {}", stepExecution.getFilterCount());
		log.debug("afterStep RollbackCount {}", stepExecution.getRollbackCount());
		log.debug("afterStep SkipCount {}", stepExecution.getSkipCount());
		
		log.debug("afterStep ReadSkipCount {}", stepExecution.getReadSkipCount());
		log.debug("afterStep ProcessSkipCount {}", stepExecution.getProcessSkipCount());
		log.debug("afterStep WriteSkipCount{}", stepExecution.getWriteSkipCount());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		
		jobExecutionHolder.saveBatch();
		
		log.debug("afterStep Id {}", stepExecution.getId());
		log.debug("afterStep StepName {}", stepExecution.getStepName());
		log.debug("afterStep JobExecutionId {}", stepExecution.getJobExecutionId());
		log.debug("afterStep Summary {}", stepExecution.getSummary());
		log.debug("afterStep Version {}", stepExecution.getVersion());
		log.debug("afterStep CreateTime {}", stepExecution.getCreateTime());
		log.debug("afterStep StartTime {}", stepExecution.getStartTime());
		log.debug("afterStep EndTime {}", stepExecution.getEndTime());
		log.debug("afterStep LastUpdated {}", stepExecution.getLastUpdated());
		log.debug("afterStep Status {}", stepExecution.getStatus());
//		log.debug("afterStep ExecutionContext {}", stepExecution.getExecutionContext());
		log.debug("afterStep ExitStatus {}", stepExecution.getExitStatus());
//		log.debug("afterStep FailureExceptions {}", stepExecution.getFailureExceptions());
//		log.debug("afterStep JobExecution {}", stepExecution.getJobExecution());
//		log.debug("afterStep JobParameters {}", stepExecution.getJobParameters());

		log.debug("afterStep ReadCount {}", stepExecution.getReadCount());
		log.debug("afterStep WriteCount {}", stepExecution.getWriteCount());
		log.debug("afterStep CommitCount {}", stepExecution.getCommitCount());
		log.debug("afterStep FilterCount {}", stepExecution.getFilterCount());
		log.debug("afterStep RollbackCount {}", stepExecution.getRollbackCount());
		log.debug("afterStep SkipCount {}", stepExecution.getSkipCount());
		
		log.debug("afterStep ReadSkipCount {}", stepExecution.getReadSkipCount());
		log.debug("afterStep ProcessSkipCount {}", stepExecution.getProcessSkipCount());
		log.debug("afterStep WriteSkipCount{}", stepExecution.getWriteSkipCount());
	
		return stepExecution.getExitStatus();
	}
}
