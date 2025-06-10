package com.chj.gr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "batch_entity_person")
@Data
@ToString
public class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String firstName;
	private String lastName;
	/**
     * 
     */
    @Column(nullable = false)
    private Long jobExecutionId;
    @Column(nullable = false)
    private String jobExecutionName;
    @Column(nullable = false)
    private Long stepExecutionId;
    @Column(nullable = false)
    private String stepExecutionName;

}
