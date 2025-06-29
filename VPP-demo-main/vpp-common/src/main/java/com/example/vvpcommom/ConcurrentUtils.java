package com.example.vvpcommom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ConcurrentUtils {
	private static final Logger log = LoggerFactory.getLogger(ConcurrentUtils.class);

	public static <T> Future<T> doJob(ExecutorService executorService, Callable<T> callable) {
		return executorService.submit(callable);
	}

	public static <T> T futureGet(Future<T> future) {
		try {
			return future.get();
		} catch (ExecutionException | InterruptedException e) {
			log.error("excture failed", e);
			throw new RuntimeException(e);
		}
	}
}
