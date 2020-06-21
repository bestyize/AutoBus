package com.yize.speaker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yize.autobus.AutoBus;
import com.yize.autobus.LiteBus;
import com.yize.autobus.Subscribe;
import com.yize.speaker.databinding.ActivityDeepBinding;

public class DeepActivity extends AppCompatActivity {
    private static final String TAG="DeepActivity";
    private ActivityDeepBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //LiteBus.defaultBus().register(this);
        binding=ActivityDeepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LiteBus.defaultBus().publish(new MyMessage("LiteBus : DeepActivity发送的消息"),20,2000);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AutoBus.with(DeepActivity.this).publish(new MyMessage("AutoBus : DeepActivity发送的消息"),20,2000);
                    }
                }).start();


            }
        });
    }

    /**
     * 默认，主线程、中等优先级
     * @param message
     */
    @Subscribe
    public void receiver(MyMessage message){
        Log.i(TAG,message.msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //LiteBus.defaultBus().unregister(this);
    }
}