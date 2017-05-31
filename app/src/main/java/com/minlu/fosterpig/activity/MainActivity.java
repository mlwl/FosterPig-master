package com.minlu.fosterpig.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.base.MyApplication;
import com.minlu.fosterpig.bean.AlreadySureWarn;
import com.minlu.fosterpig.bean.MainAllInformation;
import com.minlu.fosterpig.customview.ColorfulRingProgressView;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.observer.MySubject;
import com.minlu.fosterpig.observer.Observers;
import com.minlu.fosterpig.util.GsonTools;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ToastUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener, Observers {

    private FrameLayout mSafeNumber;
    private ColorfulRingProgressView mRingProgressView;
    private TextView mSafeProcessResult;
    private TextView mPercent;
    private TextView mAmmoniaMonitor;
    private TextView mTemperatureMonitor;
    private TextView mHumidityMonitor;
    private TextView mPowerSupplyMonitor;
    private TextView mAmmoniaWarn;
    private TextView mTemperatureWarn;
    private TextView mHumidityWarn;
    private TextView mPowerSupplyWarn;
    private TextView mAmmoniaAlreadyWarn;
    private TextView mTemperatureAlreadyWarn;
    private TextView mHumidityAlreadyWarn;
    private TextView mPowerSupplyAlreadyWarn;
    private String mResultJSON;

    private float mAllFacilityData = 0f;
    private float mAllWarnFacilityData = 0f;
    private int mSafePercentNumber = 100;

    private int mAmmoniaAllNumber = 0;
    private int mTemperatureAllNumber = 0;
    private int mHumidityAllNumber = 0;
    private int mPowerSupplyAllNumber = 0;
    private int mAmmoniaWarnNumber = 0;
    private int mTemperatureWarnNumber = 0;
    private int mHumidityWarnNumber = 0;
    private int mPowerSupplyWarnNumber = 0;

    private int mAmmoniaAlreadyWarnNumber = 0;
    private int mTemperatureAlreadyWarnNumber = 0;
    private int mHumidityAlreadyWarnNumber = 0;
    private int mPowerSupplyAlreadyWarnNumber = 0;
    private String mAreaName;
    private String mSiteName;
    private String mFacilityName;

    private List<MainAllInformation> mAllAmmoniaWarnData = new ArrayList<>();
    private List<MainAllInformation> mAllTemperatureWarnData = new ArrayList<>();
    private List<MainAllInformation> mAllHumidityWarnData = new ArrayList<>();
    private List<MainAllInformation> mAllPowerSupplyWarnData = new ArrayList<>();

    private List<AlreadySureWarn> mAllAmmoniaAlreadyWarnData = new ArrayList<>();
    private List<AlreadySureWarn> mAllTemperatureAlreadyWarnData = new ArrayList<>();
    private List<AlreadySureWarn> mAllHumidityAlreadyWarnData = new ArrayList<>();
    private List<AlreadySureWarn> mAllPowerSupplyAlreadyWarnData = new ArrayList<>();

    /*private final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE};

    // 该注释用于判断int类型数据的位数
    private static int sizeOfInt(int x) {
        for (int i = 0; ; i++)
            if (x <= sizeTable[i])
                return i + 1;
    }*/

    @Override
    public void update(int distinguishNotified, int position, int cancelOrderBid) {
        switch (distinguishNotified) {
            case StringsFiled.OBSERVER_AMMONIA_SURE:
                mAllWarnFacilityData -= 0.5; // 减去报警的分数
                mAmmoniaWarnNumber--;// 减少round报警数
                mAmmoniaAlreadyWarnNumber++;// 增加round已处理数
                break;
            case StringsFiled.OBSERVER_TEMPERATURE_SURE:
                mAllWarnFacilityData -= 0.5;
                mTemperatureWarnNumber--;
                mTemperatureAlreadyWarnNumber++;
                break;
            case StringsFiled.OBSERVER_HUMIDITY_SURE:
                mAllWarnFacilityData -= 0.5;
                mHumidityWarnNumber--;
                mHumidityAlreadyWarnNumber++;
                break;
            case StringsFiled.OBSERVER_POWER_SUPPLY_SURE:
                mAllWarnFacilityData -= 0.5;
                mPowerSupplyWarnNumber--;
                mPowerSupplyAlreadyWarnNumber++;
                break;
        }
        if (distinguishNotified > 1 && distinguishNotified < 6) {
            countGetSafeValue();// 这个是下面两个方法的前提 计算安全指数

            updateRingProgress();// 根据安全指数 修改主圆圈的percent与颜色
            gistSafeNumberSetText();// 根据安全指数 修改圆圈下的文本提示

            updateEightItemRound();// 根据round报警数与已处理数更新round控件
        }

    }

    static class MyHandler extends Handler {
        WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mActivity.get();
            switch (msg.what) {
                case StringsFiled.SERVER_OUTAGE:
                    ToastUtil.showToast(mainActivity, "服务器宕机,请稍后");
                    mainActivity.setLoadingVisibility(View.GONE);
                    mainActivity.setIsInterruptTouch(false);
                    break;
                case StringsFiled.SERVER_THROW:
                    ToastUtil.showToast(mainActivity, "服务器正忙,请稍后");
                    mainActivity.setLoadingVisibility(View.GONE);
                    mainActivity.setIsInterruptTouch(false);
                    break;
                case StringsFiled.SERVER_NO_DATA:
                    ToastUtil.showToast(mainActivity, "服务器无数据");
                    mainActivity.setLoadingVisibility(View.GONE);
                    mainActivity.setIsInterruptTouch(false);
                    break;
                case StringsFiled.STOP_LOADING_BUT_NO_CLICK:// 在这里是已经从网络上获取到了具体数据，在进行解析前先停止加载页面，并初始化一些数据与Ui
                    mainActivity.setLoadingVisibility(View.GONE);
                    mainActivity.dataInitToView();// 该方法根据归零的数据更新初始化UI (在发送该message消息前已经将数据归零初始)
                    break;
                case StringsFiled.MAIN_ANALYSIS_FINISH_JSON:// 从网络获取的数据已经全部解析完成，并且也将数据进行了8个分类的存储
                    mainActivity.mSafeProcessResult.setTextColor(ContextCompat.getColor(mainActivity, R.color.white));
                    mainActivity.gistSafeNumberSetText();// 修改圆圈下的文本提示
                    mainActivity.setIsInterruptTouch(false);
                    ToastUtil.showToast(mainActivity, "扫描完成");
                    break;
                case StringsFiled.MAIN_DISPOSE_DATA_TO_UI:// 在解析数据的时，没解析了一条数据就更新一次UI
                    mainActivity.updateRingProgress();// 根据安全指数 修改主圆圈的percent与颜色
                    mainActivity.updateSafeProcessResult();// 根据解析的数据更新检查的设备所在位置与名称
                    mainActivity.setFourItemAllNumber();// 设置四大模块的总数文本
                    mainActivity.updateEightItemRound();// 更新四大模块的八个红灰圆点
                    break;
            }
        }
    }

    // 修改圆圈下的文本提示
    private void gistSafeNumberSetText() {
        if (mSafePercentNumber == 100) {
            mSafeProcessResult.setText("您的系统很安全,点击安全指数重新检测");
        } else if (mSafePercentNumber >= 80 && mSafePercentNumber < 100) {
            mSafeProcessResult.setText("安全等级良好,点击安全指数重新检测");
        } else if (mSafePercentNumber < 80 && mSafePercentNumber >= 60) {
            mSafeProcessResult.setText("安全等级及格,点击安全指数重新检测");
        } else if (mSafePercentNumber < 60) {
            mSafeProcessResult.setText("安全等级不及格,点击安全指数重新检测");
        }
    }

    MyHandler myHandler = new MyHandler(this);

    @Override
    public void onCreateContent() {
        View view = setContent(R.layout.activity_main);

        MySubject.getInstance().add(this);// 添加观察者模式

        // 根据设置界面中是否开启了通知报警与服务是否已经开启类判断是否要开启服务
        boolean informWarn = SharedPreferencesUtil.getboolean(
                ViewsUitls.getContext(), StringsFiled.INFORM_WARN, false);
        if (informWarn) {
            if (!ViewsUitls.isServiceWork("com.minlu.fosterpig.activity.AlarmServicer")) {
                System.out.println("=====================在主界面开启了报警服务===============");
                startService(MyApplication.getIntentServicer());
            }
        }

        MyApplication.getSaveActivity().add(this);// 将本activity加入集合便于设置界面中退出

        // 判断是否第一次进入app主页,是就存储初始化的视频参数值,不是就跳过
        if (-1 == SharedPreferencesUtil.getint(ViewsUitls.getContext(), StringsFiled.FIRST_IN_APP, -1)) {
            SharedPreferencesUtil.saveint(ViewsUitls.getContext(), StringsFiled.FIRST_IN_APP, 1);
            SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.VIDEO_LOGIN_IP_KEY, IpFiled.VIDEO_LOGIN_IP_VALUE);
            SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.VIDEO_LOGIN_USER_NAME_KEY, IpFiled.VIDEO_LOGIN_USER_NAME_VALUE);
            SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.VIDEO_LOGIN_PASS_WORD_KEY, IpFiled.VIDEO_LOGIN_PASS_WORD_VALUE);
            System.out.println("第一次登录");
        } else {
            System.out.println("不是第一次登录");
        }

        // 该存储内容是为了在其他界面确认过报警后在回到本界面时可以根据这个数据来判断时候要重新扫描
        SharedPreferencesUtil.saveint(ViewsUitls.getContext(), StringsFiled.IS_SURE_WARN, -1);// 存储为-1初始化

        initContentView(view);
        startRunPoint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPreferencesUtil.getint(ViewsUitls.getContext(), StringsFiled.IS_SURE_WARN, -1) > 0) {
            startRunPoint();
            SharedPreferencesUtil.saveint(ViewsUitls.getContext(), StringsFiled.IS_SURE_WARN, -1);
        }
    }

    private void initContentView(View view) {

        getThreePoint().setOnClickListener(this);
        getThreeLine().setOnClickListener(this);

        // 四个条目中代表监控数的文本
        mAmmoniaMonitor = (TextView) view.findViewById(R.id.tv_item_ammonia_monitor_number);
        mTemperatureMonitor = (TextView) view.findViewById(R.id.tv_item_temperature_monitor_number);
        mHumidityMonitor = (TextView) view.findViewById(R.id.tv_item_humidity_monitor_number);
        mPowerSupplyMonitor = (TextView) view.findViewById(R.id.tv_item_power_supply_monitor_number);

        // 四个条目中代表警报数的文本
        mAmmoniaWarn = (TextView) view.findViewById(R.id.tv_item_ammonia_warn_number);
        mTemperatureWarn = (TextView) view.findViewById(R.id.tv_item_temperature_warn_number);
        mHumidityWarn = (TextView) view.findViewById(R.id.tv_item_humidity_warn_number);
        mPowerSupplyWarn = (TextView) view.findViewById(R.id.tv_item_power_supply_warn_number);

        mAmmoniaAlreadyWarn = (TextView) view.findViewById(R.id.tv_item_ammonia_already_warn_number);
        mTemperatureAlreadyWarn = (TextView) view.findViewById(R.id.tv_item_temperature_already_warn_number);
        mHumidityAlreadyWarn = (TextView) view.findViewById(R.id.tv_item_humidity_already_warn_number);
        mPowerSupplyAlreadyWarn = (TextView) view.findViewById(R.id.tv_item_power_supply_already_warn_number);

        // 四个条目的点击事件
        RelativeLayout mItemAmmonia = (RelativeLayout) view.findViewById(R.id.rl_item_ammonia);
        mItemAmmonia.setOnClickListener(this);
        RelativeLayout mItemTemperature = (RelativeLayout) view.findViewById(R.id.rl_item_temperature);
        mItemTemperature.setOnClickListener(this);
        RelativeLayout mItemPowerSupply = (RelativeLayout) view.findViewById(R.id.rl_item_power_supply);
        mItemPowerSupply.setOnClickListener(this);
        RelativeLayout mItemHumidity = (RelativeLayout) view.findViewById(R.id.rl_item_humidity);
        mItemHumidity.setOnClickListener(this);

        // 圆环扫描相关控件
        mSafeProcessResult = (TextView) view.findViewById(R.id.tv_safe_process_result);
        mPercent = (TextView) view.findViewById(R.id.tv_percent);
        mSafeNumber = (FrameLayout) view.findViewById(R.id.fl_safe_number_annulus);
        mRingProgressView = (ColorfulRingProgressView) view.findViewById(R.id.color_ful_ring_progress_view);
        mRingProgressView.setOnClickListener(this);

        if (mSafeNumber != null && mRingProgressView != null) {
            ViewTreeObserver viewTreeObserver = mSafeNumber.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mSafeNumber.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    ViewGroup.LayoutParams layoutParams = mRingProgressView.getLayoutParams();
                    layoutParams.width = mSafeNumber.getHeight();
                    mRingProgressView.setLayoutParams(layoutParams);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // 根据报警数或已处理数来判断 是否可以点击进入四大模块
            case R.id.rl_item_ammonia:
                if (mAmmoniaWarnNumber > 0 || mAmmoniaAlreadyWarnNumber > 0) {
                    mainSkipToWarn(StringsFiled.MAIN_TO_WARN_AMMONIA, "氨气报警");
                }
                break;
            case R.id.rl_item_temperature:
                if (mTemperatureWarnNumber > 0 || mTemperatureAlreadyWarnNumber > 0) {
                    mainSkipToWarn(StringsFiled.MAIN_TO_WARN_TEMPERATURE, "温度报警");
                }
                break;
            case R.id.rl_item_humidity:
                if (mHumidityWarnNumber > 0 || mHumidityAlreadyWarnNumber > 0) {
                    mainSkipToWarn(StringsFiled.MAIN_TO_WARN_HUMIDITY, "湿度报警");
                }
                break;
            case R.id.rl_item_power_supply:
                if (mPowerSupplyWarnNumber > 0 || mPowerSupplyAlreadyWarnNumber > 0) {
                    mainSkipToWarn(StringsFiled.MAIN_TO_WARN_POWER_SUPPLY, "市电报警");
                }
                break;
            // 点击圆圈开始扫描
            case R.id.color_ful_ring_progress_view:
                startRunPoint();
                break;
            // 左右两个角的点击
            case R.id.iv_title_three_line:
                Intent trueTimeIntent = new Intent(getApplicationContext(), TrueTimeDataActivity.class);
                trueTimeIntent.putExtra(StringsFiled.ACTIVITY_TITLE, "实时数据");
                startActivity(trueTimeIntent);
                break;
            case R.id.iv_title_three_point:
                Intent settingIntent = new Intent(getApplicationContext(), SettingActivity.class);
                settingIntent.putExtra(StringsFiled.ACTIVITY_TITLE, "设置");
                startActivity(settingIntent);
                break;
        }


    }

    private void startRunPoint() {
        setLoadingVisibility(View.VISIBLE);
        setIsInterruptTouch(true);
        // 点击圆环开始请求网络
        requestAllMonitorInformation();
    }

    // 四大模块跳转界面
    private void mainSkipToWarn(String mainToWarn, String title) {
        Intent intent = new Intent(getApplicationContext(), WarnActivity.class);
        intent.putExtra(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY, mainToWarn);
        intent.putExtra(StringsFiled.ACTIVITY_TITLE, title);
        startActivity(intent);
    }

    // 网络请求所有数据
    private void requestAllMonitorInformation() {

        OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
        RequestBody formBody = new FormBody.Builder().build();

        String ipAddress = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");

        Request request = new Request.Builder()
                .url(ipAddress + IpFiled.MAIN_GET_ALL_INFORMATION)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                myHandler.sendEmptyMessage(StringsFiled.SERVER_OUTAGE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mResultJSON = response.body().string();
                if (StringUtils.interentIsNormal(mResultJSON)) {
                    analysisDataJSON();
                } else {
                    myHandler.sendEmptyMessage(StringsFiled.SERVER_THROW);
                }
            }
        });

    }

    // 解析json数据
    private void analysisDataJSON() {
        System.out.println("解析数据: " + mResultJSON);
        try {
            JSONObject jsonObject = new JSONObject(mResultJSON);
            JSONObject allInformation = jsonObject.optJSONObject("mapList");
            if (allInformation.has("selectList")) {
                JSONArray informationList = allInformation.optJSONArray("selectList");
                if (informationList.length() > 0) {

                    dataInit();// 将所有数据都归零还原初始

                    // 这里是准备分析一条条数据，所以去除转圈，但不能点击
                    myHandler.sendEmptyMessage(StringsFiled.STOP_LOADING_BUT_NO_CLICK);

                    mAllFacilityData = informationList.length();// 每次准备解析数组数据，就将数组的长度赋值给mAllFacilityData
                    mAllWarnFacilityData = 0f;// 每次准备解析数组数据，就将报警设备数清零
                    for (int i = 0; i < informationList.length(); i++) {

                        JSONObject singleInformation = informationList.getJSONObject(i);

//                       "status": 0未处理  1处理   endTime处理时间
                        int handleStatus = singleInformation.optInt("status");
                        String handleTime = singleInformation.optString("endTime");

                        int facilityType = singleInformation.optInt("type");//1氨气 2温度 3湿度 4市电通道一 。。。11市电通道八
                        double facilityValue = singleInformation.optDouble("value");// 市电的值0断1通  温湿氨气为double

                        // 获取是否报警
                        int isWarn = -1;
                        if (singleInformation.has("police")) {
                            isWarn = singleInformation.optInt("police");// 1报警0不报警 市电没有这个字段
                        }

                        String startWarnTime = "---";
                        if (singleInformation.has("startWarnTime")) {
                            startWarnTime = singleInformation.optString("startWarnTime");
                        }
                        String siteName = singleInformation.optString("dtuName");
                        String facilityName = singleInformation.optString("lmuName");
                        String areaName = singleInformation.optString("stationName");
                        int siteId = singleInformation.optInt("dtuId");
                        int facilityId = singleInformation.optInt("lmuId");
                        int areaId = singleInformation.optInt("stationId");

                        int mainId = -1;
                        if (singleInformation.has("id")) {
                            mainId = singleInformation.optInt("id");
                        }
                        // 下面是对数据进行处理，发到ui进行更新
                        disposeDataToUI(handleStatus, handleTime, mainId, facilityType, facilityValue, isWarn, siteName, areaName, facilityName, siteId, facilityId, areaId, startWarnTime);

                        // 延迟时间，给ui更新
                        try {
                            // TODO
                            System.out.println();
                            Thread.sleep((int) (mAllFacilityData * 20) / informationList.length());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        myHandler.sendEmptyMessage(StringsFiled.MAIN_DISPOSE_DATA_TO_UI);
                    }

                    SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_AMMONIA_JSON, GsonTools.createGsonString(mAllAmmoniaWarnData));
                    SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_TEMPERATURE_JSON, GsonTools.createGsonString(mAllTemperatureWarnData));
                    SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_HUMIDITY_JSON, GsonTools.createGsonString(mAllHumidityWarnData));
                    SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_POWER_SUPPLY_JSON, GsonTools.createGsonString(mAllPowerSupplyWarnData));

                    SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_AMMONIA_JSON, GsonTools.createGsonString(mAllAmmoniaAlreadyWarnData));
                    SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_TEMPERATURE_JSON, GsonTools.createGsonString(mAllTemperatureAlreadyWarnData));
                    SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_HUMIDITY_JSON, GsonTools.createGsonString(mAllHumidityAlreadyWarnData));
                    SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_POWER_SUPPLY_JSON, GsonTools.createGsonString(mAllPowerSupplyAlreadyWarnData));

                    myHandler.sendEmptyMessage(StringsFiled.MAIN_ANALYSIS_FINISH_JSON);
                } else {
                    // json数组里没有一点数据
                    myHandler.sendEmptyMessage(StringsFiled.SERVER_NO_DATA);
                }
            } else {
                // 没有selectList这个字段说明服务器异常了
                myHandler.sendEmptyMessage(StringsFiled.SERVER_THROW);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void disposeDataToUI(int handleStatus, String handleTime, int mainId, int facilityType, double facilityValue, int isWarn, String siteName,
                                 String areaName, String facilityName, int siteId, int facilityId, int areaId, String startWarnTime) {
        MainAllInformation mainAllInformation = new MainAllInformation(mainId, areaName, siteName, siteId, facilityName, facilityId, areaId, facilityType, facilityValue, isWarn, startWarnTime);
        AlreadySureWarn alreadySureWarn = new AlreadySureWarn(startWarnTime, handleTime, siteName, areaName, facilityType, facilityValue);
        mAreaName = areaName;
        mSiteName = siteName;
        switch (facilityType) {
            case 1:// 1氨气 a
                mFacilityName = "氨气传感器";
                mAmmoniaAllNumber++;
                if (isWarn == 1) {
                    if (handleStatus == 1) {
                        mAllAmmoniaAlreadyWarnData.add(alreadySureWarn);
                        mAllWarnFacilityData += 0.5;
                        mAmmoniaAlreadyWarnNumber++;
                    } else {
                        mAllWarnFacilityData++; // 温湿氨的报警
                        mAmmoniaWarnNumber++;
                        mAllAmmoniaWarnData.add(mainAllInformation);
                    }
                }
                break;
            case 2:// 2温度 t
                mFacilityName = "温度传感器";
                mTemperatureAllNumber++;
                if (isWarn == 1) {
                    if (handleStatus == 1) {
                        mAllTemperatureAlreadyWarnData.add(alreadySureWarn);
                        mAllWarnFacilityData += 0.5;
                        mTemperatureAlreadyWarnNumber++;
                    } else {
                        mAllWarnFacilityData++; // 温湿氨的报警
                        mTemperatureWarnNumber++;
                        mAllTemperatureWarnData.add(mainAllInformation);
                    }
                }
                break;
            case 3:// 3湿度 h
                mFacilityName = "湿度传感器";
                mHumidityAllNumber++;
                if (isWarn == 1) {
                    if (handleStatus == 1) {
                        mAllHumidityAlreadyWarnData.add(alreadySureWarn);
                        mAllWarnFacilityData += 0.5;
                        mHumidityAlreadyWarnNumber++;
                    } else {
                        mAllWarnFacilityData++; // 温湿氨的报警
                        mHumidityWarnNumber++;
                        mAllHumidityWarnData.add(mainAllInformation);
                    }
                }
                break;
            default:// 市电 p
                mFacilityName = "市电通道" + (facilityType - 3);
                mPowerSupplyAllNumber++;
                if (isWarn == 1) {
                    if (handleStatus == 1) {
                        mAllPowerSupplyAlreadyWarnData.add(alreadySureWarn);
                        mAllWarnFacilityData += 0.5;
                        mPowerSupplyAlreadyWarnNumber++;
                    } else {
                        mAllWarnFacilityData++; // 市电的报警
                        mPowerSupplyWarnNumber++;
                        mAllPowerSupplyWarnData.add(mainAllInformation);
                    }
                }
                break;
        }
        // 计算获取安全数值
        countGetSafeValue();

        // Log.v("allData", "安全指数：" + mSafePercentNumber + " 氨气总数：" + mAmmoniaAllNumber + " 温度总数：" + mTemperatureAllNumber + " 湿度总数：" + mHumidityAllNumber + " 市电总数：" + mPowerSupplyAllNumber + " 氨气报警总数：" + mAmmoniaWarnNumber + " 温度报警总数：" + mTemperatureWarnNumber + " 湿度报警总数：" + mHumidityWarnNumber + " 市电报警总数：" + mPowerSupplyWarnNumber + " 区域名字：" + mAreaName + " 站点名字：" + mSiteName + " 设备名字：" + mFacilityName);
    }

    // 计算安全指数
    private void countGetSafeValue() {
        mSafePercentNumber = (int) (((mAllFacilityData - mAllWarnFacilityData) / mAllFacilityData) * 100);
    }

    // 该方法根据归零的数据更新初始化UI
    private void dataInitToView() {
        setFourItemAllNumber();// 设置四大模块的总数文本
        updateEightItemRound();// 更新四大模块的八个红灰圆点
        updateRingProgress();// 根据安全指数 修改主圆圈的percent与颜色
    }

    // 将所有数据都归零还原初始
    private void dataInit() {
        mAmmoniaAllNumber = 0;
        mTemperatureAllNumber = 0;
        mHumidityAllNumber = 0;
        mPowerSupplyAllNumber = 0;

        mAmmoniaWarnNumber = 0;
        mTemperatureWarnNumber = 0;
        mHumidityWarnNumber = 0;
        mPowerSupplyWarnNumber = 0;

        mAmmoniaAlreadyWarnNumber = 0;
        mTemperatureAlreadyWarnNumber = 0;
        mHumidityAlreadyWarnNumber = 0;
        mPowerSupplyAlreadyWarnNumber = 0;


        mFacilityName = "";
        mAreaName = "";
        mSiteName = "";

        mSafePercentNumber = 100;

        // 存储四大模块的数据
        mAllAmmoniaWarnData.clear();
        mAllTemperatureWarnData.clear();
        mAllHumidityWarnData.clear();
        mAllPowerSupplyWarnData.clear();

        mAllAmmoniaAlreadyWarnData.clear();
        mAllTemperatureAlreadyWarnData.clear();
        mAllHumidityAlreadyWarnData.clear();
        mAllPowerSupplyAlreadyWarnData.clear();
    }

    // 更新四大模块的八个红灰圆点
    private void updateEightItemRound() {
        // 设置四个模块的警告红点灰点
        setRoundShow(mAmmoniaWarnNumber, mAmmoniaAlreadyWarnNumber, mAmmoniaWarn, mAmmoniaAlreadyWarn);
        setRoundShow(mTemperatureWarnNumber, mTemperatureAlreadyWarnNumber, mTemperatureWarn, mTemperatureAlreadyWarn);
        setRoundShow(mHumidityWarnNumber, mHumidityAlreadyWarnNumber, mHumidityWarn, mHumidityAlreadyWarn);
        setRoundShow(mPowerSupplyWarnNumber, mPowerSupplyAlreadyWarnNumber, mPowerSupplyWarn, mPowerSupplyAlreadyWarn);
    }

    // 设置四个模块的警告红点灰点
    @SuppressLint("SetTextI18n")
    private void setRoundShow(int warnNumber, int alreadyWarnNumber, TextView warn, TextView alreadyWarn) {
        if (warnNumber > 0) {
            alreadyWarn.setVisibility(View.INVISIBLE);
            warn.setVisibility(View.VISIBLE);
            warn.setText("" + warnNumber);
        } else {
            warn.setVisibility(View.INVISIBLE);
            if (alreadyWarnNumber > 0) {
                alreadyWarn.setVisibility(View.VISIBLE);
                alreadyWarn.setText("" + alreadyWarnNumber);
            } else {
                alreadyWarn.setVisibility(View.INVISIBLE);
            }
        }
    }

    // 设置四大模块的总数文本
    private void setFourItemAllNumber() {
        // 设置四个模块下的文本
        mAmmoniaMonitor.setText("氨气[" + mAmmoniaAllNumber + "]");
        mHumidityMonitor.setText("湿度[" + mHumidityAllNumber + "]");
        mTemperatureMonitor.setText("温度[" + mTemperatureAllNumber + "]");
        mPowerSupplyMonitor.setText("市电[" + mPowerSupplyAllNumber + "]");// mPowerSupplyWarnNumber + "/" +
//        setPowerSupplyMonitorTextColor();
    }

    /*private void setPowerSupplyMonitorTextColor() {
        SpannableStringBuilder builder = new SpannableStringBuilder(mPowerSupplyMonitor.getText().toString());
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
        builder.setSpan(redSpan, 3, MainActivity.sizeOfInt(mPowerSupplyWarnNumber) + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPowerSupplyMonitor.setText(builder);
    }*/

    private void updateSafeProcessResult() {
        // 设置圆环下的文本内容与颜色
        mSafeProcessResult.setText(mAreaName + "-" + mSiteName + "-" + mFacilityName);
    }

    /*根据mSafePercentNumber值修改控件ui: 多少分的TextView控件修改其文本，还有圆圈修改其percent与颜色*/
    @SuppressLint("SetTextI18n")
    private void updateRingProgress() {
        // 更新圆环进度与颜色，安全指数大小与颜色
        mRingProgressView.setPercent(mSafePercentNumber);
        mPercent.setText(mSafePercentNumber + "分");
        if (mSafePercentNumber == 100) {
            setPercentColorProgressBackGround(R.color.white);
        } else if (mSafePercentNumber >= 80 && mSafePercentNumber < 100) {
            setPercentColorProgressBackGround(R.color.white);
        } else if (mSafePercentNumber < 80 && mSafePercentNumber >= 60) {
            setPercentColorProgressBackGround(R.color.white);
        } else if (mSafePercentNumber < 60) {
            setPercentColorProgressBackGround(R.color.white);
        }
    }

    private void setPercentColorProgressBackGround(int color) {
        mRingProgressView.setFgColorEnd(ContextCompat.getColor(MainActivity.this, color));
        mRingProgressView.setFgColorStart(ContextCompat.getColor(MainActivity.this, color));
        mPercent.setTextColor(ContextCompat.getColor(MainActivity.this, color));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MySubject.getInstance().del(this);
        MyApplication.getSaveActivity().remove(this);
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {                                                    //两次按键小于2秒时，退出应用
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
