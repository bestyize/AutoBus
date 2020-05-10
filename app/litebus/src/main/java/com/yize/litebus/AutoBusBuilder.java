package com.yize.litebus;

import android.app.Activity;

import java.util.Map;

public class AutoBusBuilder{
    public Activity activity;
    public String link;
    public Map<String,String> headers;
    public AutoBusListener listener;

    public AutoBusBuilder(Activity activity, String link, Map<String, String> headers) {
        this.activity = activity;
        this.link = link;
        this.headers = headers;
    }

    public AutoBusBuilder() {
    }

    public AutoBusBuilder with(Activity activity){
        this.activity=activity;
        return this;
    }

    public AutoBusBuilder load(String link){
        this.link=link;
        return this;
    }

    public AutoBusBuilder addListener(AutoBusListener listener){
        this.listener=listener;
        return this;
    }

    public AutoBus build(){
        return new AutoBus(this);
    }
}