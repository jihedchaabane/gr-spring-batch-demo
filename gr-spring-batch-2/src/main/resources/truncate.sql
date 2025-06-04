TRUNCATE TABLE BATCH_ENTITY_PERSON CASCADE;
TRUNCATE TABLE BATCH_ENTITY_TRANSACTION CASCADE;

ALTER SEQUENCE BATCH_ENTITY_PERSON_ID_SEQ RESTART WITH 1;
ALTER SEQUENCE BATCH_ENTITY_TRANSACTION_ID_SEQ RESTART WITH 1;

-- Script to truncate Spring Batch metadata tables and reset their sequences in PostgreSQL
-- Ensures foreign key relationships are respected by truncating in the correct order

-- Step 1: Truncate BATCH_JOB_INSTANCE with CASCADE
-- This will automatically truncate all dependent tables:
-- 		BATCH_JOB_EXECUTION, 
-- 		BATCH_JOB_EXECUTION_PARAMS,
--		BATCH_STEP_EXECUTION,
-- 		BATCH_STEP_EXECUTION_CONTEXT,
--		BATCH_JOB_EXECUTION_CONTEXT
TRUNCATE TABLE BATCH_JOB_INSTANCE CASCADE;


-- Step 2: Reset the sequences to start from 1
ALTER SEQUENCE BATCH_STEP_EXECUTION_SEQ RESTART WITH 1;
ALTER SEQUENCE BATCH_JOB_EXECUTION_SEQ RESTART WITH 1;
ALTER SEQUENCE BATCH_JOB_SEQ RESTART WITH 1;
-------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------
--BATCH_JOB_INSTANCE: 			No incoming foreign keys, referenced by BATCH_JOB_EXECUTION.
--BATCH_JOB_EXECUTION: 			References BATCH_JOB_INSTANCE, referenced by BATCH_JOB_EXECUTION_PARAMS, BATCH_STEP_EXECUTION, and BATCH_JOB_EXECUTION_CONTEXT.
--BATCH_JOB_EXECUTION_PARAMS: 	References BATCH_JOB_EXECUTION.
--BATCH_STEP_EXECUTION: 		References BATCH_JOB_EXECUTION, referenced by BATCH_STEP_EXECUTION_CONTEXT.
--BATCH_STEP_EXECUTION_CONTEXT: References BATCH_STEP_EXECUTION.
--BATCH_JOB_EXECUTION_CONTEXT: 	References BATCH_JOB_EXECUTION.
-------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------

-- Optional: Commit the transaction to ensure changes are applied
COMMIT;