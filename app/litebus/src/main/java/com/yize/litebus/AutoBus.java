package com.yize.litebus;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.yize.litebus.life.AutoLifeListener;
import com.yize.litebus.life.AutoLifeManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class AutoBus {
    private volatile static AutoBus AUTO_LIFE_INSTANCE;
    private AutoLifeManager autoLifeManager;
    public static AutoBus getAutoLifeBus(){
        if(AUTO_LIFE_INSTANCE==null){
            synchronized (LiteBus.class){
                if(AUTO_LIFE_INSTANCE==null){
                    AUTO_LIFE_INSTANCE=new AutoBus();
                }
            }
        }
        return AUTO_LIFE_INSTANCE;
    }

    private ExecutorService executorService;
    private static final int DEFAULT_CORE_POOL_SIZE=4;
    private static final int MAX_POOL_SIZE=16;

    private AutoBus() {
        this(new AutoBusBuilder());
        autoLifeManager=AutoLifeManager.getDefaultManager();
        executorService=new ThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE
                ,MAX_POOL_SIZE
                ,5
                , TimeUnit.SECONDS
                ,new ArrayBlockingQueue<Runnable>(MAX_POOL_SIZE)
                ,new ConcurrentThreadFactory()
                ,new ExceedHandler());
        mainThreadHandler=new MainThreadHandler();

    }

    /**
     * 线程工厂
     */
    private class ConcurrentThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    }

    /**
     * 饱和策略
     */
    private class ExceedHandler implements RejectedExecutionHandler {

        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println("超过最大线程数");
        }
    }

    private static AutoBusBuilder Builder(){
        return autoBusBuilder;
    }

    private static AutoBusBuilder autoBusBuilder;

    public AutoBus(AutoBusBuilder autoBusBuilder){
        this.autoBusBuilder=autoBusBuilder;
    }

//    public static void main(String[] args) {
//        AutoBus autoBus=AutoBus.Builder().with(new Activity()).load("https://www.baidu.com").addListener(new AutoBusListener() {
//            @Override
//            public void onSuccess(String response) {
//
//            }
//
//            @Override
//            public void onFailed(String reason) {
//
//            }
//        }).build();
//    }


    private interface AutoBusListener {
        void onSuccess(String response);
        void onFailed(String reason);
    }

    private static final int SUCCESS=0;
    private static final int FAILED=-1;

    class AsynsWebRequestListener implements AutoBusListener{

        @Override
        public void onSuccess(String response) {
            Message msg=new Message();
            msg.what=SUCCESS;
            msg.obj=response;
            mainThreadHandler.sendMessage(msg);
        }

        @Override
        public void onFailed(String reason) {
            Message msg=new Message();
            msg.what=FAILED;
            msg.obj=reason;
            mainThreadHandler.sendMessage(msg);
        }
    }

    /**
     * 做出请求
     */
    public void doRequest(){
        if(autoBusBuilder==null){
            throw new NullPointerException("未构建！");
        }
        autoLifeManager.bindLifeCycle(autoBusBuilder.activity, new AutoLifeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {
                isStop=true;
            }

            @Override
            public void onDestroy() {
                isDestory=true;
            }
        });
        executorService.submit(new AsyncWebRequestRunnable(autoBusBuilder.link,autoBusBuilder.headers,new AsynsWebRequestListener()));
    }

    public boolean isDestory;
    public boolean isStop;

    static class MainThreadHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    //ToDo
                    String response=(String) msg.obj;
                    break;
                case FAILED:
                    //ToDo
                    String reason=(String) msg.obj;
                    break;
                default:
                    break;
            }
        }
    }

    private MainThreadHandler mainThreadHandler;


    class AsyncWebRequestRunnable implements Runnable{
        private String link;
        private Map<String,String> headers;
        private AutoBusListener listener;

        public AsyncWebRequestRunnable(String link, Map<String, String> headers,AutoBusListener autoBusListener) {
            this.link = link;
            this.headers = headers;
            this.listener=autoBusListener;
        }

        @Override
        public void run() {
            try {
                URL url=new URL(link);
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(10000);
                if(headers!=null){
                    for (String key:headers.keySet()){
                        conn.setRequestProperty(key,headers.get(key));
                    }
                }
                BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder sb=new StringBuilder();
                while((line=reader.readLine())!=null){
                    if(isDestory||isStop){
                        reader.close();
                        conn.disconnect();
                        listener.onFailed("组件已销毁或停止");
                        return;
                    }
                    sb.append(line);
                }
                reader.close();
                conn.disconnect();
                listener.onSuccess(sb.toString());
            } catch (IOException e) {
                listener.onFailed(e.toString());
            }
        }
    }

}
