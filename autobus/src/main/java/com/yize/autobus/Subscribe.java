package com.yize.autobus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
    //订阅方法默认的工作线程
    WorkMode workMode() default WorkMode.THREAD_MAIN;
    //订阅方法的优先级
    WorkPriority workPriority() default WorkPriority.PRIORITY_DEFAULT;
}
