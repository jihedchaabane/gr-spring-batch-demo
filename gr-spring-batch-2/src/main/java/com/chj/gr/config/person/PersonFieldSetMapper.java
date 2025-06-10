package com.chj.gr.config.person;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.boot.context.properties.bind.BindException;

import com.chj.gr.entity.Person;

import io.micrometer.common.util.StringUtils;

public class PersonFieldSetMapper implements FieldSetMapper<Person> {
    @Override
    public Person mapFieldSet(FieldSet fieldSet) throws BindException {
    	Person person = new Person();
    	person.setFirstName(fieldSet.readString("firstName"));
    	person.setLastName(fieldSet.readString("lastName"));
    	
    	if (StringUtils.isEmpty(person.getFirstName())) {
			throw new IllegalArgumentException("FirstName is either null or empty!");
		}
    	if (StringUtils.isEmpty(person.getLastName())) {
    		throw new IllegalArgumentException("LastName is either null or empty!");
		}
    	
        return person;
    }
}
