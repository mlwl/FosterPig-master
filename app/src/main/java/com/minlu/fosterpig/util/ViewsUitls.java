package com.minlu.fosterpig.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.view.View;

import com.minlu.fosterpig.base.MyApplication;

import java.util.List;

public class ViewsUitls {

    public static Context getContext() {
        return MyApplication.getContext();
    }


    /**
     * dip转px
     */
    public static int dptopx(int dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        // px = dip * density
        // 3.3 3.8 3
        // 3.6 4.1 4
        return (int) (dip * density + 0.5);
    }

    /**
     * xml 转成View对象
     *
     * @param id
     * @return
     */
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    /**
     * 在主线程中执行任务 模仿runOut。。。。
     *
     * @param task
     */
    public static void runInMainThread(Runnable task) {
        if (MyApplication.getMainThreadId() == android.os.Process.myTid()) {
            // 当前就是主线程，直接执行task
            task.run();
        } else {
            // 在子线程，post给主线程
            MyApplication.getHanlder().post(task);
        }
    }

    /*
    * 当前线程是否在子线程
    * */
    public static boolean isAtMainThread() {
        return MyApplication.getMainThreadId() == android.os.Process.myTid();
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(String serviceName) {
        boolean isWork = false;
        ActivityManager systemService = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = systemService.getRunningServices(40);
        if (runningServices.size() <= 0) {
            return false;
        }
        for (int i = 0; i < runningServices.size(); i++) {
            String setviceName = runningServices.get(i).service.getClassName().toString();
            if (setviceName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }


    /*
    * 返回true:  应用所在系统的Api Level大于等于参数传递过来的Api Level
    * 返回false: 应用所在系统的Api Level小于参数传递过来的Api Level
    * */
    public static boolean systemSdkVersionIsBigThanParameter(int possibleSystemSdkVersion) {
        // Build.VERSION_CODES.BASE; 这个是SDK中存储的各个版本对应的Api Level
        // Build.VERSION.SDK_INT 获取的是应用所在系统的Api Level
        if (Build.VERSION.SDK_INT >= possibleSystemSdkVersion) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取登录设备mac地址
     */
    public static String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String mac = wifiManager.getConnectionInfo().getMacAddress();
        return mac == null ? "" : mac;
    }

}
