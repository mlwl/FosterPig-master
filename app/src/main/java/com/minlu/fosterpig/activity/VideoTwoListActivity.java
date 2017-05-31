package com.minlu.fosterpig.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.HttpConstants;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.adapter.VideoAreaListAdapter;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.util.ToastUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by user on 2017/1/18.
 */

public class VideoTwoListActivity extends BaseActivity {


    private View mLoading;
    private View mEmpty;
    private View mError;
    private ListView mListView;

    private static final int LOADING_LAYOUT = 1;
    private static final int EMPTY_LAYOUT = 2;
    private static final int ERROR_LAYOUT = 3;
    private static final int LIST_VIEW_LAYOUT = 4;
    private List<SubResourceNodeBean> areaData = new ArrayList<>();
    private VideoAreaListAdapter videoAreaListAdapter;

    private MyHandler myHandler;
    private int parentId;
    private int parentNodeType;


    static class MyHandler extends Handler {
        WeakReference<VideoTwoListActivity> mVideoListFragment;

        MyHandler(VideoTwoListActivity videoListFragment) {
            mVideoListFragment = new WeakReference<>(videoListFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoTwoListActivity videoListFragment = mVideoListFragment.get();
            switch (msg.what) {
                case LOADING_LAYOUT:
                    videoListFragment.showWhichLayout(LOADING_LAYOUT);
                    break;
                case EMPTY_LAYOUT:
                    videoListFragment.showWhichLayout(EMPTY_LAYOUT);
                    break;
                case ERROR_LAYOUT:
                    videoListFragment.showWhichLayout(ERROR_LAYOUT);
                    break;
                case LIST_VIEW_LAYOUT:
                    videoListFragment.showWhichLayout(LIST_VIEW_LAYOUT);
                    videoListFragment.videoAreaListAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void onCreateContent() {
        getThreeLine().setVisibility(View.GONE);
        setBackVisibility(View.VISIBLE);
        setSettingVisibility(View.GONE);

        Intent intent = getIntent();

        parentNodeType = intent.getIntExtra(StringsFiled.PARENT_NODE_TYPE, -1);
        parentId = intent.getIntExtra(StringsFiled.PARENT_ID, -1);

        View inflate = setContent(R.layout.activity_video_list);

        mLoading = inflate.findViewById(R.id.video_list_loading);
        mEmpty = inflate.findViewById(R.id.video_list_empty);
        mError = inflate.findViewById(R.id.video_list_error);
        mError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWhichLayout(LOADING_LAYOUT);
                getSubResourceList(parentNodeType, parentId);
            }
        });

        mListView = (ListView) inflate.findViewById(R.id.video_list_lv);
        videoAreaListAdapter = new VideoAreaListAdapter(areaData);
        mListView.setAdapter(videoAreaListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubResourceNodeBean subResourceNodeBean = areaData.get(position);
                if (HttpConstants.NodeType.TYPE_CAMERA_OR_DOOR == subResourceNodeBean.getNodeType()) {// 被点击的条目是摄像所在站点名称
                    Camera camera = VMSNetSDK.getInstance().initCameraInfo(subResourceNodeBean);
                    if (VMSNetSDK.getInstance().isHasLivePermission(camera)) {// 判断camera是否是有权限
                        Intent intent = new Intent(VideoTwoListActivity.this, LiveActivity.class);
                        intent.putExtra(StringsFiled.CAMERA_INFORMATION, camera);
                        startActivity(intent);
                    } else {
                        ToastUtil.showToast(ViewsUitls.getContext(), "摄像头无权限打开");
                    }
                }
            }
        });

        myHandler = new MyHandler(this);

        showWhichLayout(LOADING_LAYOUT);

        getSubResourceList(parentNodeType, parentId);
    }

    /**
     * 获取控制中心下的区域信息或者区域下的站点信息
     */
    private void getSubResourceList(int parentNodeType, int pId) {
        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof List<?>) {
                    areaData.clear();
                    areaData.addAll((Collection<? extends SubResourceNodeBean>) obj);
                    if (areaData.size() == 0) {
                        myHandler.sendEmptyMessage(EMPTY_LAYOUT);
                    } else {
                        myHandler.sendEmptyMessage(LIST_VIEW_LAYOUT);
                    }
                } else {
                    myHandler.sendEmptyMessage(ERROR_LAYOUT);
                }
            }

            @Override
            public void loading() {
                System.out.println();
                super.loading();
            }

            @Override
            public void onFailure() {
                System.out.println();
                super.onFailure();
                myHandler.sendEmptyMessage(ERROR_LAYOUT);
            }
        });
        // 参数一:当前页  参数二:每页数量  参数三:系统类型  1-视频 2-门禁 3-连锁  参数四:上级类型  参数五:上级id
        VMSNetSDK.getInstance().getSubResourceList(1, 15, HttpConstants.SysType.TYPE_VIDEO, parentNodeType, pId + "");
    }

    private void showWhichLayout(int which) {
        switch (which) {
            case LOADING_LAYOUT:
                mLoading.setVisibility(View.VISIBLE);
                mEmpty.setVisibility(View.GONE);
                mError.setVisibility(View.GONE);
                mListView.setVisibility(View.INVISIBLE);
                break;
            case EMPTY_LAYOUT:
                mLoading.setVisibility(View.GONE);
                mEmpty.setVisibility(View.VISIBLE);
                mError.setVisibility(View.GONE);
                mListView.setVisibility(View.INVISIBLE);
                break;
            case ERROR_LAYOUT:
                mLoading.setVisibility(View.GONE);
                mEmpty.setVisibility(View.GONE);
                mError.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.INVISIBLE);
                break;
            case LIST_VIEW_LAYOUT:
                mLoading.setVisibility(View.GONE);
                mEmpty.setVisibility(View.GONE);
                mError.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
}
