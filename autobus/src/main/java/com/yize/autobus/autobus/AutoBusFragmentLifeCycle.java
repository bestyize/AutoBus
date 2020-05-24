package com.yize.autobus.autobus;

import android.util.Log;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 对所有注册的fragment的生命周期进行统一的管理。
 */
public class AutoBusFragmentLifeCycle  {
    private static String TAG="AutoBusFragmentLifeCycle";

    /**
     * 保证不会重复注册同一个空白的fragment
     */
    private Set<AutoBusFragmentLifeCycleListener> lifeCycleListenerSet= Collections.newSetFromMap(new WeakHashMap<AutoBusFragmentLifeCycleListener, Boolean>());
    private boolean isStarted;
    private boolean isDestroyed;

    public void registerListener(AutoBusFragmentLifeCycleListener autoBusFragmentLifeCycleListener){
        lifeCycleListenerSet.add(autoBusFragmentLifeCycleListener);
        if(isDestroyed){
            autoBusFragmentLifeCycleListener.onDestroy();
        }else if(isStarted){
            autoBusFragmentLifeCycleListener.onStart();
        }else {
            autoBusFragmentLifeCycleListener.onStop();
        }
    }

    /**
     * 每次经历生命周期的onStart方法，都会调用此函数
     */
    public void onStart() {
        Log.i(TAG,"onStart()");
        isStarted=true;
        for (AutoBusFragmentLifeCycleListener lifeCycleListener:lifeCycleListenerSet){
            lifeCycleListener.onStart();
        }
    }

    public void onStop() {
        Log.i(TAG,"onStop()");
        isStarted=false;
        for (AutoBusFragmentLifeCycleListener lifeCycleListener:lifeCycleListenerSet){
            lifeCycleListener.onStop();
        }
    }

    /**
     * fragment的生命周期结束之前会调用此方法
     * 我们也是在这里直接回调
     */
    public void onDestroy() {
        Log.i(TAG,"onDestroy()");
        isDestroyed=true;
        for (AutoBusFragmentLifeCycleListener lifeCycleListener:lifeCycleListenerSet){
            lifeCycleListener.onDestroy();
        }
    }
}
