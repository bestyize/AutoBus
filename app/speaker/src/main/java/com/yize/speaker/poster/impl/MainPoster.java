package com.yize.speaker.poster.impl;

import android.os.Looper;

import com.yize.speaker.poster.Poster;

public class MainPoster implements Poster {
    final Looper looper;
    public MainPoster() {
        looper=Looper.getMainLooper();
    }

    @Override
    public void commit() {

    }
}
