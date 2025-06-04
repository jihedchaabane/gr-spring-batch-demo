package com.chj.gr.listeners;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StepExecutionNotificationListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		log.debug("beforeStep {}", stepExecution.getId());
		log.debug("beforeStep {}", stepExecution.getStepName());
		log.debug("beforeStep {}", stepExecution.getJobExecutionId());
		log.debug("beforeStep {}", stepExecution.getSummary());
		log.debug("beforeStep {}", stepExecution.getVersion());
		log.debug("beforeStep {}", stepExecution.getCreateTime());
		log.debug("beforeStep {}", stepExecution.getStartTime());
		log.debug("beforeStep {}", stepExecution.getEndTime());
		log.debug("beforeStep {}", stepExecution.getLastUpdated());
		log.debug("beforeStep {}", stepExecution.getStatus());
//		log.debug("beforeStep {}", stepExecution.getExecutionContext());
		log.debug("beforeStep {}", stepExecution.getExitStatus());
//		log.debug("beforeStep {}", stepExecution.getFailureExceptions());
//		log.debug("beforeStep {}", stepExecution.getJobExecution());
//		log.debug("beforeStep {}", stepExecution.getJobParameters());
		log.debug("beforeStep {}", stepExecution.getCommitCount());
		log.debug("beforeStep {}", stepExecution.getFilterCount());
		log.debug("beforeStep {}", stepExecution.getProcessSkipCount());
		log.debug("beforeStep {}", stepExecution.getReadCount());
		log.debug("beforeStep {}", stepExecution.getReadSkipCount());
		log.debug("beforeStep {}", stepExecution.getRollbackCount());
		log.debug("beforeStep {}", stepExecution.getSkipCount());
		log.debug("beforeStep {}", stepExecution.getWriteCount());
		log.debug("beforeStep {}", stepExecution.getWriteSkipCount());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		log.debug("afterStep {}", stepExecution.getId());
		log.debug("afterStep {}", stepExecution.getStepName());
		log.debug("afterStep {}", stepExecution.getJobExecutionId());
		log.debug("afterStep {}", stepExecution.getSummary());
		log.debug("afterStep {}", stepExecution.getVersion());
		log.debug("afterStep {}", stepExecution.getCreateTime());
		log.debug("afterStep {}", stepExecution.getStartTime());
		log.debug("afterStep {}", stepExecution.getEndTime());
		log.debug("afterStep {}", stepExecution.getLastUpdated());
		log.debug("afterStep {}", stepExecution.getStatus());
//		log.debug("afterStep {}", stepExecution.getExecutionContext());
		log.debug("afterStep {}", stepExecution.getExitStatus());
//		log.debug("afterStep {}", stepExecution.getFailureExceptions());
//		log.debug("afterStep {}", stepExecution.getJobExecution());
//		log.debug("afterStep {}", stepExecution.getJobParameters());
		log.debug("afterStep {}", stepExecution.getCommitCount());
		log.debug("afterStep {}", stepExecution.getFilterCount());
		log.debug("afterStep {}", stepExecution.getProcessSkipCount());
		log.debug("afterStep {}", stepExecution.getReadCount());
		log.debug("afterStep {}", stepExecution.getReadSkipCount());
		log.debug("afterStep {}", stepExecution.getRollbackCount());
		log.debug("afterStep {}", stepExecution.getSkipCount());
		log.debug("afterStep {}", stepExecution.getWriteCount());
		log.debug("afterStep {}", stepExecution.getWriteSkipCount());
		return stepExecution.getExitStatus();
	}
}
