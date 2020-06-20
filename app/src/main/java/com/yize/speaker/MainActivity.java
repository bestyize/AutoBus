package com.yize.speaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.yize.autobus.LiteBus;
import com.yize.autobus.Subscribe;
import com.yize.autobus.WorkMode;
import com.yize.speaker.databinding.ActivityMainBinding;



public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
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
                        int count=20;
                        while (count-->0){
                            try {

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
        vb.btnSendPeriodMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LiteBus.defaultBus().publish(new MyMessage("定时消息"), 10, 1000);
                    }
                }).start();
                LiteBus.defaultBus().publish(new MyMessage("延时消息"),1000);
            }
        });


    }
    @Subscribe(workMode = WorkMode.THREAD_MAIN)
    public void finder(MyMessage obj){
        Log.i(TAG,obj.msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiteBus.defaultBus().unregister(this);
    }
}
