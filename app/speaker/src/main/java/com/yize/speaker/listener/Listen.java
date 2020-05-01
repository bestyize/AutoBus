package com.yize.speaker.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listen {
    //默认发布到主线程
    ListenMode listenMode() default ListenMode.MAIN_THREAD;
}
