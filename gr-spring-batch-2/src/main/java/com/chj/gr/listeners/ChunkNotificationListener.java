package com.chj.gr.listeners;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ChunkNotificationListener implements ChunkListener {

	@Override
	public void beforeChunk(ChunkContext context) {
		log.debug("beforeChunk");
	}

	@Override
	public void afterChunk(ChunkContext context) {
		log.debug("afterChunk");
	}

	@Override
	public void afterChunkError(ChunkContext context) {
		log.debug("afterChunkError");
	}

	
}
