package com.yize.litebus;

import android.app.Activity;
import android.app.FragmentManager;
import android.util.Log;

import com.yize.litebus.autobus.AutoBusBlankFragment;
import com.yize.litebus.autobus.AutoBusFragmentLifeCycleListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AutoBus {
    private static final String TAG="com.yize.litebus.AutoBus";
    private static volatile AutoBus DEFAULT_INSTANCE;
    private static Object subscribrActivity;
    public static AutoBus with(Object activity){
        if(DEFAULT_INSTANCE==null){
            synchronized (AutoBus.class){
                if(DEFAULT_INSTANCE==null){
                    DEFAULT_INSTANCE=new AutoBus(activity);
                }
            }
        }
        return DEFAULT_INSTANCE;
    }

    public AutoBus(Object activity) {
        LiteBus.getAutoBus().register(activity);
        subscribrActivity=activity;
        register(activity);
    }

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
            LiteBus.getAutoBus().unregister(subscribrActivity);
            subscribrActivity=null;
        }
    };

    private Map<FragmentManager, AutoBusBlankFragment> FRAGMENT_MANAGER_CHACE=new ConcurrentHashMap<>();



    private void register(Object activity){
        if(activity instanceof Activity){
            FragmentManager fm=((Activity)activity).getFragmentManager();
            AutoBusBlankFragment blankFragment=getBlankFragment(fm);
            blankFragment.getLifeCycle().registerListener(lifeCycleListener);
        }
    }

    private AutoBusBlankFragment getBlankFragment(FragmentManager fm) {
        AutoBusBlankFragment blankFragment=(AutoBusBlankFragment)fm.findFragmentByTag(TAG);
        if(blankFragment==null){
            blankFragment=new AutoBusBlankFragment();
            fm.beginTransaction().add(blankFragment,TAG).commitAllowingStateLoss();
        }
        return blankFragment;

    }

    public void post(Object data){
        LiteBus.getAutoBus().publish(data);
    }
}
