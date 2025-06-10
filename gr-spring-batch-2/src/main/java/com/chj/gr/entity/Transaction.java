package com.chj.gr.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

/**
 * Entité représentant une transaction financière.
 * Utilisée pour démontrer le traitement des données avec Spring Batch.
 */
@Data
@Entity
@Table(name = "batch_entity_transaction")
@ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String transactionDate;

    @Column
    private String description;
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
    
    public String toRawData() {
    	return transactionId + "," 
				+ amount + "," 
				+ status + "," 
				+ transactionDate+ "," 
				+ description;
	}
}