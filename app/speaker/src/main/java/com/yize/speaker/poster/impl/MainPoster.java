package com.yize.speaker.poster.impl;

import android.os.Looper;

import com.yize.speaker.Topic;
import com.yize.speaker.poster.Poster;

public class MainPoster implements Poster {
    private volatile HandlerPoster handlerPoster;
    public MainPoster() {
    }

    @Override
    public void commit(Topic topic, Object msg) {
        if(handlerPoster==null){
            handlerPoster = new HandlerPoster(Looper.getMainLooper());
        }
        handlerPoster.commit(topic,msg);
    }
}
