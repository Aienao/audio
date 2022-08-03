package com.ainoe.audio.multithread.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class CachedThreadPool {
    static int cpu = Runtime.getRuntime().availableProcessors();
    private static final Logger logger = LoggerFactory.getLogger(CachedThreadPool.class);
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(0, cpu * 15,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new rejectHandler());

    public static void execute(Runnable command) {
        try {
            threadPool.execute(command);
        } catch (RejectedExecutionException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    static class rejectHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            logger.warn("thread pool(size:" + (cpu * 15) + ") is full.");
        }
    }

    public static int getThreadActiveCount() {
        return threadPool.getActiveCount();
    }
}
