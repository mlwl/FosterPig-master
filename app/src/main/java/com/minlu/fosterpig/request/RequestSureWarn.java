package com.minlu.fosterpig.request;

import android.view.View;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.bean.MainAllInformation;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.ToastUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by user on 2016/12/6.
 */
public class RequestSureWarn {

    public static void requestSureWarn(MainAllInformation mainAllInformation, final BaseActivity baseActivity, final RequestResult requestResult) {
        // 加载页面显示
        baseActivity.setLoadingVisibility(View.VISIBLE);
        baseActivity.setIsInterruptTouch(true);

        int mainId = mainAllInformation.getMainId();

        // 开启请求网络
        OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
        System.out.println("确认报警传递的字段id: " + mainId);
        RequestBody formBody = new FormBody.Builder().add("id", "" + mainId).build();

        String ipAddress = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");

        Request request = new Request.Builder()
                .url(ipAddress + IpFiled.REQUEST_SURE_WARN)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("=======================onFailure===========================");
                ViewsUitls.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(baseActivity, "服务器宕机,请稍后");
                        baseActivity.setLoadingVisibility(View.GONE);
                        baseActivity.setIsInterruptTouch(false);
                    }
                });
            }

            @Override
            public void onResponse(final Call call, final Response response) {
                System.out.println("=========================onResponse==========================");
                String string = null;
                try {
                    string = response.body().string();
                    System.out.println("确认报警结果: " + string);
                } catch (IOException e) {
                    e.printStackTrace();
                    ViewsUitls.runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(baseActivity, "服务器结果异常,请稍后");
                            baseActivity.setLoadingVisibility(View.GONE);
                            baseActivity.setIsInterruptTouch(false);
                        }
                    });
                }
                final boolean result;
                if (string.contains("success")) {
                    result = true;
                } else {
                    result = false;
                }
                ViewsUitls.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        requestResult.onResponse(result);
                        baseActivity.setLoadingVisibility(View.GONE);
                        baseActivity.setIsInterruptTouch(false);
                    }
                });
            }
        });
    }


}
