package com.yize.speaker.poster.impl;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.yize.speaker.Topic;
import com.yize.speaker.poster.Poster;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

public class HandlerPoster extends Handler implements Poster {

    public HandlerPoster(@NonNull Looper looper) {
        super(looper);
    }

    @Override
    public void commit(Topic topic,Object event) {
        Message message=new Message();
        HandlerResult result=new HandlerResult(topic,event);
        message.obj=result;
        sendMessage(message);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        HandlerResult result= (HandlerResult) msg.obj;
        Topic topic=result.topic;
        Object message=result.data;

        try {
            topic.listenerMethod.method.invoke(topic.listener,message);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    private static class HandlerResult{
        final Topic topic;
        final Object data;

        public HandlerResult(Topic topic, Object data) {
            this.topic = topic;
            this.data = data;
        }
    }
}
