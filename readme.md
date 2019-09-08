### HandlerThread<br>
HandlerThread是Android API提供的一个方便、便捷的类，使用它我们可以快速的创建一个带有Looper的线程。Looper可以用来创建Handler实例。它继承Thread，外界需要通过Handler的消息方法来通知HandlerThread执行一个具体任务。<br>
使用步骤<br>
1.创建HandlerThread线程<br>
2.运行线程<br>
3.获取HandlerThread线程中的Looper实例<br>
4.通过Looper实例创建Handler实例，从而使mSubThreadHandler与该线程连接到一起。<br>


看一下HandlerThread的run方法
```Java
@Override
public void run() {
    mTid = Process.myTid();
    Looper.prepare();//创建一个Looper
    synchronized (this) {
        mLooper = Looper.myLooper();
        notifyAll();
    }
    Process.setThreadPriority(mPriority);
    onLooperPrepared();
    Looper.loop();//开启这个Looper
    mTid = -1;
}
//其实HanderThread就是一个帮我们封装了Looper的Thread，然后通过这个Looper可以创建Handler
```
### IntentService<br>
IntentService是Service的子类，根据需要处理异步请求（以intent表示）。客户端通过调用startService(Intent) 发送请求，该Service根据需要启动，使用工作线程处理依次每个Intent，并在停止工作时停止自身。 <br>
这种“工作队列处理器”模式通常用于从应用程序的主线程中卸载任务。 IntentService类的存在是为了简化这种模式。 要使用它，扩展IntentService并实现onHandleIntent（Intent）。 IntentService将收到Intents，启动一个工作线程，并根据需要停止该服务。<br> 
**所有请求都在单个工作线程处理 - 它们可能需要很长的时间（并且不会阻止应用程序的主循环），但是一次只会处理一个请求。如果有多个请求，那么其他的任务会阻塞，并且只有当所有的任务执行完毕的时候，才会停止。**<br>
**如果在一个任务没有执行完成，调用停止这个服务，那么会服务会毁掉，但是这个执行这个任务的线程会执行完成之后才退出**

    特点：
    1.一个封装了HandlerThread和Handler的异步框架。
    2.是一种特殊Service，继承自Service，是抽象类,必须创建子类才可以使用。
    3.可用于执行后台耗时的任务，任务执行后会自动停止。
    4.具有高优先级(服务的原因),优先级比单纯的线程高很多，适合高优先级的后台任务，且不容易被系统杀死。
    5.启动方式和Service一样。
    6.可以多次启动，每个耗时操作都会以工作队列的方式在IntentService的onHandleIntent回调方法中执行。
    7.串行执行。
    8.在子线程执行，而service默认是主线程执行。

使用IntentService的方法：<br>
1.继承IntentService。<br>
2.实现不带参数的构造方法，并且调用父类IntentService的构造方法。<br>
3.实现onHandleIntent方法。<br>

