package com.example.hui.handlerlearn;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

public class MainActivity extends AppCompatActivity{

    private final String TAG = "MainActivity";
    private MyBroadCastReceiver myBroadCastReceiver;
    private final int CHANGEUI = 1;
    private final int GETRANDOM = 2;
    @BindView(R2.id.send)
    protected Button sendButton;
    @BindView(R2.id.receive)
    protected Button receiveButton;
    @BindView(R2.id.progressBar)
    protected ProgressBar progressBar;
    @BindView(R2.id.task1)
    protected Button task1;
    @BindView(R2.id.task2)
    protected Button task2;
    @BindView(R2.id.change)
    protected Button change;
    @BindView(R2.id.stop)
    protected Button stop;
    @BindView(R2.id.random)
    protected TextView getRandom;

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
        ButterKnife.bind(this);
        init();
    }

    private void init(){

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

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.hui.handlerlearn.mybroadcast");
        //通过代码动态注册广播
        myBroadCastReceiver = new MyBroadCastReceiver();
        registerReceiver(myBroadCastReceiver,intentFilter);//动态注册的广播之后记得取消注册

    }



    //在子线程中发送消息，然后在主线程中改变receiveButton中的值
    @OnClick(R.id.send)
    protected void sendMessageOnChildThread(){
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

    @OnClick(R.id.change)
    protected void change(){
        if(handlerThread!=null){
            Message message = new Message();
            message.what = CHANGEUI;
            subHandler.sendMessage(message);
        }
    }

    @OnClick(R.id.task1)
    protected void startTask1(){
        Intent intent = new Intent(MainActivity.this,MyIntentService.class);
        intent.setAction("down.image");
        startService(intent);
    }

    @OnClick(R.id.task2)
    protected void startTask2(){
        Intent intent = new Intent(MainActivity.this,MyIntentService.class);
        intent.setAction("down.vid");
        startService(intent);
    }

    @OnClick(R.id.stop)
    protected void stopTask(){
        stopService(new Intent(MainActivity.this,MyIntentService.class));
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
