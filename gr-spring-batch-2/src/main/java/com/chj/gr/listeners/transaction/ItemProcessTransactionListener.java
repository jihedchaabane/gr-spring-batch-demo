package com.chj.gr.listeners.transaction;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

import com.chj.gr.entity.Transaction;

import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class ItemProcessTransactionListener implements ItemProcessListener<Transaction, Transaction> {

	@Override
	public void beforeProcess(Transaction item) {
		log.debug("beforeProcess {}", item.toString());
	}

	@Override
	public void afterProcess(Transaction item, Transaction result) {
		log.debug("afterProcess {}", result.toString());
	}

	@Override
	public void onProcessError(Transaction item, Exception e) {
		log.debug("onProcessError {}:{}", item.toString(), e.getMessage());
	}

	
}
