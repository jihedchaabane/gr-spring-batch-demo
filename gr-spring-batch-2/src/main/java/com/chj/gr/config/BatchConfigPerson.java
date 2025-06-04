package com.chj.gr.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.chj.gr.entity.Person;
import com.chj.gr.listeners.ChunkNotificationListener;
import com.chj.gr.listeners.StepExecutionNotificationListener;
import com.chj.gr.listeners.person.ItemProcessNotificationListener;
import com.chj.gr.listeners.person.ItemReaderNotificationListener;
import com.chj.gr.listeners.person.ItemWriteNotificationListener;
import com.chj.gr.processors.PersonItemProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration principale de Spring Batch.
 * Définit le job, les étapes, et les composants (reader, processor, writer).
 */
@Configuration
@Slf4j
public class BatchConfigPerson {

	/**
     * Configuration du lecteur de fichier CSV.
     * Lit les données d'entrée depuis un fichier CSV.
     */
    @Bean
    public FlatFileItemReader<Person> personReader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("persons.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .linesToSkip(1)
                .targetType(Person.class)
                .fieldSetMapper(fieldSet -> {
                    Person person = new Person();
                    person.setFirstName(fieldSet.readString("firstName"));
                    person.setLastName(fieldSet.readString("lastName"));
                    return person;
                })
                .build();
    }

    /**
     * Configuration du processeur.
     * Transforme les données lues avant de les écrire.
     */
    @Bean
    public PersonItemProcessor personProcessor() {
        return new PersonItemProcessor();
    }
    
    /**
     * Configuration du writer.
     * Écrit les données transformées dans la base PostgreSQL.
     */
    @Bean
    public JdbcBatchItemWriter<Person> personWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO batch_entity_person (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }
    
    /**
     * Configuration de l'étape (step).
     * Définit la séquence reader -> processor -> writer.
     */
    @Bean
    public Step personStep(
    		JobRepository jobRepository, 
    		PlatformTransactionManager transactionManager, 
    		FlatFileItemReader<Person> personReader, 
    		PersonItemProcessor personProcessor,
    		JdbcBatchItemWriter<Person> personWriter,
    		StepExecutionNotificationListener stepExecutionNotificationListener,
    		ChunkNotificationListener chunkNotificationListener,
    		ItemReaderNotificationListener itemReaderNotificationListener,
    		ItemProcessNotificationListener itemProcessNotificationListener,
    		ItemWriteNotificationListener itemWriteNotificationListener) {
    	
        return new StepBuilder("personStep", jobRepository)
                .<Person, Person>chunk(10, transactionManager) // Taille du chunk pour traitement par lots
                .reader(personReader)
                .processor(personProcessor)
                .writer(personWriter)
                .transactionManager(transactionManager)
                .faultTolerant()
                .retry(Exception.class)      // Retry on any exception
                .retryLimit(3)               // Retry up to 3 times
                /**
                 * listeners
                 */
                .listener(stepExecutionNotificationListener)
                .listener(chunkNotificationListener)
                .listener(itemReaderNotificationListener)
                .listener(itemProcessNotificationListener)
                .listener(itemWriteNotificationListener)
//                .listener(SimpleStepBuilder<I, O>)
                .build();
    }

    @Bean
    public Step successPersonTasklet(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("successPersonTasklet", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("SUCCESS Step: Exactly 24 persons processed.");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step warningPersonTasklet(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("warningPersonTasklet", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                	log.info("WARNING Step: More/Less than 24 persons processed.");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
