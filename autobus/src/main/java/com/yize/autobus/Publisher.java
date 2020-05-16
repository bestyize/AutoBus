package com.yize.autobus;

public interface Publisher {
    void enqueue(Subscription subscription, Object data);
}
