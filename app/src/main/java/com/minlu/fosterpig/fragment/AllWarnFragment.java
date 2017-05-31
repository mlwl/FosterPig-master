package com.minlu.fosterpig.fragment;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.adapter.AllWarnAdapter;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.bean.MainAllInformation;
import com.minlu.fosterpig.customview.swipelistview.SwipeMenu;
import com.minlu.fosterpig.customview.swipelistview.SwipeMenuCreator;
import com.minlu.fosterpig.customview.swipelistview.SwipeMenuItem;
import com.minlu.fosterpig.customview.swipelistview.SwipeMenuListView;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.manager.ThreadManager;
import com.minlu.fosterpig.request.RequestResult;
import com.minlu.fosterpig.request.RequestSureWarn;
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
 * Created by user on 2016/11/24.
 */
public class AllWarnFragment extends BaseFragment<MainAllInformation> implements SwipeRefreshLayout.OnRefreshListener {
    private List<MainAllInformation> allInformation;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AllWarnAdapter mAllWarnAdapter;
    private Runnable mRefreshThread;
    private String mResultString;

    private boolean requestDataIsSuccess;
    private BaseActivity mActivity;

    @Override
    protected void onSubClassOnCreateView() {
        loadDataAndRefresh();
    }

    @Override
    protected View onCreateSuccessView() {
        mActivity = (BaseActivity) getActivity();

        View inflate = ViewsUitls.inflate(R.layout.layout_swipe_menu_listview);

        swipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_list_view_have_swipe_menu);
        setSwipeRefreshSetting();


        SwipeMenuListView mListView = (SwipeMenuListView) inflate.findViewById(R.id.swipe_menu_list_view);

        setSwipeMenu(mListView);

        mAllWarnAdapter = new AllWarnAdapter(allInformation);
        mListView.setAdapter(mAllWarnAdapter);

        return inflate;
    }

    @Override
    protected ContentPage.ResultState onLoad() {


        requestData();

        if (!requestDataIsSuccess) {
            allInformation = null;
        }
        return chat(allInformation);
    }

    private void requestData() {
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
//            InputStream is = getActivity().getAssets().open("textJson.txt");
//            mResultString = readTextFromSDcard(is);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // TODO 测试数据

        if (StringUtils.interentIsNormal(mResultString)) {
            try {
                JSONObject jsonObject = new JSONObject(mResultString);
                JSONObject object = jsonObject.optJSONObject("mapList");
                if (object.has("selectList")) {
                    JSONArray informationList = object.optJSONArray("selectList");
                    if (allInformation == null) {
                        allInformation = new ArrayList<>();
                    } else {
                        allInformation.clear();
                    }
                    for (int i = 0; i < informationList.length(); i++) {

                        JSONObject singleInformation = informationList.getJSONObject(i);
                        int facilityType = singleInformation.optInt("type");//1氨气 2温度 3湿度 4市电通道一 。。。11市电通道八
                        double facilityValue = singleInformation.optDouble("value");// 市电的值0断1通  温湿氨气为double
                        // 获取是否报警
                        int isWarn = -1;
                        if (singleInformation.has("police")) {
                            isWarn = singleInformation.optInt("police");// 0报警1不报警 市电没有这个字段
                        }
                        String startWarnTime = "---";
                        if (singleInformation.has("startWarnTime")) {
                            String time = singleInformation.optString("startWarnTime");
                            if (!StringUtils.isEmpty(time)) {
                                startWarnTime = time;
                            }
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
                        int handleStatus = singleInformation.optInt("status");
                        if (isWarn == 1 && handleStatus == 0) {
                            allInformation.add(new MainAllInformation(mainId, areaName, siteName, siteId, facilityName, facilityId, areaId, facilityType, facilityValue, isWarn, startWarnTime));
                        }
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
            System.out.println("WarnInformation-New-Thread");
            mRefreshThread = new Runnable() {
                @Override
                public void run() {
                    requestData();
                    ViewsUitls.runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (requestDataIsSuccess) {
                                mAllWarnAdapter.notifyDataSetChanged();
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

    private void setSwipeRefreshSetting() {
        //改变加载显示的颜色
        swipeRefreshLayout.setColorSchemeColors(StringsFiled.SWIPE_REFRESH_FIRST_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_SECOND_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_THIRD_ROUND_COLOR);
        //设置背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(StringsFiled.SWIPE_REFRESH_BACKGROUND);
        //设置初始时的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        //设置监听
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void setSwipeMenu(SwipeMenuListView mListView) {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(ViewsUitls.getContext());
                // set item background
                openItem.setBackground(R.drawable.selector_swipe_menu_bottom_item_background);
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("确认");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

            }
        };

        // set creator
        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        MainAllInformation mainAllInformation = allInformation.get(position);
                        RequestSureWarn.requestSureWarn(mainAllInformation, mActivity, new RequestResult() {
                            @Override
                            public void onResponse(boolean result) {// 此处是主线程，根据结果进行不同的处理
                                if (result) {
                                    allInformation.remove(position);
                                    mAllWarnAdapter.notifyDataSetChanged();
                                    ToastUtil.showToast(ViewsUitls.getContext(), "确认报警成功");
                                    SharedPreferencesUtil.saveint(ViewsUitls.getContext(), StringsFiled.IS_SURE_WARN, 6);
                                } else {
                                    ToastUtil.showToast(ViewsUitls.getContext(), "确认报警失败");
                                }
                            }
                        });

                        break;
                }
                // ★★★★★false : close the menu; true : not close the menu
                return false;
            }
        });
        mListView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {
                System.out.println("setOnMenuStateChangeListener+onMenuOpen: " + position);
                swipeRefreshLayout.setEnabled(true);
            }

            @Override
            public void onMenuClose(int position) {
                System.out.println("setOnMenuStateChangeListener+onMenuClose: " + position);
                swipeRefreshLayout.setEnabled(true);
            }
        });
        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                System.out.println("setOnSwipeListener+onSwipeStart: " + position);
                swipeRefreshLayout.setEnabled(false);
            }

            @Override
            public void onSwipeEnd(int position) {
                System.out.println("setOnSwipeListener+onSwipeEnd: " + position);
                swipeRefreshLayout.setEnabled(true);
            }
        });
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
