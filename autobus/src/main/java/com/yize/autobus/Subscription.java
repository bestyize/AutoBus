package com.yize.autobus;

import java.util.Objects;

/**
 * 对于每个被subscribe注解的public方法
 * 我们认为其属于一个订阅
 * 一个订阅的要素就是
 * 1、订阅者所属于的类，用来反射传递给订阅者消息
 * 2、订阅的方法本身（也就是订阅者）
 * 3、当前订阅的状态（由于订阅者所属于的类可能被销毁，在销毁之后，订阅者状态应该设置为无效，防止不必要的发布，null问题等）
 */
public class Subscription {
    /**
     * 订阅者
     */
    public final Object subscriber;
    /**
     * 订阅方法的具体信息
     */
    public final SubscriberMethod subscriberMethod;
    /**
     * 订阅者的状态，默认为true,因为订阅者只有有价值才会被创建
     */
    public volatile boolean isAlive=true;

    public Subscription(Object subscriber, SubscriberMethod subscriberMethod) {
        this.subscriber = subscriber;
        this.subscriberMethod = subscriberMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return isAlive == that.isAlive &&
                Objects.equals(subscriber, that.subscriber) &&
                Objects.equals(subscriberMethod, that.subscriberMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriber, subscriberMethod, isAlive);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "subscriber=" + subscriber +
                ", subscriberMethod=" + subscriberMethod +
                ", isAlive=" + isAlive +
                '}';
    }
}
