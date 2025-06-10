package com.chj.gr.processors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chj.gr.config.JobExecutionHolder;
import com.chj.gr.entity.Person;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person, Person> {
	@Autowired
	private JobExecutionHolder jobExecutionHolder;
    @Override
    public Person process(Person person) throws Exception {
        
    	person.setJobExecutionId(jobExecutionHolder.getStepExecution().getJobExecutionId());
    	person.setJobExecutionName(jobExecutionHolder.getStepExecution().getJobExecution().getJobInstance().getJobName());
    	person.setStepExecutionId(jobExecutionHolder.getStepExecution().getId());
    	person.setStepExecutionName(jobExecutionHolder.getStepExecution().getStepName());
    	
    	person.setFirstName(person.getFirstName().toUpperCase());
        person.setLastName(person.getLastName().toUpperCase());

        return person;
    }
}
