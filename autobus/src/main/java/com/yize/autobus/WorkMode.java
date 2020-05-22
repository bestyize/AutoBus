package com.yize.autobus;
/**
 * 方法作用线程,工作所在线程
 */

public enum WorkMode {
    //发布到主线程，此类一般用于更新UI
    THREAD_MAIN,
    //在本线程发布，要注意最终函数那里不能做更新UI操作
    THREAD_SYNC,
    //在专门的子线程里面发布，可以用来做一些日志
    THREAD_ASYNC
}
