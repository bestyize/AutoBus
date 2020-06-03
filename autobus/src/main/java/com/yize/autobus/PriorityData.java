package com.yize.autobus;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * 支持数据的不同优先级
 */
public interface PriorityData  {
    /**
     * 获取优先级
     * @return
     */
    int getPriority();
}
