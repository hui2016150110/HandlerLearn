package com.example.hui.handlerlearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    private MyBroadCastReceiver myBroadCastReceiver;
    private final int CHANGEUI = 1;
    private final int GETRANDOM = 2;
    private Button sendButton;
    private Button receiveButton;
    private ProgressBar progressBar;
    private Button task1,task2,change,stop;
    private TextView getRandom;
    private HandlerThread handlerThread;
    private Handler subHandler;
    Random random = new Random();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CHANGEUI:
                    receiveButton.setText("已经收到消息，并改变UI");
                    Log.i("MainActivity",System.currentTimeMillis()+"");
                    break;
                case GETRANDOM:
                    getRandom.setText(msg.obj.toString());
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        sendButton = findViewById(R.id.send);
        receiveButton = findViewById(R.id.receive);
        sendButton.setOnClickListener(this);
        getRandom = findViewById(R.id.random);
        change = findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(handlerThread!=null){
                    Message message = new Message();
                    message.what = CHANGEUI;
                    subHandler.sendMessage(message);
                }
            }
        });

        handlerThread = new HandlerThread("myThread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        subHandler = new Handler(looper){
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG,"收到改变UI的消息,但是这是子线程，无法改变，需要交给另一个handler处理");
                Message message = new Message();
                message.what = GETRANDOM;
                message.obj = random.nextInt();
                handler.sendMessage(message);
                super.handleMessage(msg);
            }
        };

        task1 = findViewById(R.id.task1);
        task1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MyIntentService.class);
                intent.setAction("down.image");
                startService(intent);
            }
        });

        task2 = findViewById(R.id.task2);
        task2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MyIntentService.class);
                intent.setAction("down.vid");
                startService(intent);
            }
        });

        stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this,MyIntentService.class));
            }
        });

        progressBar = findViewById(R.id.progressBar);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.hui.handlerlearn.mybroadcast");
        //通过代码动态注册广播
        myBroadCastReceiver = new MyBroadCastReceiver();
        registerReceiver(myBroadCastReceiver,intentFilter);//动态注册的广播之后记得取消注册

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                sendMessageOnChildThread();
                break;

        }
    }

    //在子线程中发送消息，然后在主线程中改变receiveButton中的值
    private void sendMessageOnChildThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message= new Message();
                message.what = 1;
                message.obj = "主线程需要修改receiveButton里面的内容";
                //延迟两秒发送
                handler.sendMessageDelayed(message,2000);
                Log.i("MainActivity",System.currentTimeMillis()+"");
            }
        }).start();
    }

    private void postMessageOnChildThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        receiveButton.setText("已经收到消息，并改变UI");
                    }
                },2000);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myBroadCastReceiver);
        handlerThread.quit();
        super.onDestroy();
    }

    class MyBroadCastReceiver extends BroadcastReceiver {
        //不能执行耗时操作
        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getStringExtra("serviceState");
            if(info!=null)
                Log.i("IntentService",info);
            int progress = intent.getIntExtra("updateProgress",0);
            if(progress!=0)
                progressBar.setProgress(progress);

        }
    }
}
