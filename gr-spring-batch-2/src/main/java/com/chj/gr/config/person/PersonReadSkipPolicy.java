package com.chj.gr.config.person;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * SkipPolicy pour gérer les exceptions lors de la lecture des persons.
 */
@Component
@Slf4j
public class PersonReadSkipPolicy implements SkipPolicy {

    @Override
    public boolean shouldSkip(Throwable throwable, long skipCount) throws SkipLimitExceededException {
        if (throwable instanceof IllegalArgumentException || throwable.getCause() instanceof IllegalArgumentException) {
            log.error("Skipping person => IllegalArgumentException: {}, skip count {}", throwable.getMessage(), skipCount);
            return true;
        }
        return false;
    }
}