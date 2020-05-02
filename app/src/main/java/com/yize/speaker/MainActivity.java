package com.yize.speaker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.yize.litebus.LiteBus;
import com.yize.litebus.Subscribe;
import com.yize.litebus.WorkMode;
import com.yize.speaker.databinding.ActivityMainBinding;
import com.yize.speaker.listener.Listen;
import com.yize.speaker.listener.ListenMode;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding vb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LiteBus.defaultBus().register(this);
        super.onCreate(savedInstanceState);
        vb=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());
        vb.btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object obj=new Object();
                System.out.println(obj);
                LiteBus.defaultBus().publish(new MyMessage("同步消息"));
            }
        });
        vb.btnSendAsyncMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LiteBus.defaultBus().publish(new MyMessage("异步消息"));
                    }
                }).start();
            }
        });

    }
    @Subscribe(workMode = WorkMode.THREAD_MAIN)
    public void finder(MyMessage obj){
        System.out.println("finder:"+obj.msg);
    }
    class MyMessage {
        public String msg;
        public MyMessage(String msg){
            this.msg=msg;
        }
    }
}
