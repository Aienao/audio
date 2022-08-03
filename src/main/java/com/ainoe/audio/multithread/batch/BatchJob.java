package com.ainoe.audio.multithread.batch;

public interface BatchJob<T> {
    void execute(T item);
}
