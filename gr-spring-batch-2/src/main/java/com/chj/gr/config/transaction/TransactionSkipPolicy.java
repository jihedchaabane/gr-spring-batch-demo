package com.chj.gr.config.transaction;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * SkipPolicy pour gérer les exceptions lors de la gestion des transactions (read/process/write).
 * Autorise le skip jusqu'à une limite.
 */
@Component
@Slf4j
public class TransactionSkipPolicy implements SkipPolicy {

    @Override
    public boolean shouldSkip(Throwable throwable, long skipCount) throws SkipLimitExceededException {
        if (throwable instanceof NumberFormatException || throwable.getCause() instanceof NumberFormatException) {
            log.error("Skipping transaction=> NumberFormatException: {}, skip count {}", throwable.getMessage(), skipCount);
            return true;
        }
        if (throwable instanceof IllegalArgumentException || throwable.getCause() instanceof IllegalArgumentException) {
            log.error("Skipping transaction=> IllegalArgumentException: {}, skip count {}", throwable.getMessage(), skipCount);
            return true;
        }
        return false;
    }
}