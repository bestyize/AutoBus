package com.yize.autobus;

import android.app.Activity;
import android.app.FragmentManager;
import android.util.Log;

import com.yize.autobus.autobus.AutoBusBlankFragment;
import com.yize.autobus.autobus.AutoBusFragmentLifeCycleListener;


import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class AutoBus {
    private static final String TAG="com.yize.litebus.AutoBus";
    //双检锁要保证变量的可见性
    private static volatile AutoBus DEFAULT_INSTANCE;
    //private Object subscribrActivity;
    private static Stack<Object> subscriberStack;//订阅者放在一个栈里面,按此顺序销毁activity,Stack是线程安全的容器，不需要我们同步。
    /**
     * 单例模式创建AutoBus实体
     * @param activity
     * @return
     */
    public static AutoBus with(Object activity){
        if(DEFAULT_INSTANCE==null){
            synchronized (AutoBus.class){
                if(DEFAULT_INSTANCE==null){
                    DEFAULT_INSTANCE=new AutoBus();
                }
            }
        }
        DEFAULT_INSTANCE.addSubscriber(activity);
        return DEFAULT_INSTANCE;
    }

    private AutoBus(){
        subscriberStack=new Stack<>();
    }

    public void addSubscriber(Object activity){
        LiteBus.getAutoBus().register(activity);
        //subscribrActivity=activity;
        subscriberStack.push(activity);
        register(activity);
    }

    /**
     * 空白fragment的生命周期监听回调
     */
    private AutoBusFragmentLifeCycleListener lifeCycleListener=new AutoBusFragmentLifeCycleListener() {
        @Override
        public void onStart() {
            Log.i("AutoBus","onStart()");
        }

        @Override
        public void onStop() {
            Log.i("AutoBus","onStop()");
        }

        @Override
        public void onDestroy() {
            Log.i("AutoBus","onDestroy()");
            if(subscriberStack.isEmpty()==false){
                LiteBus.getAutoBus().unregister(subscriberStack.pop());
            }else {
                Log.i("AutoBus","订阅者已经清空了");
            }
        }
    };

    /**
     * 作用：为activity或者fragment绑定一个空白的fragment，
     * activity或者fragment生命周期的变化会首先传递到fragment上去，因此设置
     * 回调函数来监听空白的fragment的生命周期就能监听注册者的生命周期，
     * 以此来完成自动注册和自动解除注册的功能
     *
     * @param activity 需要被绑定的activity
     */
    private void register(Object activity){
        if(activity instanceof Activity){
            FragmentManager fm=((Activity)activity).getFragmentManager();
            AutoBusBlankFragment blankFragment=getBlankFragment(fm);
            blankFragment.getLifeCycle().registerListener(lifeCycleListener);
        }else {
            Log.i("AutoBus","传入的类不属于activity，无法完成注册");
        }
    }

    /**
     * 防止重复创建一个fragment浪费资源，通过TAG来标识
     * @param fm fragment管理器
     * @return
     */
    private AutoBusBlankFragment getBlankFragment(FragmentManager fm) {
        AutoBusBlankFragment blankFragment=(AutoBusBlankFragment)fm.findFragmentByTag(TAG);
        if(blankFragment==null){
            blankFragment=new AutoBusBlankFragment();
            fm.beginTransaction().add(blankFragment,TAG).commitAllowingStateLoss();
        }
        return blankFragment;

    }

    /**
     * 数据发布函数，需要知道数据类型，然后发布，最终消息的发布也是根据消息类型来区分的
     * 这个数据类型可以是自定义（推荐）也可以用Java自带的类，比如String之类的
     * @param data
     */
    public void publish(Object data){
        LiteBus.getAutoBus().publish(data);
    }
}
