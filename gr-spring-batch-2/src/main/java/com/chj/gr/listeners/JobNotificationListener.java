package com.chj.gr.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobNotificationListener.class);

//    private final JdbcTemplate jdbcTemplate;

//    @Autowired
//    public JobNotificationListener(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }

    @Override
	public void beforeJob(JobExecution jobExecution) {
    	log.debug("beforeJob...");
	}

	@Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Job terminé avec succès, vérification des resultats...");

//            jdbcTemplate.query("SELECT first_name, last_name FROM batch_entity_person",
//                    (rs, row) -> new Person(
//                            rs.getString(1),
//                            rs.getString(2))
//            ).forEach(person -> log.info("<" + person + "> trouvé dans la database."));
        } else {
            log.error("Job terminé avec des erreurs : {}", jobExecution.getStatus());
        }
    }
}
