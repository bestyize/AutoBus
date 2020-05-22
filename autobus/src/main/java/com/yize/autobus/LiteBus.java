package com.yize.autobus;

import android.os.Looper;
import android.util.Log;

import com.yize.autobus.pubish.AsyncPublisher;
import com.yize.autobus.pubish.MainPublisher;
import com.yize.autobus.pubish.Publisher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class LiteBus {
    private static final String TAG="LiteBus";
    //默认实例，用来单独使用的
    private volatile static LiteBus DEFAULT_INSTANCE;
    public static LiteBus defaultBus(){
        if(DEFAULT_INSTANCE==null){
            synchronized (LiteBus.class){
                if(DEFAULT_INSTANCE==null){
                    DEFAULT_INSTANCE=new LiteBus();
                }
            }
        }

        return DEFAULT_INSTANCE;
    }

    /**
     * AutoBus使用的实例，防止重复创建对象
     */
    private volatile static LiteBus AUTO_BUS_INSTANCE;

    protected static LiteBus getAutoBus(){
        if(AUTO_BUS_INSTANCE==null){
            synchronized (LiteBus.class){
                if(AUTO_BUS_INSTANCE==null){
                    AUTO_BUS_INSTANCE=new LiteBus();
                }
            }
        }

        return AUTO_BUS_INSTANCE;
    }

    private LiteBus(){
        METHOD_CACHE=new ConcurrentHashMap<Class<?>, List<SubscriberMethod>>();
        subscriptionBus=new ConcurrentHashMap<Class<?>, List<Subscription>>();
        SUBSCRIPTION_CACHE=new ConcurrentHashMap<Class<?>, List<Subscription>>();
        dataTypeList=new CopyOnWriteArrayList<>();
        subscriberDataTypeList=new HashMap<>();
        mainPublisher=new MainPublisher();
        asyncPublisher=new AsyncPublisher();
    }
    //订阅者的方法缓存，key为订阅者类
    private volatile Map<Class<?>, List<SubscriberMethod>> METHOD_CACHE;


    //订阅总线，key为数据类型，value是订阅者的订阅。在消息发布的时候根据消息类型进行派发
    private volatile Map<Class<?>,List<Subscription>> subscriptionBus;
    //订阅者的订阅缓存，key是订阅者类
    private volatile Map<Class<?>,List<Subscription>> SUBSCRIPTION_CACHE;
    //所有的消息类型种类列表
    private volatile List<Class<?>> dataTypeList;
    //订阅者类的消息种类列表
    private volatile Map<Class<?>,List<Class<?>>> subscriberDataTypeList;

    //主发布器
    private Publisher mainPublisher;
    //异步发布器，专门放大另外一个线程里面发布
    private Publisher asyncPublisher;


    /**
     * 注册数据总线，根据数据类型的不同，放到Map对应位置
     * @param subscriber
     */
    public void register(Object subscriber){
        Class<?> subscriberClass=subscriber.getClass();
        List<SubscriberMethod> subscriberMethodList=findSubscribeMethods(subscriberClass);
        if(subscriberMethodList.size()==0){
            Log.e("LiteBus","所在类没有订阅方法");
            return;
        }
        synchronized (this){
            for (SubscriberMethod subscriberMethod:subscriberMethodList){
                subscribeByDataType(subscriber,subscriberMethod);
            }

        }

    }

    /**
     * 使用反射获取订阅方法列表，也就是那些加了注解的方法，这里限制只能使用public的方法可以被注册
     * @param subscriberClass
     * @return
     */
    private List<SubscriberMethod> findSubscribeMethods(Class<?> subscriberClass){
        if(METHOD_CACHE.containsKey(subscriberClass)){
            return METHOD_CACHE.get(subscriberClass);
        }
        List<SubscriberMethod> subscriberMethodList=new LinkedList<SubscriberMethod>();
        Method[] methods=subscriberClass.getDeclaredMethods();
        for (Method method:methods){
            if(method.isAnnotationPresent(Subscribe.class)){
                Subscribe subscribe=method.getAnnotation(Subscribe.class);
                WorkMode workMode=subscribe.workMode();//入口方法的工作模式，也就是作用的线程
                Class<?> dataType=method.getParameterTypes()[0];//获取订阅方法的入口参数的类型
                if(!dataTypeList.contains(dataType)){
                    dataTypeList.add(dataType);
                }
                SubscriberMethod subscriberMethod=new SubscriberMethod(method,workMode,dataType);
                subscriberMethodList.add(subscriberMethod);
            }
        }
        METHOD_CACHE.put(subscriberClass,subscriberMethodList);//放到类订阅方法的缓存里
        return subscriberMethodList;
    }
    /**
     * 为每个方法新建一个订阅，根据数据类型不同，放到一个订阅列表里
     * @param subscriber
     * @param subscriberMethod
     */
    private void subscribeByDataType(Object subscriber, SubscriberMethod subscriberMethod){
        Subscription subscription=new Subscription(subscriber,subscriberMethod);
        Class<?> dataType=subscriberMethod.dataType;
        List<Subscription> subscriptionList=subscriptionBus.get(dataType);
        if(subscriptionList==null){
            subscriptionList=new CopyOnWriteArrayList<Subscription>();
            subscriptionBus.put(dataType,subscriptionList);
        }

        List<Subscription> cachedSubscriptionList=SUBSCRIPTION_CACHE.get(subscriber.getClass());
        if(cachedSubscriptionList==null){
            cachedSubscriptionList=new LinkedList<>();
        }
        cachedSubscriptionList.add(subscription);
        subscriptionList.add(subscription);
    }

    /**
     * 普通的数据发布函数。
     * 在发布之前应该查看当前发布发布线程的状态，看一下是否处于发布状态
     * 为了保证绝对的线程安全，需要使用ThreadLocal实现线程隔离
     * @param data
     */
    public void publish(Object data){
        PublishThreadState currState=currentPublishState.get();
        List<Object> dataQueue=currState.dataQueue;
        dataQueue.add(data);
        if(!currState.isPublishing){
            currState.isMainThread= Looper.myLooper()==Looper.getMainLooper();
            currState.isPublishing=true;
            try {
                while (dataQueue.size()>0){
                    //消息按个发布，每次发布队列最前面的数据，目前是按FIFO的方式推送消息，考虑后续加上优先级。
                    publishSingleData(dataQueue.remove(0),currState);
                }
            }finally {
                //发布完成后一定要改变状态，否则消息发布会受到阻塞
                currState.isMainThread=false;
                currState.isPublishing=false;
            }
        }
    }

    /**
     * 发布消息
     * @param data
     * @param currState
     */
    private void publishSingleData(Object data,PublishThreadState currState){
        Class<?> dataType=data.getClass();
        List<Subscription> subscriptionList=subscriptionBus.get(dataType);//根据数据类型获取订阅列表
        for(Subscription subscription:subscriptionList){//为每个订阅发布消息
            publishToSubscriber(subscription,data,currState.isMainThread);
        }
    }

    /**
     * 向每个订阅者发布消息
     * @param subscription
     * @param data
     * @param isMainThread
     */
    private void publishToSubscriber(Subscription subscription, Object data, boolean isMainThread){
        WorkMode workMode=subscription.subscriberMethod.workMode;
        switch (workMode){
            case THREAD_MAIN:
                if(isMainThread){
                    invoke(subscription,data);
                }else {
                    mainPublisher.enqueue(subscription,data);
                }
                break;
            case THREAD_SYNC:
                invoke(subscription,data);
                break;
            case THREAD_ASYNC:
                asyncPublisher.enqueue(subscription,data);
                break;
            default:
                break;
        }
    }

    /**
     * 反射执行方法
     * @param subscription
     * @param data
     */
    private void invoke(Subscription subscription, Object data){
        try {
            subscription.subscriberMethod.method.invoke(subscription.subscriber,data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 线程发布状态，使用ThreadLocal保证线程隔离
     */
    private final ThreadLocal<PublishThreadState> currentPublishState=new ThreadLocal<PublishThreadState>(){
        @Override
        protected PublishThreadState initialValue() {
            return new PublishThreadState();
        }
    };

    /**
     * 当前线程的发布状态
     */
    static class PublishThreadState{
        final List<Object> dataQueue=new LinkedList<Object>();
        boolean isMainThread;
        boolean isPublishing;
    }

    /**
     * 解除数据总线上的注册
     * @param subscriber
     */
    public void unregister(Object subscriber){
        subscripitionManager.enqueue(new CleanUpWoker(subscriber));
    }

    /**
     * 采用异步的方式退出订阅状态。
     */
    private class CleanUpWoker implements Runnable{

        private final Object subscriber;

        CleanUpWoker(Object subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void run() {
            Class<?> subscriberClass=subscriber.getClass();
            List<SubscriberMethod> subscriberMethods=METHOD_CACHE.get(subscriberClass);
            if(subscriberMethods.size()==0){
                Log.i(TAG,"unregister failed ,there is no subscriber");
                return;
            }
            METHOD_CACHE.remove(subscriberClass);
            synchronized (this){
                for (Class<?> dataType:dataTypeList){
                    List<Subscription> subscriptionList=subscriptionBus.get(dataType);
                    if(subscriptionList!=null&&subscriptionList.size()>0){
                        int curr=0;
                        while (curr<subscriptionList.size()){
                            if(subscriptionList.get(curr).subscriber==subscriber){
                                subscriptionList.remove(curr).isAlive=false;
                            }else {
                                curr++;
                            }
                        }
                    }
                }
            }
        }
    }

    private final SubscripitionManager subscripitionManager=new SubscripitionManager();

}
