package com.chj.gr.listeners.person;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

import com.chj.gr.entity.Person;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ItemReadPersonListener implements ItemReadListener<Person> {
	
	@Override
	public void beforeRead() {
		log.debug("beforeRead");
	}

	@Override
	public void afterRead(Person object) {
		log.debug("afterRead {}", object.toString());
	}

	@Override
	public void onReadError(Exception ex) {
		log.debug("onReadError {}", ex.getMessage());
	}

	
}
