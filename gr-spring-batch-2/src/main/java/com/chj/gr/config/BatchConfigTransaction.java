package com.chj.gr.config;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.chj.gr.entity.Transaction;
import com.chj.gr.listeners.ChunkNotificationListener;
import com.chj.gr.listeners.StepExecutionNotificationListener;
import com.chj.gr.listeners.transaction.ItemProcessTransactionListener;
import com.chj.gr.listeners.transaction.ItemReadTransactionListener;
import com.chj.gr.listeners.transaction.ItemWriteTransactionListener;
import com.chj.gr.processors.TransactionItemProcessor;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration principale de Spring Batch.
 * Définit le job, les étapes, et les composants (reader, processor, writer).
 */
@Configuration
@Slf4j
public class BatchConfigTransaction {

    /**
     * Configuration du lecteur de fichier CSV.
     * Lit les données d'entrée depuis un fichier CSV.
     */
    @Bean
    public FlatFileItemReader<Transaction> transactionReader() {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("transactionItemReader")
                .resource(new ClassPathResource("transactions.csv"))
                .delimited()
                .names("transactionId", "amount", "status", "transactionDate", "description")
                .linesToSkip(1)
                .targetType(Transaction.class)
                /**
                 * OR.
                 */
                .fieldSetMapper(fieldSet -> {
                	Transaction transaction = new Transaction();
                	transaction.setTransactionId(fieldSet.readString("transactionId"));
                	transaction.setAmount(fieldSet.readBigDecimal("amount"));
                	transaction.setStatus(fieldSet.readString("status"));
                	transaction.setTransactionDate(fieldSet.readString("transactionDate"));
                	transaction.setDescription(fieldSet.readString("description"));
                    return transaction;
                })
                .build();
    }

    /**
     * Configuration du processeur.
     * Transforme les données lues avant de les écrire.
     */
    @Bean
    public TransactionItemProcessor transactionProcessor() {
        return new TransactionItemProcessor();
    }

    /**
     * Configuration du writer JPA.
     * Écrit les données transformées dans la base PostgreSQL.
     */
    @Bean
    public JpaItemWriter<Transaction> transactionWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Transaction>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    /**
     * Configuration de l'étape (step).
     * Définit la séquence reader -> processor -> writer.
     */
    @Bean
    public Step transactionStep(
    		JobRepository jobRepository, 
    		PlatformTransactionManager transactionManager,
            FlatFileItemReader<Transaction> transactionReader, 
            TransactionItemProcessor transactionProcessor,
            JpaItemWriter<Transaction> transactionWriter,
    		/**
             * listeners
             */
    		StepExecutionNotificationListener stepExecutionNotificationListener,
    		ChunkNotificationListener chunkNotificationListener,
    		ItemReadTransactionListener itemReadTransactionListener,
    		ItemProcessTransactionListener itemProcessTransactionListener,
    		ItemWriteTransactionListener itemWriteTransactionListener) {
        return new StepBuilder("transactionStep", jobRepository)
                .<Transaction, Transaction>chunk(1000, transactionManager) // Taille du chunk pour traitement par lots
                .reader(transactionReader)
                .processor(transactionProcessor)
                .writer(transactionWriter)
                .transactionManager(transactionManager)
                .faultTolerant()
                .retry(Exception.class)      // Retry on any exception
                .retryLimit(3)               // Retry up to 3 times
                /**
                 * listeners
                 */
                .listener(stepExecutionNotificationListener)
                .listener(chunkNotificationListener)
                .listener(itemReadTransactionListener)
                .listener(itemProcessTransactionListener)
                .listener(itemWriteTransactionListener)
//                .listener(SimpleStepBuilder<I, O>)
                .build();
    }

    @Bean
    public Step successTransactionTasklet(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("successTransactionTasklet", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("SUCCESS Step: Exactly 3 transactions processed.");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step warningTransactionTasklet(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("warningTransactionTasklet", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                	log.info("WARNING Step: More/Less than 3 transactions processed.");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}