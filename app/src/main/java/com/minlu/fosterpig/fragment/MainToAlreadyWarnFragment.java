package com.minlu.fosterpig.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.adapter.MainToAlreadyWarnAdapter;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.bean.AlreadySureWarn;
import com.minlu.fosterpig.manager.ThreadManager;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ToastUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/11/23.
 */
public class MainToAlreadyWarnFragment extends BaseFragment<AlreadySureWarn> implements SwipeRefreshLayout.OnRefreshListener {
    private List<AlreadySureWarn> allAlreadySureWarn;
    private ListView listView;
    private MainToAlreadyWarnAdapter sureWarnAdapter;

    private String mResultString;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Runnable mRefreshThread;
    private int bundleValue;

    @Override
    protected void onSubClassOnCreateView() {
        loadDataAndRefresh();
    }

    @Override
    protected View onCreateSuccessView() {

        View inflate = ViewsUitls.inflate(R.layout.layout_listview);

        swipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_list_view_no_swipe_menu);
        listView = (ListView) inflate.findViewById(R.id.have_swipe_refresh_list_view);

        sureWarnAdapter = new MainToAlreadyWarnAdapter(allAlreadySureWarn);
        listView.setAdapter(sureWarnAdapter);

        //改变加载显示的颜色
        swipeRefreshLayout.setColorSchemeColors(StringsFiled.SWIPE_REFRESH_FIRST_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_SECOND_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_THIRD_ROUND_COLOR);
        //设置背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(StringsFiled.SWIPE_REFRESH_BACKGROUND);
        //设置初始时的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        //设置监听
        swipeRefreshLayout.setOnRefreshListener(this);
        return inflate;
    }

    @Override
    protected ContentPage.ResultState onLoad() {
        bundleValue = getBundleValue();
        requestData();
        return chat(allAlreadySureWarn);
    }

    private void requestData() {
        switch (bundleValue) {
            case StringsFiled.MAIN_TO_WARN_VALUE_AMMONIA:
                mResultString = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_AMMONIA_JSON, "");
                break;
            case StringsFiled.MAIN_TO_WARN_VALUE_TEMPERATURE:
                mResultString = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_TEMPERATURE_JSON, "");
                break;
            case StringsFiled.MAIN_TO_WARN_VALUE_HUMIDITY:
                mResultString = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_HUMIDITY_JSON, "");
                break;
            case StringsFiled.MAIN_TO_WARN_VALUE_POWER_SUPPLY:
                mResultString = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_POWER_SUPPLY_JSON, "");
                break;
        }
        if (!StringUtils.isEmpty(mResultString)) {
            analysisJsonDate();
        } else {
            allAlreadySureWarn = null;
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
            System.out.println(mResultString);
            try {
                JSONArray jsonArray = new JSONArray(mResultString);
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            allAlreadySureWarn = null;
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
                            if (allAlreadySureWarn != null) {
                                sureWarnAdapter.notifyDataSetChanged();
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
