package com.yize.autobus.pubish;

import com.yize.autobus.Subscription;

/**
 * 发布者都需要实现此接口
 * 所有的订阅都要入队之后进行消息发布
 */
public interface Publisher {
    void enqueue(Subscription subscription, Object data);
}
