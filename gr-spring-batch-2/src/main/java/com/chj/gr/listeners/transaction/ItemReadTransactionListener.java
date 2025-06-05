package com.chj.gr.listeners.transaction;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

import com.chj.gr.entity.Transaction;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ItemReadTransactionListener implements ItemReadListener<Transaction> {
	
	@Override
	public void beforeRead() {
		log.debug("beforeRead");
	}

	@Override
	public void afterRead(Transaction object) {
		log.debug("afterRead {}", object.toString());
	}

	@Override
	public void onReadError(Exception ex) {
		log.debug("onReadError {}", ex.getMessage());
	}

	
}
