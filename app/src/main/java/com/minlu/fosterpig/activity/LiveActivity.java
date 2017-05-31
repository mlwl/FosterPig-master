package com.minlu.fosterpig.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.ConstantLiveSDK;
import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.DeviceInfo;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.haikang.LiveControl;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.manager.ThreadManager;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.ToastUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by user on 2017/1/18.
 */
public class LiveActivity extends Activity implements View.OnClickListener {


    private LinearLayout mLoad;
    private LinearLayout mError;
    private SurfaceView mSurfaceView;
    private LinearLayout mTrueTimeData;
    private int mTrueTimeDataWidth = -1;
    private TranslateAnimation mShiftOut;
    private TranslateAnimation mEnterInto;
    private TextView mAmmoniaData;
    private TextView mTemperatureData;
    private TextView mHumidityData;
    private TextView mPowerSupplyData1;
    private TextView mPowerSupplyData2;
    private TextView mPowerSupplyData3;
    private TextView mPowerSupplyData4;
    private TextView mPowerSupplyData5;
    private TextView mPowerSupplyData6;
    private TextView mPowerSupplyData7;
    private TextView mPowerSupplyData8;
    private boolean isCanShowTrueTimeData = false;
    private Timer keepTimeTimer;
    private TimerTask keepTimeTimerTask;
    private boolean isAlreadyShowTrueTimeData = true;
    private int keepTime = -1;
    private Timer mGetHttpData;
    private TimerTask mGetHttpDataTask;
    private Call callTrueTime;
    private boolean animationIsCompletes = true;
    private Camera camera;
    private VMSNetSDK mVMSNetSDK;
    private MyHandler mHandler;
    private LiveControl mLiveControl;
    private CameraInfo cameraInfo;
    private DeviceInfo deviceInfo;

    private int whichError = -1;
    private final int cameraError = 1;
    private final int deviceError = 2;
    private final int playError = 3;
    private String deviceNumber;

    static class MyHandler extends Handler {
        WeakReference<LiveActivity> mActivity;

        MyHandler(LiveActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final LiveActivity liveActivity = mActivity.get();
            switch (msg.what) {
                case StringsFiled.GET_CAMERA_INFO:
                    System.out.println("准备请求Camera信息");
                    break;
                case StringsFiled.GET_CAMERA_INFO_SUCCESS:
                    System.out.println("请求Camera信息成功");
                    liveActivity.getDeviceInfo();
                    break;
                case StringsFiled.GET_CAMERA_INFO_FAILURE:
                    System.out.println("请求Camera信息失败");
                    liveActivity.goneLoad();
                    liveActivity.whichError = liveActivity.cameraError;
                    break;
                case StringsFiled.GET_DEVICE_INFO:
                    System.out.println("准备请求DEVICE设备信息");
                    break;
                case StringsFiled.GET_DEVICE_INFO_SUCCESS:
                    System.out.println("请求DEVICE设备信息成功");
                    final String username = liveActivity.deviceInfo.getUserName();
                    final String password = liveActivity.deviceInfo.getPassword();
                    ThreadManager.getInstance().execute(new TimerTask() {
                        @Override
                        public void run() {
                            liveActivity.startPlay(username, password);
                        }
                    });
                    break;
                case StringsFiled.GET_DEVICE_INFO_FAILURE:
                    System.out.println("请求DEVICE设备信息失败");
                    liveActivity.goneLoad();
                    liveActivity.whichError = liveActivity.deviceError;
                    break;


                // 视频控制层回调的消息
                case ConstantLiveSDK.RTSP_FAIL:
                    ToastUtil.showToast(liveActivity, "RTSP链接失败");
                    if (null != liveActivity.mLiveControl) {
                        ThreadManager.getInstance().execute(new TimerTask() {
                            @Override
                            public void run() {
                                liveActivity.mLiveControl.stop();
                            }
                        });
                    }
                    liveActivity.goneLoad();
                    liveActivity.whichError = liveActivity.playError;
                    break;
                case ConstantLiveSDK.RTSP_SUCCESS:
                    System.out.println("启动取流成功");
                    break;
                case ConstantLiveSDK.STOP_SUCCESS:
                    System.out.println("停止成功");
                    if (liveActivity.mError != null && liveActivity.mLoad != null) {
                        liveActivity.goneLoad();
                        liveActivity.whichError = liveActivity.playError;
                    }
                    break;
                case ConstantLiveSDK.START_OPEN_FAILED:
                    ToastUtil.showToast(liveActivity, "开启播放库失败");
                    liveActivity.goneLoad();
                    liveActivity.whichError = liveActivity.playError;
                    break;
                case ConstantLiveSDK.PLAY_DISPLAY_SUCCESS:
                    System.out.println("播放成功");
                    liveActivity.goneAll();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_screen);

        initData();
        initViews();

        getCameraInfo();
    }

    private void initData() {
        Intent intent = getIntent();
        camera = (Camera) intent.getSerializableExtra(StringsFiled.CAMERA_INFORMATION);
        mVMSNetSDK = VMSNetSDK.getInstance();
        mHandler = new MyHandler(this);

        mLiveControl = new LiveControl();
        mLiveControl.setLiveCallBack(new LiveControl.LiveCallBack() {
            @Override
            public void onMessageCallback(int message) {
                if (null != mHandler) {
                    // 发送消息
                    mHandler.sendEmptyMessage(message);
                }
            }
        });
    }


    private void initViews() {
        mLoad = (LinearLayout) findViewById(R.id.ll_loading);
        mError = (LinearLayout) findViewById(R.id.ll_error);
        mSurfaceView = (SurfaceView) findViewById(R.id.sv_player);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (null != mLiveControl) {
                    ThreadManager.getInstance().execute(new TimerTask() {
                        @Override
                        public void run() {
                            mLiveControl.stop();
                        }
                    });
                }
            }
        });
        mTrueTimeData = (LinearLayout) findViewById(R.id.ll_video_true_time_data);
        ViewTreeObserver viewTreeObserver = mTrueTimeData.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTrueTimeData.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mTrueTimeDataWidth = mTrueTimeData.getWidth();
                mShiftOut = new TranslateAnimation(0f, -mTrueTimeDataWidth - ViewsUitls.dptopx(6), 0f, 0f);
                mEnterInto = new TranslateAnimation(-mTrueTimeDataWidth - ViewsUitls.dptopx(6), 0, 0f, 0f);
            }
        });

        mAmmoniaData = (TextView) findViewById(R.id.tv_video_true_time_data_ammonia);
        mTemperatureData = (TextView) findViewById(R.id.tv_video_true_time_data_temperature);
        mHumidityData = (TextView) findViewById(R.id.tv_video_true_time_data_humidity);
        mPowerSupplyData1 = (TextView) findViewById(R.id.tv_video_true_time_data_power_supply_1);
        mPowerSupplyData2 = (TextView) findViewById(R.id.tv_video_true_time_data_power_supply_2);
        mPowerSupplyData3 = (TextView) findViewById(R.id.tv_video_true_time_data_power_supply_3);
        mPowerSupplyData4 = (TextView) findViewById(R.id.tv_video_true_time_data_power_supply_4);
        mPowerSupplyData5 = (TextView) findViewById(R.id.tv_video_true_time_data_power_supply_5);
        mPowerSupplyData6 = (TextView) findViewById(R.id.tv_video_true_time_data_power_supply_6);
        mPowerSupplyData7 = (TextView) findViewById(R.id.tv_video_true_time_data_power_supply_7);
        mPowerSupplyData8 = (TextView) findViewById(R.id.tv_video_true_time_data_power_supply_8);

        mError.setOnClickListener(this);
        mSurfaceView.setOnClickListener(this);
    }

    /**
     * 获取监控点详细信息
     */
    private void getCameraInfo() {
        if (null == camera) {
            Log.e("LiveActivity", "从视频列表中传递过来的Camera对象为null");
            goneLoad();
            return;
        }
        mHandler.sendEmptyMessage(StringsFiled.GET_CAMERA_INFO);
        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
                mHandler.sendEmptyMessage(StringsFiled.GET_CAMERA_INFO_FAILURE);
            }

            @Override
            public void loading() {
            }

            @Override
            public void onSuccess(Object data) {
                if (data instanceof CameraInfo) {
                    cameraInfo = (CameraInfo) data;
                    mHandler.sendEmptyMessage(StringsFiled.GET_CAMERA_INFO_SUCCESS);
                }
            }
        });
        mVMSNetSDK.getCameraInfo(camera);
    }

    /**
     * 获取设备信息
     */
    private void getDeviceInfo() {
        if (null == cameraInfo) {
            Log.e("LiveActivity", "getCameraInfo()方法获取的CameraInfo为null");
            goneLoad();
            return;
        }

        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
                mHandler.sendEmptyMessage(StringsFiled.GET_DEVICE_INFO_FAILURE);
            }

            @Override
            public void loading() {
            }

            @Override
            public void onSuccess(Object data) {
                if (data instanceof DeviceInfo) {
                    deviceInfo = (DeviceInfo) data;
                    mHandler.sendEmptyMessage(StringsFiled.GET_DEVICE_INFO_SUCCESS);
                }
            }

        });
        mVMSNetSDK.getDeviceInfo(cameraInfo.getDeviceID());
    }


    /**
     * 开启播放
     */
    private void startPlay(String username, String password) {
        System.out.println();
        String liveUrl = VMSNetSDK.getInstance().getPlayUrl(cameraInfo, ConstantLiveSDK.MAIN_HING_STREAM);
        mLiveControl.setLiveParams(liveUrl, null == username ? "" : username, null == password ? "" : password);
        if (LiveControl.LIVE_PLAY == mLiveControl.getLiveState()) {
            mLiveControl.stop();
        }
        if (LiveControl.LIVE_INIT == mLiveControl.getLiveState()) {
            mLiveControl.startLive(mSurfaceView);
        }
    }

    private void goneLoad() {
        ViewsUitls.runInMainThread(new Runnable() {
            @Override
            public void run() {
                mLoad.setVisibility(View.GONE);
                mError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void goneError() {
        ViewsUitls.runInMainThread(new Runnable() {
            @Override
            public void run() {
                mLoad.setVisibility(View.VISIBLE);
                mError.setVisibility(View.GONE);
            }
        });
    }

    // 调用了该方法，代表播放成功，开始请求实时数据和第一次显示实时数据板
    private void goneAll() {
        deviceNumber = deviceInfo.getIndexCode();
        ViewsUitls.runInMainThread(new Runnable() {
            @Override
            public void run() {
                mLoad.setVisibility(View.GONE);
                mError.setVisibility(View.GONE);
                isCanShowTrueTimeData = true;
                mTrueTimeData.setVisibility(View.VISIBLE);
                requestHttpGetData();
                openKeepTimeTimer();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_error:
                switch (whichError) {
                    case cameraError:
                        goneError();
                        getCameraInfo();
                        break;
                    case deviceError:
                        goneError();
                        getDeviceInfo();
                        break;
                    case playError:
                        goneError();
                        ThreadManager.getInstance().execute(new TimerTask() {
                            @Override
                            public void run() {
                                startPlay(deviceInfo.getUserName(), deviceInfo.getPassword());
                            }
                        });
                        break;
                    default:
                        ToastUtil.showToast(this, "参数不对");
                        break;
                }
                break;
            case R.id.sv_player:
                System.out.println("kjsahdfkha;lshd======================================");
                // 根据实时界面是否是在第一次播放成功后出现
                if (isCanShowTrueTimeData) {
                    // 根据实时界面是否出现来进行退回动画
                    if (mShiftOut != null && isAlreadyShowTrueTimeData && animationIsCompletes) {
                        startAnimation(mShiftOut);
                        isAlreadyShowTrueTimeData = false;
                    } else if (mEnterInto != null && !isAlreadyShowTrueTimeData && animationIsCompletes) {
                        startAnimation(mEnterInto);
                        isAlreadyShowTrueTimeData = true;
                    }
                }
                break;
        }
    }

    //****************************************************************实时数据的展示，开启是在goneAll()方法中************************************************

    /* 开启相应的动画 */
    private void startAnimation(TranslateAnimation translateAnimation) {
        translateAnimation.setDuration(1000);
        //当动画执行结束后  动画停留在结束的位置上
        translateAnimation.setFillAfter(true);
        mTrueTimeData.startAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animationIsCompletes = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationIsCompletes = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                System.out.println("onAnimationRepeat");
            }
        });
    }

    /* 定时:实时数据块的间隔显示 */
    private void openKeepTimeTimer() {
        keepTimeTimer = new Timer();
        // 实时数据已经展示，需要过一段时间后隐藏掉
        keepTimeTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (isAlreadyShowTrueTimeData) {// 实时数据已经展示，需要过一段时间后隐藏掉
                    keepTime++;
                    ViewsUitls.runInMainThread(new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("=============================================openKeepTimeTimer=================================================");
                            if (keepTime == StringsFiled.VIDEO_TRUE_TIME_DATA_KEEP_TIME && mShiftOut != null && isAlreadyShowTrueTimeData && animationIsCompletes) {
                                startAnimation(mShiftOut);
                                isAlreadyShowTrueTimeData = false;
                            }
                        }
                    });
                } else {
                    keepTime = -1;
                }
            }
        };
        keepTimeTimer.schedule(keepTimeTimerTask, 0, 1000);
    }

    /* 定时:请求网络获取实时数据 */
    private void requestHttpGetData() {
        mGetHttpData = new Timer();
        mGetHttpDataTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("==================================requestHttpGetDataTimer==================================");
                OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
                RequestBody formBody = new FormBody.Builder().add("id", deviceNumber).build();

                String ipAddress = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");

                Request request = new Request.Builder().tag("mGetHttpData")
                        .url(ipAddress + IpFiled.VIDEO_TRUE_TIME_DATA)
                        .post(formBody)
                        .build();
                callTrueTime = okHttpClient.newCall(request);
                try {
                    Response response = callTrueTime.execute();
                    if (response.isSuccessful()) {
                        System.out.println("====================================requestHttpGetData==onResponse====================================");
                        String result = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.has("result")) {
                                if (jsonObject.optString("result").contains("true")) {
                                    if (jsonObject.has("partSensor")) {
                                        JSONObject partSensor = jsonObject.optJSONObject("partSensor");

                                        final double ammoniaData = getDoubleData(partSensor, "v1");
                                        final double temperatureData = getDoubleData(partSensor, "v2");
                                        final double humidityData = getDoubleData(partSensor, "v3");
                                        final int powerSupplyData1 = getIntData(partSensor, "v4");
                                        final int powerSupplyData2 = getIntData(partSensor, "v5");
                                        final int powerSupplyData3 = getIntData(partSensor, "v6");
                                        final int powerSupplyData4 = getIntData(partSensor, "v7");
                                        final int powerSupplyData5 = getIntData(partSensor, "v8");
                                        final int powerSupplyData6 = getIntData(partSensor, "v9");
                                        final int powerSupplyData7 = getIntData(partSensor, "v10");
                                        final int powerSupplyData8 = getIntData(partSensor, "v11");
                                        ViewsUitls.runInMainThread(new TimerTask() {
                                            @Override
                                            public void run() {
                                                setTrueTimeText(ammoniaData, temperatureData, humidityData, powerSupplyData1, powerSupplyData2, powerSupplyData3, powerSupplyData4, powerSupplyData5, powerSupplyData6, powerSupplyData7, powerSupplyData8);
                                            }
                                        });
                                    } else {
                                        showError("网络异常，无法获取实时数据");
                                    }
                                } else {
                                    showError("编号错误，无法获取实时数据");
                                }
                            } else {
                                showError("网络异常，无法获取实时数据");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showError("网络异常，无法获取实时数据");
                        }
                    } else {
                        showError("网络异常，无法获取实时数据");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showError("网络异常，无法获取实时数据");
                }
            }
        };
        mGetHttpData.schedule(mGetHttpDataTask, 0, StringsFiled.VIDEO_TRUE_TIME_SHOW_INTERVAL);
    }

    /* 根据json数据是否存在来判断是否返回-1 */
    private double getDoubleData(JSONObject partSensor, String field) {
        if (partSensor.has(field)) {
            return partSensor.optDouble(field);
        } else {
            return -1;
        }
    }

    /* 根据json数据是否存在来判断是否返回-1 */
    private int getIntData(JSONObject partSensor, String field) {
        if (partSensor.has(field)) {
            return partSensor.optInt(field);
        } else {
            return -1;
        }
    }

    /*实时数据请求失败时做出的UI反应*/
    private void showError(final String toast) {
        System.out.println("====================================requestHttpGetData==onFailure====================================");
        ViewsUitls.runInMainThread(new TimerTask() {
            @Override
            public void run() {
                if (isAlreadyShowTrueTimeData) {
                    ToastUtil.showToast(ViewsUitls.getContext(), toast);
                }
            }
        });
    }

    /*根据参数设置11个TextView的显示*/
    @SuppressLint("SetTextI18n")
    private void setTrueTimeText(double ammoniaData, double temperatureData, double humidityData,
                                 int powerSupplyData1, int powerSupplyData2, int powerSupplyData3, int powerSupplyData4,
                                 int powerSupplyData5, int powerSupplyData6, int powerSupplyData7, int powerSupplyData8) {
        if (ammoniaData != -1) {
            mAmmoniaData.setText("氨气 : " + ammoniaData + "ppm");
        } else {
            mAmmoniaData.setText("氨气 : --ppm");
        }
        if (temperatureData != -1) {
            mTemperatureData.setText("温度 : " + temperatureData + "℃");
        } else {
            mTemperatureData.setText("温度 : --℃");
        }
        if (humidityData != -1) {
            mHumidityData.setText("湿度 : " + humidityData + "%");
        } else {
            mHumidityData.setText("湿度 : --%");
        }
        if (powerSupplyData1 != -1) {
            mPowerSupplyData1.setText("市电一 : " + getOpenOrClose(powerSupplyData1));
        } else {
            mPowerSupplyData1.setText("市电一 : --");
        }
        if (powerSupplyData2 != -1) {
            mPowerSupplyData2.setText("市电二 : " + getOpenOrClose(powerSupplyData2));
        } else {
            mPowerSupplyData2.setText("市电二 : --");
        }
        if (powerSupplyData3 != -1) {
            mPowerSupplyData3.setText("市电三 : " + getOpenOrClose(powerSupplyData3));
        } else {
            mPowerSupplyData3.setText("市电三 : --");
        }
        if (powerSupplyData4 != -1) {
            mPowerSupplyData4.setText("市电四 : " + getOpenOrClose(powerSupplyData4));
        } else {
            mPowerSupplyData4.setText("市电四 : --");
        }
        if (powerSupplyData5 != -1) {
            mPowerSupplyData5.setText("市电五 : " + getOpenOrClose(powerSupplyData5));
        } else {
            mPowerSupplyData5.setText("市电五 : --");
        }
        if (powerSupplyData6 != -1) {
            mPowerSupplyData6.setText("市电六 : " + getOpenOrClose(powerSupplyData6));
        } else {
            mPowerSupplyData6.setText("市电六 : --");
        }
        if (powerSupplyData7 != -1) {
            mPowerSupplyData7.setText("市电七 : " + getOpenOrClose(powerSupplyData7));
        } else {
            mPowerSupplyData7.setText("市电七 : --");
        }
        if (powerSupplyData8 != -1) {
            mPowerSupplyData8.setText("市电八 : " + getOpenOrClose(powerSupplyData8));
        } else {
            mPowerSupplyData8.setText("市电八 : --");
        }
    }

    /*根据传递过来的参数来判断返回是断还是通*/
    private String getOpenOrClose(int openOrClose) {
        if (openOrClose == 0) {
            return "断";
        } else {
            return "通";
        }
    }

    //**************************************************************************************************************************************************

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                if (keepTimeTimerTask != null)
                    keepTimeTimerTask.cancel();
                if (keepTimeTimer != null)
                    keepTimeTimer.cancel();
                keepTimeTimerTask = null;
                keepTimeTimer = null;

                if (mGetHttpDataTask != null)
                    mGetHttpDataTask.cancel();
                if (mGetHttpData != null)
                    mGetHttpData.cancel();
                mGetHttpDataTask = null;
                mGetHttpData = null;

                if (callTrueTime != null) {
                    System.out.println("是否取消了：" + callTrueTime.isCanceled());
                    callTrueTime.cancel();
                }
                if (callTrueTime != null) {
                    System.out.println("是否取消了：" + callTrueTime.isCanceled());
                }
                if (mTrueTimeData != null) {
                    mTrueTimeData.setVisibility(View.INVISIBLE);
                }

                // 延时关闭界面
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finish();
                        super.run();
                    }
                }.start();
                break;
            default:
                break;
        }
        return true;
    }
}
