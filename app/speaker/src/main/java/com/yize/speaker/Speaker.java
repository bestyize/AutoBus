package com.yize.speaker;

import android.os.Looper;

import androidx.annotation.Nullable;

import com.yize.speaker.listener.Listen;
import com.yize.speaker.listener.ListenMode;
import com.yize.speaker.listener.ListenerMethod;
import com.yize.speaker.poster.impl.MainPoster;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Speaker {
    //默认的speaker实例
    private volatile static Speaker DEFAULT_SPEAKER;
    //双检锁单例模式
    public static Speaker defaultSpeaker(){
        if(DEFAULT_SPEAKER==null){
            synchronized (Speaker.class){
                if(DEFAULT_SPEAKER==null){
                    DEFAULT_SPEAKER=new Speaker();
                }
            }
        }
        return DEFAULT_SPEAKER;
    }
    //主发布器
    private final MainPoster mainPoster;
    //按照订阅者，对话题进行发布
    private final Map<Class<?>, CopyOnWriteArrayList<Topic>> listenerMap;
    //订阅方法的缓存池，防止重复反射
    private static final Map<Class<?>,List<ListenerMethod>> METHOD_CACHE=new ConcurrentHashMap<>();

    private static final List<Class<?>> listenerList=new LinkedList<>();
    public Speaker(){
        mainPoster=new MainPoster();
        listenerMap=new HashMap<>();
    }

    private final ThreadLocal<SpeakingThreadState> currSpeakingThreadState=new ThreadLocal<SpeakingThreadState>(){
        @Nullable
        @Override
        protected SpeakingThreadState initialValue() {
            return new SpeakingThreadState();
        }
    };

    //将订阅事件注册到监听列表里面
    public void register(Object listener){
        Class<?> listenClass=listener.getClass();
        listenerList.add(listenClass);
        List<ListenerMethod> listenerMethods=findListenerMethod(listenClass);
        CopyOnWriteArrayList<Topic> topics=listenerMap.get(listenClass);
        if(topics==null){
            topics=new CopyOnWriteArrayList<>();
        }
        synchronized (this){
            for (ListenerMethod listenerMethod:listenerMethods){
                Topic topic=new Topic(listener,listenerMethod);
                topics.add(topic);
            }
        }
        listenerMap.put(listenClass,topics);
    }

    //利用反射获取注册监听事件类的所有订阅方法
    public List<ListenerMethod> findListenerMethod(Class<?> listenClass){
        List<ListenerMethod> listenerMethods=METHOD_CACHE.get(listenClass);
        if(listenerMethods!=null){
            return listenerMethods;
        }
        listenerMethods=new ArrayList<>();
        Method[] methods;
        methods=listenClass.getDeclaredMethods();//利用反射获取所有的公有方法
        for (Method method:methods){
            if(method.getParameterTypes().length==1){
                Listen listen=method.getAnnotation(Listen.class);
                if(listen!=null){
                    ListenMode listenMode=listen.listenMode();
                    ListenerMethod listenerMethod=new ListenerMethod(method,listenMode);
                    listenerMethods.add(listenerMethod);
                    METHOD_CACHE.put(listenClass,listenerMethods);
                }
            }
        }
        return listenerMethods;

    }

    public void speakToAll(Object msg){
        SpeakingThreadState speakingThreadState=currSpeakingThreadState.get();
        List<Object> msgQueue=speakingThreadState.msgQueue;
        msgQueue.add(msg);
        if(!speakingThreadState.isSpeaking){
            speakingThreadState.isMainThread= Looper.myLooper()==Looper.getMainLooper();
            speakingThreadState.isSpeaking=true;
            try{
                while (!msgQueue.isEmpty()){
                    speakSingleMsg(msgQueue.remove(0),speakingThreadState);
                }
            }finally {
                speakingThreadState.isMainThread=false;
                speakingThreadState.isSpeaking=false;
            }

        }
    }

    //分发单个消息
    private void speakSingleMsg(Object msg, SpeakingThreadState speakingThreadState) {
        CopyOnWriteArrayList<ListenerMethod> listenerMethods;
        List<Class<?>> listenerList=findAllListener();
        for (Class<?> listener:listenerList){
            speakToOneClass(msg,speakingThreadState,listener);
        }
    }

    //为每个监听者类分发话题
    private void speakToOneClass(Object msg, SpeakingThreadState speakingThreadState, Class<?> listener) {
        CopyOnWriteArrayList<Topic> listenerMethods;
        synchronized (this){
            listenerMethods=listenerMap.get(listener);
        }
        if(listenerMethods!=null&&listenerMethods.size()>0){
            for(Topic topic:listenerMethods){
                speakToOnMethod(msg,speakingThreadState,topic,speakingThreadState.isMainThread);
            }
        }
    }

    private void speakToOnMethod(Object msg, SpeakingThreadState speakingThreadState, Topic topic, boolean isMainThread) {
        switch (topic.listenerMethod.mode){
            case SAME_THREAD:
                break;
            case MAIN_THREAD:
                if(speakingThreadState.isMainThread){
                    try {
                        topic.listenerMethod.method.invoke(topic.listener,msg);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }else {
                    mainPoster.commit(topic,msg);
                }
                break;
            default:
                break;
        }
    }






    private List<Class<?>> findAllListener(){
        return listenerList;
    }

    final static class SpeakingThreadState{
        final List<Object> msgQueue=new LinkedList<>();
        boolean isSpeaking;
        boolean isMainThread;

    }

}
