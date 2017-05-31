package com.minlu.fosterpig.holder;

import android.view.View;
import android.widget.TextView;

import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.base.BaseHolder;
import com.minlu.fosterpig.util.ViewsUitls;

/**
 * Created by user on 2017/1/18.
 */

public class VideoAreaListHolder extends BaseHolder<SubResourceNodeBean> {

    private TextView textView;

    @Override
    public View initView() {

        View inflate = ViewsUitls.inflate(R.layout.item_video_group);
        textView = (TextView) inflate.findViewById(R.id.tv_video_group_area_name);
        return inflate;
    }

    @Override
    public void setRelfshData(SubResourceNodeBean mData, int postion) {
        textView.setText(mData.getName());
    }
}
