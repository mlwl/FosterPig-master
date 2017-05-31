package com.minlu.fosterpig.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.customview.MyMediaPlayer;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.observer.MySubject;
import com.minlu.fosterpig.observer.Observers;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AlarmServicer extends Service implements Observers {

    private NotificationManager mNotificationManager;
    private MyMediaPlayer myMediaPlayer;
    private Timer timer;
    private TimerTask timerTask;
    private boolean isAlarm;
    private Builder mBuilder;
    private String postBack;
    private int isStart = 0;
    private String msg;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MySubject.getInstance().add(this);

        // 通知栏准备
        initNotification();
        // 多媒体播放准备
        prepareMediaPlayer();

        // 一开服务就允许声音播放
        SharedPreferencesUtil.saveboolean(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY, true);
        // 一开服务的系统时间
        SharedPreferencesUtil.saveLong(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY_TIME, System.currentTimeMillis());

        // 0秒后开启定时器，定时循环间隔60秒
        timer = new Timer();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
                RequestBody formBody = new FormBody.Builder().build();

                String ipAddress = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");

                Request request = new Request.Builder()
                        .url(ipAddress + IpFiled.MAIN_GET_ALL_INFORMATION)
                        .post(formBody)
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        postBack = response.body().string();
                        Log.i("okHttp_SUCCESS", postBack);
                        JSONObject jsonObject = new JSONObject(postBack);
                        JSONObject object = jsonObject.optJSONObject("mapList");
                        if (object.has("allWranNumber")) {
                            int allWarnNumber = object.optInt("allWranNumber");
                            // TODO 测试数据
//                            allWarnNumber = 2;
                            // TODO 测试数据
                            if (allWarnNumber > 0) {
                                isAlarm = true;
                                msg = "请注意!现有" + allWarnNumber + "报警,触摸可显示具体报警信息";
                            } else {
                                isAlarm = false;
                                msg = "暂无报警信息";
                            }
                        } else {
                            isAlarm = false;
                            msg = "报警信息,请求失败!";
                        }
                    }else{
                        System.out.println("=========================onFailure=============================");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("=========================onFailure=============================");
                    Log.i("okHttp_ERROE", "okHttp is request error");
                    isAlarm = false;
                    msg = "报警信息,请求失败!";
                }

                if (!SharedPreferencesUtil.getboolean(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY, false)) {
                    Log.v("alarm", "此时不允许报警播放，所以接下来判断时间间隔");
                    long timeInterval = SharedPreferencesUtil.getLong(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY_TIME, -1) - System.currentTimeMillis();
                    Log.v("alarm", "时间间隔为: " + timeInterval);
                    if (timeInterval < 0) {// 如果时间间隔小于0，说明暂停报警声音时间超过，可重新报警
                        Log.v("alarm", "由于时间间隔小于0，所以重新允许报警播放");
                        SharedPreferencesUtil.saveboolean(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY, true);
                    }
                }

                if (SharedPreferencesUtil.getboolean(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY, false)) {
                    Log.v("alarm", "允许报警，开始进行是否开启声音判断");
                    if (isAlarm) {
                        isStart++;
                        myMediaPlayer.start();
                        Log.v("alarm", "开启声音报警");
                    } else {
                        if (isStart != 0) {
                            myMediaPlayer.pause();
                            Log.v("alarm", "暂停声音报警");
                            isStart = 0;
                        }
                    }
                } else {
                    msg = "暂无系统报警";
                }
                mBuilder.setContentText(msg);
                startNotification();
            }
        };
        timer.schedule(timerTask, 0, 15000);

        return super.onStartCommand(intent, flags, startId);
    }

    private void prepareMediaPlayer() {
        AssetManager am = getAssets();// 获得该应用的AssetManager
        try {
            AssetFileDescriptor afd = am.openFd("alarm.mp3");
            myMediaPlayer = new MyMediaPlayer();
            MySubject.getInstance().add(myMediaPlayer);
            myMediaPlayer.setDataSource(afd.getFileDescriptor());
            myMediaPlayer.prepare(); // 准备
            myMediaPlayer.setLooping(true);
            myMediaPlayer.isPlaying();
            // mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            // @Override
            // public void onCompletion(MediaPlayer mp) {
            // mediaPlayer.start();
            // // mediaPlayer.setLooping(true);
            // }
            // });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new Builder(this);
        // //PendingIntent 跳转动作
        Intent intent2 = new Intent();
        intent2.setAction("com.alarm.receiver");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker("系统警报")
                .setContentTitle("系统警报")
                .setContentText("当报警声响起，请点击通知栏进入设置页面关闭")
                .setContentIntent(pendingIntent);
    }

    private void startNotification() {
        Notification mNotification = mBuilder.build();
        // 设置通知 消息 图标
        mNotification.icon = R.mipmap.ic_launcher;
        // 在通知栏上点击此通知后自动清除此通知
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;// FLAG_ONGOING_EVENT
        // 在顶部常驻，可以调用下面的清除方法去除
        // FLAG_AUTO_CANCEL
        // 点击和清理可以去调
        // 设置显示通知时的默认的发声、震动、Light效果
        // mNotification.defaults = Notification.DEFAULT_VIBRATE;
        // 设置发出消息的内容
        mNotification.tickerText = "系统报警";
        // 设置发出通知的时间
        mNotification.when = System.currentTimeMillis();
        // mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        // //在通知栏上点击此通知后自动清除此通知
        // mNotification.setLatestEventInfo(this, "常驻测试",
        // "使用cancel()方法才可以把我去掉哦", null); //设置详细的信息 ,这个方法现在已经不用了
        mNotificationManager.notify(100, mNotification);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {

        System.out.println("服务结束了");
        myMediaPlayer.stop();
        myMediaPlayer.release();
        MySubject.getInstance().del(myMediaPlayer);
        MySubject.getInstance().del(this);
        if (timer != null)
            timer.cancel();
        timer = null;
        timerTask = null;

        if (mNotificationManager != null) {
            mNotificationManager.cancel(100);
        }

        super.onDestroy();
    }

    @Override
    public void update(int distinguishNotified, int position, int cancelOrderBid) {
        if (distinguishNotified == StringsFiled.OBSERVER_UPDATE_NOTIFICATION) {
            mBuilder.setContentText("暂无系统报警");
            startNotification();
        }
    }
}
