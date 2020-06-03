package com.yize.speaker;

import com.yize.autobus.PriorityData;

class MyMessage  implements PriorityData {
    public String msg;
    public int priority;
    public MyMessage(String msg){
        this.msg=msg;
    }
    public MyMessage(String msg,int priority){
        this.msg=msg;
        this.priority=priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}