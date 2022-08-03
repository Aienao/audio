/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package com.ainoe.audio.multithread.batch;

import com.ainoe.audio.multithread.threadpool.CachedThreadPool;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class BatchRunner<T> {
    private final static Logger logger = LoggerFactory.getLogger(BatchRunner.class);

    public static class State {
        private boolean isSucceed = false;
        private Exception exception;

        public boolean isSucceed() {
            return isSucceed;
        }

        public void setSucceed(boolean succeed) {
            isSucceed = succeed;
        }


        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }

    }

    /**
     * @param itemList 对象列表
     * @param parallel 并发度（多少个线程）
     * @param job      执行函数
     */
    public State execute(List<T> itemList, int parallel, BatchJob<T> job) {
        State state = new State();
        if (CollectionUtils.isNotEmpty(itemList)) {
            //状态默认是成功状态，任意线程出现异常则置为失败
            state.setSucceed(true);
            parallel = Math.min(itemList.size(), parallel);
            CountDownLatch latch = new CountDownLatch(parallel);
            for (int i = 0; i < parallel; i++) {
                Runner<T> runner = new Runner<>(i, parallel, itemList, job, latch, state);
                CachedThreadPool.execute(runner);
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return state;
    }


    static class Runner<T> implements Runnable {
        int index;
        int parallel;
        List<T> itemList;
        BatchJob<T> job;
        CountDownLatch latch;
        State state;

        public Runner(int _index, int _parallel, List<T> _itemList, BatchJob<T> _job, CountDownLatch _latch, State _state) {
            index = _index;
            parallel = _parallel;
            itemList = _itemList;
            job = _job;
            latch = _latch;
            state = _state;
        }

        @Override
        public void run() {
            try {
                for (int i = index; i < itemList.size(); i += parallel) {
                    try {
                        job.execute(itemList.get(i));
                    } catch (Exception e) {
                        state.setSucceed(false);
                        state.setException(e);
                        logger.error(e.getMessage(), e);
                    }
                }
            } finally {
                latch.countDown();
            }
        }
    }

}
