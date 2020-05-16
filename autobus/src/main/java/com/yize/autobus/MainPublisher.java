package com.yize.autobus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MainPublisher extends Handler implements Publisher {
    private static final int MAIN_DATA=0;

    static class InnerPublishData{
        public Subscription subscription;
        public Object data;

        public InnerPublishData(Subscription subscription, Object data) {
            this.subscription = subscription;
            this.data = data;
        }
    }
    private LinkedList<InnerPublishData> publishQueue;
    private Object LOCK=new Object();
    private Thread publishThread;

    public MainPublisher() {
        super(Looper.getMainLooper());
        this.publishQueue = new LinkedList<InnerPublishData>();
    }

    /**
     * 订阅入队，交给一个线程单独处理。
     * 为了防止很多消息同时发送的情况，需要采用异步的方式，而且需要保证线程安全
     * @param subscription
     * @param data
     */
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
                        Message message=new Message();
                        message.what=MAIN_DATA;
                        message.obj=publishQueue.pollLast();
                        sendMessage(message);
                    }
                }
            }
        }
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case MAIN_DATA:
                InnerPublishData publishData=(InnerPublishData) msg.obj;
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
                break;
            default:
                break;
        }

    }
}
