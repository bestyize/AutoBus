package com.yize.autobus.pubish;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.yize.autobus.Subscription;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 * 发布消息到主线程，为了实现线程间的数据交换，我们需要继承Handler
 * 获取到主线程的Handler，把消息放到Handler的消息队列上，这样主线程就可以
 * 从消息队列上取出消息，并且进行消息的发布
 */
public class MainPublisher extends Handler implements Publisher {
    private static final int MAIN_DATA=0;
    //主线程的消息发布队列
    private LinkedList<InnerPublishData> publishQueue;
    //上锁保证线程安全
    private Object LOCK=new Object();
    //避免重复创建线程，只是用一个线程执行消息的发布操作
    private Thread publishThread;

    public MainPublisher() {
        //初始化主Looper，获取一个拥有主线程Looper的Handler对象
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

    /**
     * 主线程的内部数据发送函数，没有消息的时候就放弃锁
     * 防止空循环死锁
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
                        Message message=new Message();
                        message.what=MAIN_DATA;
                        message.obj=publishQueue.pollLast();
                        sendMessage(message);
                    }
                }
            }
        }
    }

    /*
     * 对Handler的重写，在这里面已经切换到主线程
     * 可以执行拥有主线程订阅的函数了
     * @param msg
     */
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
