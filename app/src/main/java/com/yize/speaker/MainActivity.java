package com.yize.speaker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.yize.speaker.databinding.ActivityMainBinding;
import com.yize.speaker.listener.Listen;
import com.yize.speaker.listener.ListenMode;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding vb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Speaker.defaultSpeaker().register(this);
        super.onCreate(savedInstanceState);
        vb=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());
        vb.btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object obj=new Object();
                System.out.println(obj);
                Speaker.defaultSpeaker().speakToAll(obj);
            }
        });

    }
    @Listen(listenMode = ListenMode.MAIN_THREAD)
    public void finder(Object obj){
        System.out.println("finder:"+obj);
    }
//    class MyMessage{
//        public String msg;
//        public MyMessage(String msg){
//            this.msg=msg;
//        }
//    }
}
