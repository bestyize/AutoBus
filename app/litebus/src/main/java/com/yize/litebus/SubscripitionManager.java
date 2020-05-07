package com.yize.litebus;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SubscripitionManager {
    private static final int MAX_THREAD_SIZE=64;
    private static final int CORE_THREAD_SIZE=5;
    private ExecutorService executor;
    private final Deque<Runnable> syncCleanupQueue=new LinkedList<>();



    public SubscripitionManager() {
        if(executor==null){
            executor=new ThreadPoolExecutor(CORE_THREAD_SIZE,MAX_THREAD_SIZE,1, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(MAX_THREAD_SIZE));
        }
    }

    /**
     * 使用线程池
     * @param runnable
     */
    public void enqueue(Runnable runnable){
        synchronized (this){
            syncCleanupQueue.offerLast(runnable);
        }
        for (Runnable r:syncCleanupQueue){
            executor.execute(r);
        }
    }


}
