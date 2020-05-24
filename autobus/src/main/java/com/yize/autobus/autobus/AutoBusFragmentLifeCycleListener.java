package com.yize.autobus.autobus;

/**
 * 对我们有用的一般只有这三个生命周期
 * 1、onStart帮助我们创建
 * 2、onStop可以让我们暂停不必要的加载
 * 3、onDestory可以帮我们自动取消注册
 */
public interface AutoBusFragmentLifeCycleListener {
    void onStart();
    void onStop();
    void onDestroy();
}
