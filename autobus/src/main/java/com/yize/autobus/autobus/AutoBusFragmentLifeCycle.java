package com.yize.autobus.autobus;

import android.util.Log;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class AutoBusFragmentLifeCycle  {
    private static String TAG="AutoBusFragmentLifeCycle";


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

    public void onDestroy() {
        Log.i(TAG,"onDestroy()");
        isDestroyed=true;
        for (AutoBusFragmentLifeCycleListener lifeCycleListener:lifeCycleListenerSet){
            lifeCycleListener.onDestroy();
        }
    }
}
