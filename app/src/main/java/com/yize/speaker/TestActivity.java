package com.yize.speaker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.yize.autobus.AutoBus;
import com.yize.autobus.Subscribe;
import com.yize.autobus.WorkMode;
import com.yize.speaker.databinding.ActivityTestBinding;

public class TestActivity extends AppCompatActivity {
    private static final String TAG="TestActivity";
    private ActivityTestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int i=20;
                        while (i-->0){
                            try {
                                Thread.sleep(2000);
                                AutoBus.with(TestActivity.this).post(new MyMessage("AutoBus发出的消息"));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }).start();
            }
        });
    }
    @Subscribe(workMode = WorkMode.THREAD_MAIN)
    public void receiver(MyMessage message){
        Log.i(TAG,message.msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
