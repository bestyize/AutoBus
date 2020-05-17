package com.yize.autobus.pubish;

import com.yize.autobus.Subscription;

public interface Publisher {
    void enqueue(Subscription subscription, Object data);
}
