package com.example.hui.handlerlearn;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class MyIntentService extends IntentService {
    private final static String TAG = "MyIntentService";

    public static final String ACTION_DOWN_IMG = "down.image";
    public static final String ACTION_DOWN_VID = "down.vid";
    public static final String ACTION_DOWN_PROGRESS = "com.example.hui.handlerlearn.mybroadcast";
    public static final String PROGRESS = "updateProgress";
    public static final String SERVICE_STATE = "serviceState";

    //构造方法 一定要实现此方法否则Service运行出错。
    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        sendServiceState("onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG, "");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent thread:"+Thread.currentThread());
        String action = intent.getAction();
        if(action.equals(ACTION_DOWN_IMG)){
            for(int i = 0; i < 100; i++){
                try{ //模拟耗时操作
                    Thread.sleep(50);
                }catch (Exception e) {
                }
                sendProgress(i);
            }
        }else if(action.equals(ACTION_DOWN_VID)){
            for(int i = 0; i < 100; i++){
                try{ //模拟耗时操作
                    Thread.sleep(70);
                }catch (Exception e) {
                }
                sendProgress(i);
            }
        }
        Log.i(TAG, "onHandleIntent end");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        sendServiceState("onDestroy");
    }

    //发送Service的状态
    private void sendServiceState(String state){
        Intent intent = new Intent();
        intent.setAction(ACTION_DOWN_PROGRESS);
        intent.putExtra(SERVICE_STATE, state);
        sendBroadcast(intent);
    }

    //发送进度
    private void sendProgress(int progress){
        Intent intent = new Intent();
        intent.setAction(ACTION_DOWN_PROGRESS);
        intent.putExtra(PROGRESS, progress);
        sendBroadcast(intent);
    }
}
