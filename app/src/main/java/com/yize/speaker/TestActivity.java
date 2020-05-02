package com.yize.speaker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.yize.litebus.LiteBus;
import com.yize.litebus.Subscribe;
import com.yize.litebus.WorkMode;
import com.yize.speaker.databinding.ActivityTestBinding;

public class TestActivity extends AppCompatActivity {
    private static final String TAG="TestActivity";
    private ActivityTestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LiteBus.defaultBus().register(this);
        binding=ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
    @Subscribe(workMode = WorkMode.THREAD_MAIN)
    public void receiver(MyMessage message){
        Log.i(TAG,message.msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiteBus.defaultBus().unregister(this);
    }
}
