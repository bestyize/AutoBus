package com.yize.speaker.listener;

import java.lang.reflect.Method;

public class ListenerMethod {
    public final Method method;
    public final ListenMode mode;

    public ListenerMethod(Method method, ListenMode mode) {
        this.method = method;
        this.mode = mode;
    }
}
