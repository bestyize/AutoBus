package com.yize.autobus.publish;

import com.yize.autobus.Subscription;

/**
 * 发布数据封装
 */
class InnerPublishData{

    //订阅
    public Subscription subscription;
    //发布的数据
    public Object data;

    public InnerPublishData(Subscription subscription, Object data) {
        this.subscription = subscription;
        this.data = data;
    }
}