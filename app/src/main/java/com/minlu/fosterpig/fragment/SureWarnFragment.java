package com.minlu.fosterpig.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.adapter.SureWarnAdapter;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.bean.AlreadySureWarn;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.manager.ThreadManager;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ToastUtil;
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
 * Created by user on 2016/11/23.
 */
public class SureWarnFragment extends BaseFragment<AlreadySureWarn> implements SwipeRefreshLayout.OnRefreshListener {
    private List<AlreadySureWarn> allAlreadySureWarn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SureWarnAdapter sureWarnAdapter;
    private Runnable mRefreshThread;

    private boolean requestDataIsSuccess;
    private String mResultString;

    @Override
    protected void onSubClassOnCreateView() {
        loadDataAndRefresh();
    }

    @Override
    protected View onCreateSuccessView() {

        View inflate = ViewsUitls.inflate(R.layout.layout_listview);

        swipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_list_view_no_swipe_menu);
        listView = (ListView) inflate.findViewById(R.id.have_swipe_refresh_list_view);

        sureWarnAdapter = new SureWarnAdapter(allAlreadySureWarn);
        listView.setAdapter(sureWarnAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("点击了条目 position: " + position + " lissize: " + allAlreadySureWarn.size());
                ViewGroup viewGroup = (ViewGroup) view;
                if (position == allAlreadySureWarn.size() && viewGroup.getChildAt(0).getVisibility() == View.GONE) {
                    System.out.println("加载更多的条目");

                    viewGroup.getChildAt(1).setVisibility(View.GONE);
                    viewGroup.getChildAt(0).setVisibility(View.VISIBLE);

                    sureWarnAdapter.loadMore();
                    listView.setSelection(position);
                }
            }
        });

        //改变加载显示的颜色
        swipeRefreshLayout.setColorSchemeColors(StringsFiled.SWIPE_REFRESH_FIRST_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_SECOND_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_THIRD_ROUND_COLOR);
        //设置背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(StringsFiled.SWIPE_REFRESH_BACKGROUND);
        //设置初始时的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        //设置监听
        swipeRefreshLayout.setOnRefreshListener(this);
        //设置向下拉多少出现刷新
//        swipeRefreshLayout.setDistanceToTriggerSync(100);
        //设置刷新出现的位置
//        swipeRefreshLayout.setProgressViewEndTarget(false, 200);


        return inflate;
    }

    @Override
    protected ContentPage.ResultState onLoad() {

        requestData();
        if (!requestDataIsSuccess) {
            allAlreadySureWarn = null;
        }
        return chat(allAlreadySureWarn);
    }

    private void requestData() {
        OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
        // start查询数据的起点  limit要查多少条数据
        RequestBody formBody = new FormBody.Builder().add("dtuId", "0").add("judgeTotal", "" + -1).add("stationid", "" + -1).add("selectDate", "").add("start", "0").add("limit", "10").build();

        String ipAddress = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");

        Request request = new Request.Builder()
                .url(ipAddress + IpFiled.ALL_ALREADY_SURE_WARN)
                .post(formBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                mResultString = response.body().string();
                Log.i("okHttp_SUCCESS", mResultString);
                analysisJsonDate();
            } else {
                System.out.println("=========================onFailure=============================");
                requestDataIsSuccess = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("=========================onFailure=============================");
            Log.i("okHttp_ERROE", "okHttp is request error");
            requestDataIsSuccess = false;
        }
    }

    private void analysisJsonDate() {

        // TODO 测试数据
//        try {
//            InputStream is = getActivity().getAssets().open("textJson3.txt");
//            mResultString = readTextFromSDcard(is);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // TODO 测试数据

        if (StringUtils.interentIsNormal(mResultString)) {
            try {
                JSONObject object = new JSONObject(mResultString);
                if (object.has("hisAlamList")) {
                    JSONArray jsonArray = object.optJSONArray("hisAlamList");
                    if (allAlreadySureWarn == null) {
                        allAlreadySureWarn = new ArrayList<>();
                    } else {
                        allAlreadySureWarn.clear();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject singleInformation = jsonArray.getJSONObject(i);
                        allAlreadySureWarn.add(new AlreadySureWarn(singleInformation.optString("alarmTime"),
                                singleInformation.optString("handleTime"),
                                singleInformation.optString("areaName"),
                                singleInformation.optString("stationName"),
                                singleInformation.optInt("type"),
                                singleInformation.optDouble("value")));
                    }
                    requestDataIsSuccess = true;
                } else {
                    // 没有selectList这个字段说明服务器异常了
                    requestDataIsSuccess = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            requestDataIsSuccess = false;
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

    @Override
    public void onRefresh() {

        if (mRefreshThread == null) {
            System.out.println("SureWarnFragment-New-Thread");
            mRefreshThread = new Runnable() {
                @Override
                public void run() {
                    requestData();
                    ViewsUitls.runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (requestDataIsSuccess) {
                                sureWarnAdapter.notifyDataSetChanged();
                                sureWarnAdapter.getMoreHolder().setData(0);// 给moreholder设置可以加载更多
                            } else {
                                ToastUtil.showToast(ViewsUitls.getContext(), "刷新失败");
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            };
        }
        ThreadManager.getInstance().execute(mRefreshThread);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        System.out.println("WarnInformation-onDestroy");
        if (mRefreshThread != null) {
            System.out.println("线程没有结束");
            ThreadManager.getInstance().cancel(mRefreshThread);
            mRefreshThread = null;
        }
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            System.out.println("还在刷新");
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
