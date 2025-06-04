package com.chj.gr.processors;

import org.springframework.batch.item.ItemProcessor;

import com.chj.gr.entity.Person;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(Person person) throws Exception {
        
    	person.setFirstName(person.getFirstName().toUpperCase());
        person.setLastName(person.getLastName().toUpperCase());

        return person;
    }
}
