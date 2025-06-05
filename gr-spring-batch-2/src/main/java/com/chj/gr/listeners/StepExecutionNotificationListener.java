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
		log.debug("beforeStep Id {}", stepExecution.getId());
		log.debug("beforeStep StepName {}", stepExecution.getStepName());
		log.debug("beforeStep JobExecutionId {}", stepExecution.getJobExecutionId());
		log.debug("beforeStep Summary {}", stepExecution.getSummary());
		log.debug("beforeStep Version {}", stepExecution.getVersion());
		log.debug("beforeStep CreateTime {}", stepExecution.getCreateTime());
		log.debug("beforeStep StartTime {}", stepExecution.getStartTime());
		log.debug("beforeStep EndTime {}", stepExecution.getEndTime());
		log.debug("beforeStep LastUpdated {}", stepExecution.getLastUpdated());
		log.debug("beforeStep Status {}", stepExecution.getStatus());
//		log.debug("beforeStep ExecutionContext {}", stepExecution.getExecutionContext());
		log.debug("beforeStep ExitStatus {}", stepExecution.getExitStatus());
//		log.debug("beforeStep FailureExceptions {}", stepExecution.getFailureExceptions());
//		log.debug("beforeStep JobExecution {}", stepExecution.getJobExecution());
//		log.debug("beforeStep JobParameters {}", stepExecution.getJobParameters());
		log.debug("beforeStep CommitCount {}", stepExecution.getCommitCount());
		log.debug("beforeStep FilterCount {}", stepExecution.getFilterCount());
		log.debug("beforeStep ProcessSkipCount {}", stepExecution.getProcessSkipCount());
		log.debug("beforeStep ReadCount {}", stepExecution.getReadCount());
		log.debug("beforeStep ReadSkipCount {}", stepExecution.getReadSkipCount());
		log.debug("beforeStep RollbackCount {}", stepExecution.getRollbackCount());
		log.debug("beforeStep SkipCount {}", stepExecution.getSkipCount());
		log.debug("beforeStep WriteCount {}", stepExecution.getWriteCount());
		log.debug("beforeStep WriteSkipCount{}", stepExecution.getWriteSkipCount());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
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
		log.debug("afterStep CommitCount {}", stepExecution.getCommitCount());
		log.debug("afterStep FilterCount {}", stepExecution.getFilterCount());
		log.debug("afterStep ProcessSkipCount {}", stepExecution.getProcessSkipCount());
		log.debug("afterStep ReadCount {}", stepExecution.getReadCount());
		log.debug("afterStep ReadSkipCount {}", stepExecution.getReadSkipCount());
		log.debug("afterStep RollbackCount {}", stepExecution.getRollbackCount());
		log.debug("afterStep SkipCount {}", stepExecution.getSkipCount());
		log.debug("afterStep WriteCount {}", stepExecution.getWriteCount());
		log.debug("afterStep WriteSkipCount{}", stepExecution.getWriteSkipCount());
	
		return stepExecution.getExitStatus();
	}
}
