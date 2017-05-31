package com.minlu.fosterpig.adapter;

import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.minlu.fosterpig.base.BaseHolder;
import com.minlu.fosterpig.base.MyBaseAdapter;
import com.minlu.fosterpig.holder.VideoAreaListHolder;

import java.util.List;

/**
 * Created by user on 2017/1/18.
 */

public class VideoAreaListAdapter extends MyBaseAdapter<SubResourceNodeBean> {

    public VideoAreaListAdapter(List<SubResourceNodeBean> list) {
        super(list);
    }

    @Override
    public BaseHolder getHolder() {
        return new VideoAreaListHolder();
    }

    @Override
    public List<SubResourceNodeBean> onLoadMore() {
        return null;
    }

    @Override
    public boolean hasMore() {
        return false;
    }
}
