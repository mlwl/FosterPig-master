package com.minlu.fosterpig.adapter;


import android.util.Log;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.base.BaseHolder;
import com.minlu.fosterpig.base.MyBaseAdapter;
import com.minlu.fosterpig.bean.AlreadySureWarn;
import com.minlu.fosterpig.holder.SureWarnHolder;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ViewsUitls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by user on 2016/11/22.
 */
public class SureWarnAdapter extends MyBaseAdapter<AlreadySureWarn> {

    private List<AlreadySureWarn> middleList;
    private String mResultString;

    public SureWarnAdapter(List<AlreadySureWarn> list) {
        super(list);
    }

    @Override
    public BaseHolder getHolder() {
        return new SureWarnHolder();
    }

    @Override
    public List<AlreadySureWarn> onLoadMore() {

        int middle = getDataSize();
        if (middle % 10 != 0) {  // 数据源只要不是10的整数都不用继续直接没有更多条数据
            return new ArrayList<>();
        } else {
            int c = middle / 10;//  数据源是10的整数，那就求出已经查询了几次 次数有可能是从0开始也可能是从1开始
            requestDate(c);    // middleList的值在这个方法中求出
            return middleList;// 返回null  空值  有数据
        }
    }

    @Override
    public boolean hasMore() {
        return true;
    }

    private void requestDate(int page) {

        OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
        // start查询数据的起点  limit要查多少条数据
        RequestBody formBody = new FormBody.Builder().add("dtuId", "0").add("judgeTotal", "" + -1).add("stationid", "" + -1).add("selectDate", "").add("start", "" + (page * 10)).add("limit", "10").build();

        String ipAddress = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");

        Request request = new Request.Builder()
                .url(ipAddress + IpFiled.ALL_ALREADY_SURE_WARN)
                .post(formBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                mResultString = response.body().string();
                parseJson();
            } else {
                System.out.println("=========================onFailure=============================");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("=========================onFailure=============================");
            Log.i("okHttp_ERROE", "okHttp is request error");
            middleList = null;
        }

    }

    private void parseJson() {
        // TODO 测试数据
//        try {
//            InputStream is = ViewsUitls.getContext().getAssets().open("textJson3.txt");
//            mResultString = readTextFromSDcard(is);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // TODO 测试数据
        Log.i("okHttp_SUCCESS", mResultString);
        if (StringUtils.interentIsNormal(mResultString)) {
            try {
                JSONObject object = new JSONObject(mResultString);
                if (object.has("hisAlamList")) {
                    JSONArray jsonArray = object.optJSONArray("hisAlamList");
                    if (middleList == null) {
                        middleList = new ArrayList<>();
                    } else {
                        middleList.clear();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject singleInformation = jsonArray.getJSONObject(i);
                        middleList.add(new AlreadySureWarn(singleInformation.optString("alarmTime"),
                                singleInformation.optString("handleTime"),
                                singleInformation.optString("areaName"),
                                singleInformation.optString("stationName"),
                                singleInformation.optInt("type"),
                                singleInformation.optDouble("value")));
                    }
                } else {
                    // 没有selectList这个字段说明服务器异常了
                    middleList = null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            middleList = null;
        }
    }

    private String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        return buffer.toString();
    }

}
