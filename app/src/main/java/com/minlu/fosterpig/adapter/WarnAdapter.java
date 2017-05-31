package com.minlu.fosterpig.adapter;


import com.minlu.fosterpig.base.BaseHolder;
import com.minlu.fosterpig.base.MyBaseAdapter;
import com.minlu.fosterpig.bean.MainAllInformation;
import com.minlu.fosterpig.holder.WarnHolder;

import java.util.List;

/**
 * Created by user on 2016/11/22.
 */
public class WarnAdapter extends MyBaseAdapter<MainAllInformation> {

    public WarnAdapter(List<MainAllInformation> list) {
        super(list);
    }

    @Override
    public BaseHolder getHolder() {
        return new WarnHolder();
    }

    @Override
    public List<MainAllInformation> onLoadMore() {
        return null;
    }

    @Override
    public boolean hasMore() {
        return false;
    }
}
