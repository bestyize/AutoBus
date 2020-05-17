package com.yize.autobus.pubish;

import android.os.Message;

import com.yize.autobus.Subscription;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 * 异步发布器，在单独的线程里面执行
 */
public class AsyncPublisher implements Publisher {

    private LinkedList<InnerPublishData> publishQueue;
    private Object LOCK=new Object();
    private Thread publishThread;

    public AsyncPublisher() {
        this.publishQueue = new LinkedList<InnerPublishData>();
    }

    @Override
    public void enqueue(Subscription subscription, Object data) {
        InnerPublishData publishData=new InnerPublishData(subscription,data);
        synchronized (LOCK){
            publishQueue.offer(publishData);
            LOCK.notifyAll();
        }
        if(publishThread==null){
            publishThread=new Thread(new InnerPublishRunnable());
            publishThread.start();
        }
    }

    private class InnerPublishRunnable implements Runnable{

        @Override
        public void run() {
            while (true){
                synchronized (LOCK){
                    while (publishQueue.isEmpty()){
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (!publishQueue.isEmpty()){
                        InnerPublishData publishData=publishQueue.pollLast();
                        try {
                            Subscription subscription=publishData.subscription;
                            if(subscription!=null&&subscription.isAlive){
                                publishData.subscription.subscriberMethod.method.invoke(subscription.subscriber,publishData.data);
                            }

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
