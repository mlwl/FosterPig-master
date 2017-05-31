package com.minlu.fosterpig.fragment;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.adapter.WarnAdapter;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.bean.AlreadySureWarn;
import com.minlu.fosterpig.bean.MainAllInformation;
import com.minlu.fosterpig.customview.swipelistview.SwipeMenu;
import com.minlu.fosterpig.customview.swipelistview.SwipeMenuCreator;
import com.minlu.fosterpig.customview.swipelistview.SwipeMenuItem;
import com.minlu.fosterpig.customview.swipelistview.SwipeMenuListView;
import com.minlu.fosterpig.observer.MySubject;
import com.minlu.fosterpig.request.RequestResult;
import com.minlu.fosterpig.request.RequestSureWarn;
import com.minlu.fosterpig.util.GsonTools;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.SystemTime;
import com.minlu.fosterpig.util.ToastUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/11/22.
 */
public class MainToWarnFragment extends BaseFragment<MainAllInformation> {

    private List<MainAllInformation> mainAllInformations = new ArrayList<>();
    private List<AlreadySureWarn> alreadySureWarns = new ArrayList<>();
    private WarnAdapter mWarnAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
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
        swipeRefreshLayout.setEnabled(false);

        SwipeMenuListView mListView = (SwipeMenuListView) inflate.findViewById(R.id.swipe_menu_list_view);
        mWarnAdapter = new WarnAdapter(mainAllInformations);
        mListView.setAdapter(mWarnAdapter);

        setSwipeMenuAttribute(mListView);

        return inflate;
    }

    @Override
    protected ContentPage.ResultState onLoad() {
        switch (getBundleValue()) {
            case StringsFiled.MAIN_TO_WARN_VALUE_AMMONIA:
                analysisDataJSON(SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_AMMONIA_JSON, ""));
                analysisDataJSONTwo(SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_AMMONIA_JSON, ""));
                break;
            case StringsFiled.MAIN_TO_WARN_VALUE_TEMPERATURE:
                analysisDataJSON(SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_TEMPERATURE_JSON, ""));
                analysisDataJSONTwo(SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_TEMPERATURE_JSON, ""));
                break;
            case StringsFiled.MAIN_TO_WARN_VALUE_HUMIDITY:
                analysisDataJSON(SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_HUMIDITY_JSON, ""));
                analysisDataJSONTwo(SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_HUMIDITY_JSON, ""));
                break;
            case StringsFiled.MAIN_TO_WARN_VALUE_POWER_SUPPLY:
                analysisDataJSON(SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_POWER_SUPPLY_JSON, ""));
                analysisDataJSONTwo(SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_POWER_SUPPLY_JSON, ""));
                break;
        }
        return chat(mainAllInformations);
    }

    private void analysisDataJSON(String jsonData) {
        mainAllInformations.clear();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject singleWarnData = jsonArray.getJSONObject(i);

                double facilityValue = singleWarnData.optDouble("facilityValue");
                int facilityType = singleWarnData.optInt("facilityType");
                int isWarn = singleWarnData.optInt("isWarn");
                int siteId = singleWarnData.optInt("siteId");
                int facilityId = singleWarnData.optInt("facilityId");
                int areaId = singleWarnData.optInt("areaId");
                String siteName = singleWarnData.optString("siteName");
                String facilityName = singleWarnData.optString("facilityName");
                String areaName = singleWarnData.optString("areaName");
                // 开始报警的时间
                String startWarnTime = "---";
                if (singleWarnData.has("startWarnTime")) {
                    String time = singleWarnData.optString("startWarnTime");
                    if (!StringUtils.isEmpty(time)) {
                        startWarnTime = time;
                    }
                }

                int mainId = -1;
                if (singleWarnData.has("mainId")) {
                    mainId = singleWarnData.optInt("mainId");
                }

                mainAllInformations.add(new MainAllInformation(mainId, areaName, siteName, siteId, facilityName, facilityId, areaId, facilityType, facilityValue, isWarn, startWarnTime));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonData);
    }

    private void analysisDataJSONTwo(String jsonData) {
        alreadySureWarns.clear();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject singleAlreadyWarnData = jsonArray.getJSONObject(i);

                double value = singleAlreadyWarnData.optDouble("value");
                int type = singleAlreadyWarnData.optInt("type");
                String stationName = singleAlreadyWarnData.optString("stationName");
                String areaName = singleAlreadyWarnData.optString("areaName");
                // 开始报警的时间
                String alarmTime = "---";
                if (singleAlreadyWarnData.has("alarmTime")) {
                    String time = singleAlreadyWarnData.optString("alarmTime");
                    if (!StringUtils.isEmpty(time)) {
                        alarmTime = time;
                    }
                }
                String handleTime = "---";
                if (singleAlreadyWarnData.has("handleTime")) {
                    String time = singleAlreadyWarnData.optString("handleTime");
                    if (!StringUtils.isEmpty(time)) {
                        handleTime = time;
                    }
                }
                alreadySureWarns.add(new AlreadySureWarn(alarmTime, handleTime, stationName, areaName, type, value));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonData);
    }


    private void setSwipeMenuAttribute(SwipeMenuListView mListView) {
        SwipeMenuCreator creator = createSwipeMenuCreator();
        // set creator
        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        MainAllInformation mainAllInformation = mainAllInformations.get(position);
                        final int facilityType = mainAllInformation.getFacilityType();
                        RequestSureWarn.requestSureWarn(mainAllInformation, mActivity, new RequestResult() {
                            @Override
                            public void onResponse(boolean result) {// 此处是主线程，根据结果进行不同的处理
                                if (result) {
                                    MainAllInformation singleInformation = mainAllInformations.get(position);
                                    alreadySureWarns.add(0, new AlreadySureWarn(singleInformation.getStartWarnTime(),
                                            SystemTime.getSureWarnTime(),
                                            singleInformation.getSiteName(),
                                            singleInformation.getAreaName(),
                                            singleInformation.getFacilityType(),
                                            singleInformation.getFacilityValue()));
                                    mainAllInformations.remove(singleInformation);
                                    mWarnAdapter.notifyDataSetChanged();
                                    ToastUtil.showToast(ViewsUitls.getContext(), "确认报警成功");
                                    // 对存储的数据源进行修改，以及与主界面互动
                                    amendDataSource(facilityType);
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
            }

            @Override
            public void onMenuClose(int position) {
                System.out.println("setOnMenuStateChangeListener+onMenuClose: " + position);
            }
        });
        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                System.out.println("setOnSwipeListener+onSwipeStart: " + position);
            }

            @Override
            public void onSwipeEnd(int position) {
                System.out.println("setOnSwipeListener+onSwipeEnd: " + position);
            }
        });
    }

    private void amendDataSource(int facilityType) {
        switch (facilityType) {
            case 1:// 氨气
                SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_AMMONIA_JSON, GsonTools.createGsonString(mainAllInformations));
                SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_AMMONIA_JSON, GsonTools.createGsonString(alreadySureWarns));
                MySubject.getInstance().operation(StringsFiled.OBSERVER_AMMONIA_SURE, -1, -1);
                break;
            case 2:// 温度
                SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_TEMPERATURE_JSON, GsonTools.createGsonString(mainAllInformations));
                SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_TEMPERATURE_JSON, GsonTools.createGsonString(alreadySureWarns));
                MySubject.getInstance().operation(StringsFiled.OBSERVER_TEMPERATURE_SURE, -1, -1);
                break;
            case 3:// 湿度
                SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_HUMIDITY_JSON, GsonTools.createGsonString(mainAllInformations));
                SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_HUMIDITY_JSON, GsonTools.createGsonString(alreadySureWarns));
                MySubject.getInstance().operation(StringsFiled.OBSERVER_HUMIDITY_SURE, -1, -1);
                break;
            default:// 市电
                SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_WARN_POWER_SUPPLY_JSON, GsonTools.createGsonString(mainAllInformations));
                SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_POWER_SUPPLY_JSON, GsonTools.createGsonString(alreadySureWarns));
                MySubject.getInstance().operation(StringsFiled.OBSERVER_POWER_SUPPLY_SURE, -1, -1);
                break;
        }
    }

    @NonNull
    private SwipeMenuCreator createSwipeMenuCreator() {
        return new SwipeMenuCreator() {

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
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}
