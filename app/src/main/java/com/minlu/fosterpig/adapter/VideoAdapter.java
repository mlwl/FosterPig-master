package com.minlu.fosterpig.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.bean.VideoBean;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.List;

/**
 * Created by user on 2016/11/24.
 */
public class VideoAdapter extends BaseExpandableListAdapter {

    private List<VideoBean> groups;

    public VideoAdapter(List<VideoBean> list) {
        this.groups = list;
    }

    // 设置组的个数
    @Override
    public int getGroupCount() {
        return groups.size();
    }

    // 设置孩子的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getSiteVideos().size();
    }

    // 根据组的位置获取的组的数据
    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    // 根据组的位置和孩子的位置获取孩子的数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getSiteVideos().get(childPosition);
    }

    // 获取组的id
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // 获取孩子的id
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // 判断id是否稳定,如果你返回id,返回false,没有返回id,返回true
    @Override
    public boolean hasStableIds() {
        return false;
    }

    // 设置组的样式
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View inflate = ViewsUitls.inflate(R.layout.item_video_group);
        TextView textView = (TextView) inflate.findViewById(R.id.tv_video_group_area_name);
        textView.setText(groups.get(groupPosition).getAreaName());
        return inflate;
    }

    // 设置孩子的样式
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View inflate = ViewsUitls.inflate(R.layout.item_video_child);
        TextView textView = (TextView) inflate.findViewById(R.id.tv_video_child_site_name);
        textView.setText(groups.get(groupPosition).getSiteVideos().get(childPosition).getSiteName());
        return inflate;
    }

    // 设置孩子是否可以点击,false:表示不可点击,true:表示可以点击
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
