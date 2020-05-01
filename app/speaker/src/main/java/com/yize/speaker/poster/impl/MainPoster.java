package com.yize.speaker.poster.impl;

import android.os.Looper;

import com.yize.speaker.Topic;
import com.yize.speaker.poster.Poster;

public class MainPoster implements Poster {
    final Looper looper;
    private HandlerPoster handlerPoster;
    public MainPoster() {
        looper=Looper.getMainLooper();

    }

    @Override
    public void commit(Topic topic, Object msg) {
        if(handlerPoster==null){
            handlerPoster = new HandlerPoster(looper);
        }
        handlerPoster.commit(topic,msg);
    }
}
