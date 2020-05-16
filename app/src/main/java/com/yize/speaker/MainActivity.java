package com.yize.speaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.yize.autobus.LiteBus;
//import com.yize.litebus.Subscribe;
//import com.yize.litebus.WorkMode;
import com.yize.speaker.databinding.ActivityMainBinding;


import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    ActivityMainBinding vb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        LiteBus.defaultBus().register(this);
        super.onCreate(savedInstanceState);
        vb=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());
        vb.btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object obj=new Object();
                System.out.println(obj);
//                LiteBus.defaultBus().publish(new MyMessage("同步消息"));
            }
        });
        vb.btnSendAsyncMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int count=20;
                        while (count-->0){
                            try {
//                                LiteBus.defaultBus().publish(new MyMessage("异步消息"));
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }).start();
            }
        });
        vb.btnOpenSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,TestActivity.class);
                startActivity(intent);
            }
        });


    }
//    @Subscribe(workMode = WorkMode.THREAD_MAIN)
//    public void finder(MyMessage obj){
//        Log.i(TAG,obj.msg);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        LiteBus.defaultBus().unregister(this);
    }
}
