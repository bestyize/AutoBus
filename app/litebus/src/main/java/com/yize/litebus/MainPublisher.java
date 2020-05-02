package com.yize.litebus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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

    public MainPublisher() {
        super(Looper.getMainLooper());
        this.publishQueue = new LinkedList<InnerPublishData>();
    }

    public void enqueue(Subscription subscription, Object data) {
        InnerPublishData publishData=new InnerPublishData(subscription,data);
        publishQueue.offer(publishData);

        while (!publishQueue.isEmpty()){
            Message message=new Message();
            message.what=MAIN_DATA;
            message.obj=publishQueue.pollLast();
            sendMessage(message);
        }
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case MAIN_DATA:
                InnerPublishData publishData=(InnerPublishData) msg.obj;
                try {
                    publishData.subscription.subscriberMethod.method.invoke(publishData.subscription.subscriber,publishData.data);
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
