package com.minlu.fosterpig.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.activity.VideoActivity;
import com.minlu.fosterpig.adapter.VideoAdapter;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.bean.SiteVideo;
import com.minlu.fosterpig.bean.VideoBean;
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
 * Created by user on 2016/12/27.
 */

public class VideoFragment extends BaseFragment<VideoBean> {

    private List<VideoBean> mVideoData;
    private ExpandableListView expandableListView;
    private int currentExpandGroup = -1;
    private boolean requestDataIsSuccess;
    private String mResultString;

    @Override
    protected void onSubClassOnCreateView() {
        loadDataAndRefresh();
    }

    @Override
    protected View onCreateSuccessView() {

        View inflate = ViewsUitls.inflate(R.layout.layout_video);

        expandableListView = (ExpandableListView) inflate.findViewById(R.id.elv_video);
        expandableListView.setGroupIndicator(null);

        VideoAdapter videoAdapter = new VideoAdapter(mVideoData);
        expandableListView.setAdapter(videoAdapter);

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

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                SiteVideo siteVideo = mVideoData.get(groupPosition).getSiteVideos().get(childPosition);
                Intent intent = new Intent(ViewsUitls.getContext(), VideoActivity.class);
                intent.putExtra(StringsFiled.VIDEO_IP, siteVideo.getVideoIP());
                intent.putExtra(StringsFiled.VIDEO_USER, siteVideo.getVideoUser());
                intent.putExtra(StringsFiled.VIDEO_PASSWORD, siteVideo.getVideoPassWord());
                intent.putExtra(StringsFiled.VIDEO_PORT, siteVideo.getVideoPort());
                intent.putExtra(StringsFiled.VIDEO_CHANNEL_NUMBER, siteVideo.getVideoChannelNumber());
                intent.putExtra(StringsFiled.VIDEO_ID, siteVideo.getId());
                startActivity(intent);

                // 如果点击事件有具体的处理，那就返回true给其他代码一个准确的判断
                return false;
            }
        });


        return inflate;
    }

    @Override
    protected ContentPage.ResultState onLoad() {

        requestData();

        if (!requestDataIsSuccess) {
            mVideoData = null;
        }

        return chat(mVideoData);
    }

    private void requestData() {
        OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
        RequestBody formBody = new FormBody.Builder().build();

        String ipAddress = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");

        Request request = new Request.Builder()
                .url(ipAddress + IpFiled.VIDEO_LIST_DATA)
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
            System.out.println();
            e.printStackTrace();
            System.out.println("=========================onFailure=============================");
            Log.i("okHttp_ERROE", "okHttp is request error");
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

    private void analysisJsonDate() {

        // TODO 测试数据
//        try {
//            InputStream is = getActivity().getAssets().open("textJson6.txt");
//            mResultString = readTextFromSDcard(is);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // TODO 测试数据

        if (StringUtils.interentIsNormal(mResultString)) {
            try {
                JSONObject jsonObject = new JSONObject(mResultString);
                if (jsonObject.has("nvrList")) {
                    JSONArray allAreaData = jsonObject.optJSONArray("nvrList");
                    if (mVideoData == null) {
                        mVideoData = new ArrayList<>();
                    } else {
                        mVideoData.clear();
                    }
                    for (int i = 0; i < allAreaData.length(); i++) {
                        JSONObject singleAreaData = allAreaData.getJSONObject(i);
                        String areaName = singleAreaData.optString("areaName");
                        JSONArray allSiteData = singleAreaData.optJSONArray("siteVideos");

                        List<SiteVideo> siteVideos = new ArrayList<>();
                        for (int j = 0; j < allSiteData.length(); j++) {
                            JSONObject singleSiteData = allSiteData.getJSONObject(j);
                            String siteName = singleSiteData.optString("siteName");
                            String videoUser = singleSiteData.optString("videoUser");
                            String videoIP = singleSiteData.optString("videoIP");
                            String videoPassWord = singleSiteData.optString("videoPassWord");
                            int id = singleSiteData.optInt("id");
                            int videoPort = singleSiteData.optInt("videoPort");
                            int videoChannelNumber = singleSiteData.optInt("videoChannelNumber");
                            siteVideos.add(new SiteVideo(id, siteName, videoIP, videoUser, videoPassWord, videoPort, videoChannelNumber));
                        }
                        mVideoData.add(new VideoBean(areaName, siteVideos));
                    }
                    requestDataIsSuccess = true;
                } else {
                    requestDataIsSuccess = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            requestDataIsSuccess = false;
        }
    }
}
