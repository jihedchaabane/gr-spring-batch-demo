package com.chj.gr.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

/**
 * Entité JPA pour la table batch_entity_business_object_skipped.
 * Stocke les business objects qui ont échouées lors de l'une des phases read/process/write.
 */
@Entity
@Table(name = "batch_entity_business_object_skipped")
@Data
@ToString
public class BusinessObjectSkipped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 
     */
    @Column(name = "raw_data", nullable = false)
    private String rawData;
    @Column(name = "error_level", nullable = false)
    private String errorLevel;
    @Column(name = "error_message", nullable = false)
    private String errorMessage;
    @Column(name = "error_timestamp", nullable = false)
    private LocalDateTime errorTimestamp;
    
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