# gr-spring-batch-demo  
  
~/.m2/repository/org/springframework/batch/spring-batch-core/spring-batch-core-5.1.2.jar

org/springframework/batch/core/schema-postgresql.sql
org/springframework/batch/core/schema-h2.sql

spring.batch.jdbc.initialize-schema: always
sudo -u postgres psql -d postgres_data_source -f src/main/resources/schema-postgresql.sql