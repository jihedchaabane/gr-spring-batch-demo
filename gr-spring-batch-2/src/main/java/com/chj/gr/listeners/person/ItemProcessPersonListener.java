package com.chj.gr.listeners.person;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

import com.chj.gr.entity.Person;

import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class ItemProcessPersonListener implements ItemProcessListener<Person, Person> {

	@Override
	public void beforeProcess(Person item) {
		log.debug("beforeProcess {}", item.toString());
	}

	@Override
	public void afterProcess(Person item, Person result) {
		log.debug("afterProcess {}", result.toString());
	}

	@Override
	public void onProcessError(Person item, Exception e) {
		log.debug("onProcessError {}:{}", item.toString(), e.getMessage());
	}

	
}
