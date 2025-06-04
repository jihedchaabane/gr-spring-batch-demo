package com.chj.gr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chj.gr.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
