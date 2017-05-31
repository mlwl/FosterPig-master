package com.minlu.fosterpig.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.sdk.VMSNetSDK;
import com.minlu.fosterpig.activity.AlarmServicer;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static Context mContext;
    private static long mainThreadId;
    private static MyApplication ins;
    private static Handler mHandler;
    private static Intent intentServicer;
    private static List<Activity> saveActivity = new ArrayList<>();

    @Override
    public void onCreate() {
        // 在onCreate方法中初始化公共变量，Context，Handler，main Thread id
        super.onCreate();

        mContext = getApplicationContext();
        mainThreadId = android.os.Process.myTid();
        ins = this;
        mHandler = new Handler();

        // 将这个加入通知
        intentServicer = new Intent(mContext, AlarmServicer.class);

        // 视频初始化
        MCRSDK.init();
        RtspClient.initLib();
        MCRSDK.setPrint(1, null);
        VMSNetSDK.init(this);

    }

    public static Intent getIntentServicer() {
        return intentServicer;
    }

    public static MyApplication getIns() {
        return ins;
    }

    // 返回上下文
    public static Context getContext() {
        return mContext;
    }

    // 返回主线程的id
    public static long getMainThreadId() {
        return mainThreadId;
    }

    // 返回主线程的Handler
    public static Handler getHanlder() {
        return mHandler;
    }

    public static List<Activity> getSaveActivity() {
        return saveActivity;
    }
}
