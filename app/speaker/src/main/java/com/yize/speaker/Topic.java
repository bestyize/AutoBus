package com.yize.speaker;

import com.yize.speaker.listener.ListenerMethod;

public class Topic {
    public final Object listener;
    public final ListenerMethod listenerMethod;

    public Topic(Object listener, ListenerMethod listenerMethod) {
        this.listener = listener;
        this.listenerMethod = listenerMethod;
    }
}
