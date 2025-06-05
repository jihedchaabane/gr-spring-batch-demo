package com.chj.gr.listeners.person;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

import com.chj.gr.entity.Person;

import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class ItemWritePersonListener implements ItemWriteListener<Person> {

	@Override
	public void beforeWrite(Chunk<? extends Person> items) {
		log.debug("beforeWrite {} persons", items.size());
	}

	@Override
	public void afterWrite(Chunk<? extends Person> items) {
		log.debug("afterWrite {} persons", items.size());
	}

	@Override
	public void onWriteError(Exception exception, Chunk<? extends Person> items) {
		log.debug("onWriteError {} persons :{}", items.size(), exception.getMessage());
	}
	
	

}
