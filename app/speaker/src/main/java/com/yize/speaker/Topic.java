package com.yize.speaker;

import com.yize.speaker.listener.ListenerMethod;

import java.io.Serializable;

public class Topic implements Serializable {
    public static final int serialVersionUid=0x12345678;
    public final Object listener;
    public final ListenerMethod listenerMethod;

    public Topic(Object listener, ListenerMethod listenerMethod) {
        this.listener = listener;
        this.listenerMethod = listenerMethod;
    }
}
