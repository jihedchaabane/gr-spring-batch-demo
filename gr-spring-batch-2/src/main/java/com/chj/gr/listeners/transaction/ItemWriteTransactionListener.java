package com.chj.gr.listeners.transaction;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

import com.chj.gr.entity.Transaction;

import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class ItemWriteTransactionListener implements ItemWriteListener<Transaction> {

	@Override
	public void beforeWrite(Chunk<? extends Transaction> items) {
		log.debug("beforeWrite {} Transactions", items.size());
	}

	@Override
	public void afterWrite(Chunk<? extends Transaction> items) {
		log.debug("afterWrite {} Transactions", items.size());
	}

	@Override
	public void onWriteError(Exception exception, Chunk<? extends Transaction> items) {
		log.debug("onWriteError {} Transactions :{}", items.size(), exception.getMessage());
	}
	
	

}
