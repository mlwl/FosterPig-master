package com.minlu.fosterpig.adapter;


import com.minlu.fosterpig.base.BaseHolder;
import com.minlu.fosterpig.base.MyBaseAdapter;
import com.minlu.fosterpig.bean.AlreadySureWarn;
import com.minlu.fosterpig.holder.SureWarnHolder;

import java.util.List;

/**
 * Created by user on 2016/11/22.
 */
public class MainToAlreadyWarnAdapter extends MyBaseAdapter<AlreadySureWarn> {

    private List<AlreadySureWarn> middleList;

    public MainToAlreadyWarnAdapter(List<AlreadySureWarn> list) {
        super(list);
    }

    @Override
    public BaseHolder getHolder() {
        return new SureWarnHolder();
    }

    @Override
    public List<AlreadySureWarn> onLoadMore() {
        return null;
    }

    @Override
    public boolean hasMore() {
        return false;
    }

}
