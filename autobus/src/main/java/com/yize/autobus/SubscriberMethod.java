package com.yize.autobus;

import java.lang.reflect.Method;
import java.util.Objects;

public class SubscriberMethod {
    //订阅的方法
    public final Method method;
    /**
     * 订阅者应该工作的线程
     * 包括主线程、当前线程和异步线程，它们适用于不同的场景
     */
    public final WorkMode workMode;
    /**
     * 订阅方法的优先级，支持高优先、低优先、默认优先三种
     */
    public final WorkPriority workPriority;
    /**
     * 发布的数据类型，也就是订阅者的入口方法的类信息，
     * 在设计的时候，对于同一类型的消息发布是一起的
     */
    public final Class<?> dataType;//订阅方法入口参数类型

    public SubscriberMethod(Method method, WorkMode workMode,WorkPriority workPriority, Class<?> dataType) {
        this.method = method;
        this.workMode = workMode;
        this.workPriority=workPriority;
        this.dataType = dataType;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriberMethod that = (SubscriberMethod) o;
        return Objects.equals(method, that.method) &&
                workMode == that.workMode &&
                workPriority == that.workPriority &&
                Objects.equals(dataType, that.dataType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, workMode, workPriority, dataType);
    }

    @Override
    public String toString() {
        return "SubscriberMethod{" +
                "method=" + method +
                ", workMode=" + workMode +
                ", workPriority=" + workPriority +
                ", dataType=" + dataType +
                '}';
    }
}
