package com.chj.gr.listeners;

import java.time.LocalDateTime;

import org.springframework.batch.core.listener.SkipListenerSupport;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import com.chj.gr.config.JobExecutionHolder;
import com.chj.gr.entity.BusinessObjectSkipped;
import com.chj.gr.utilities.ExtractUtilities;

import lombok.extern.slf4j.Slf4j;

/**
 * SkipListener pour enregistrer les objets échouées lors des phases read, process, write
 * dans la table transaction_read_failed.
 */
@Component
@Slf4j
public class CommonSkipListener extends SkipListenerSupport<Object, Object> {

    private final JobExecutionHolder jobExecutionHolder;

    public CommonSkipListener(JobExecutionHolder jobExecutionIdHolder) {
		this.jobExecutionHolder = jobExecutionIdHolder;
	}

	@Override
    public void onSkipInRead(Throwable t) {
        if (t instanceof FlatFileParseException) {
        	
            FlatFileParseException parseException = (FlatFileParseException) t;
            String rawData = parseException.getInput();
            String errorMessage = t.getCause() != null ? t.getCause().getMessage() : t.getMessage();
            
    		BusinessObjectSkipped failedRecord = ExtractUtilities.extractInformations(rawData, "SkipInRead");
            failedRecord.setErrorMessage(errorMessage);
            failedRecord.setErrorTimestamp(LocalDateTime.now());
            jobExecutionHolder.addFailedBusinessObject(failedRecord);
        }
    }

//	@Override
//	public void onSkipInProcess(Object item, Throwable t) {
//		super.onSkipInProcess(item, t);
//	}
	
//    @Override
//	public void onSkipInWrite(Object item, Throwable t) {
//		super.onSkipInWrite(item, t);
//	}

}