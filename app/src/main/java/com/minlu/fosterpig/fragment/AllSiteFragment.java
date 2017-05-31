package com.minlu.fosterpig.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.adapter.AllSiteAdapter;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.bean.AllSiteBean;
import com.minlu.fosterpig.bean.FacilityDetail;
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
public class AllSiteFragment extends BaseFragment<AllSiteBean> implements SwipeRefreshLayout.OnRefreshListener {

    private List<AllSiteBean> mAllAreaData;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpandableListView expandableListView;
    private int currentExpandGroup = -1;
    private Runnable mRefreshThread;
    private AllSiteAdapter allSiteAdapter;
    private String mResultString;
    private boolean requestDataIsSuccess;

    @Override
    protected void onSubClassOnCreateView() {
        loadDataAndRefresh();
    }

    @Override
    protected View onCreateSuccessView() {

        View inflate = ViewsUitls.inflate(R.layout.layout_all_site);

        swipeRefreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_all_site);
        //改变加载显示的颜色
        swipeRefreshLayout.setColorSchemeColors(StringsFiled.SWIPE_REFRESH_FIRST_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_SECOND_ROUND_COLOR, StringsFiled.SWIPE_REFRESH_THIRD_ROUND_COLOR);
        //设置背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(StringsFiled.SWIPE_REFRESH_BACKGROUND);
        //设置初始时的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        //设置监听
        swipeRefreshLayout.setOnRefreshListener(this);

        expandableListView = (ExpandableListView) inflate.findViewById(R.id.elv_all_site);
        expandableListView.setGroupIndicator(null);

        allSiteAdapter = new AllSiteAdapter(mAllAreaData);
        expandableListView.setAdapter(allSiteAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            //v : 条目的view对象
            //groupPosition :条目的位置
            //id : 条目的id
            //return : true:表示执行完成
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                //打开关闭条目,打开条目的时候关闭其他条目,同时让当前打开条目置顶
                if (currentExpandGroup == -1) {
                    //打开的自己
                    expandableListView.expandGroup(groupPosition);//打开点击的组条目
                    currentExpandGroup = groupPosition;
                    expandableListView.setSelectedGroup(groupPosition);
                } else {
                    //关闭组,打开其他组
                    //1.打开的是自己,又点击了自己,关闭自己
                    //2.打开的是自己,又点击其他组,关闭自己,打开其他组,通将其他组置顶
                    if (currentExpandGroup == groupPosition) {
                        //关闭自己
                        expandableListView.collapseGroup(groupPosition);
                        currentExpandGroup = -1;
                    } else {
                        //关闭之前打开的组,打开点击的组
                        expandableListView.collapseGroup(currentExpandGroup);
                        //打开点击的组
                        expandableListView.expandGroup(groupPosition);

                        expandableListView.setSelectedGroup(groupPosition);
                        currentExpandGroup = groupPosition;
                    }
                }

                return true;
            }
        });

        return inflate;
    }

    @Override
    protected ContentPage.ResultState onLoad() {

        requestData();

        if (!requestDataIsSuccess) {
            mAllAreaData = null;
        }

        return chat(mAllAreaData);
    }

    private void requestData() {
        OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
        RequestBody formBody = new FormBody.Builder().build();

        String ipAddress = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");

        Request request = new Request.Builder()
                .url(ipAddress + IpFiled.ALL_SITE_DATA)
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
//            InputStream is = getActivity().getAssets().open("textJson2.txt");
//            mResultString = readTextFromSDcard(is);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // TODO 测试数据

        if (StringUtils.interentIsNormal(mResultString)) {
            try {
                JSONObject jsonObject = new JSONObject(mResultString);
                if (jsonObject.has("selectList")) {
                    JSONArray informationList = jsonObject.optJSONArray("selectList");
                    if (mAllAreaData == null) {
                        mAllAreaData = new ArrayList<>();
                    } else {
                        mAllAreaData.clear();
                    }
                    for (int i = 0; i < informationList.length(); i++) {

                        JSONObject singleInformation = informationList.getJSONObject(i);

                        String areaName = singleInformation.optString("areaName");

                        List<FacilityDetail> facilityDetails = new ArrayList<>();
                        int facilitySum = 0;
                        int facilityWarnNumber = 0;// 如果走不进下面的判断就为0
                        if (singleInformation.has("facilitySum") && singleInformation.optInt("facilitySum") > 0) {
                            facilitySum = singleInformation.optInt("facilitySum");
                            facilityWarnNumber = singleInformation.optInt("facilityWarnNumber");
                            JSONArray allSiteData = singleInformation.optJSONArray("facilityDetails");
                            for (int j = 0; j < allSiteData.length(); j++) {
                                JSONObject singleData = allSiteData.getJSONObject(j);
                                int isWarn = -1;
                                if (singleData.has("isWarn")) {
                                    isWarn = singleData.optInt("isWarn");
                                }
                                facilityDetails.add(new FacilityDetail(singleData.optInt("status"), singleData.optDouble("dataValue"), singleData.optInt("facilityType"), isWarn, singleData.optString("siteName"), singleData.optString("areaName")));
                            }
                        }

                        mAllAreaData.add(new AllSiteBean(areaName, facilitySum, facilityWarnNumber, facilityDetails));

                    }
                    requestDataIsSuccess = true;
                    System.out.println();
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
                                allSiteAdapter.notifyDataSetChanged();
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
