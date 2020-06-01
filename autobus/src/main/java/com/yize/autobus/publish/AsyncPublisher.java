package com.yize.autobus.publish;

import com.yize.autobus.Subscription;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 * 异步发布器，在单独的线程里面执行
 */
public class AsyncPublisher implements Publisher {
    /**
     * 发布队列
     */
    private final LinkedList<InnerPublishData> publishQueue;
    /**
     * 发布过程要保证线程安全，不然有可能会带来null指针问题
     */
    private final Object LOCK=new Object();
    /**
     * 开辟一一个线程专门用于消息发布
     */
    private Thread publishThread;

    public AsyncPublisher() {
        this.publishQueue = new LinkedList<InnerPublishData>();
    }

    /**
     * 编入消息发布队列
     * @param subscription 一个订阅
     * @param data 需要发布的数据
     */
    @Override
    public void enqueue(Subscription subscription, Object data) {
        InnerPublishData publishData=new InnerPublishData(subscription,data);
        synchronized (LOCK){
            publishQueue.offer(publishData);
            LOCK.notifyAll();
        }
        //首次发布时创建一个线程用于消息发布，其后都用此线程进行异步发布
        if(publishThread==null){
            publishThread=new Thread(new InnerPublishRunnable());
            publishThread.start();
        }
    }

    /**
     * 消息发布的执行线程，注意保证线程安全
     */
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
                        //FIFO方式发布
                        InnerPublishData publishData=publishQueue.pollLast();
                        try {
                            Subscription subscription=publishData.subscription;
                            //检查订阅是否还有效
                            if(subscription!=null&&subscription.isAlive){
                                publishData.subscription.subscriberMethod.method.invoke(subscription.subscriber,publishData.data);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
