package com.minlu.fosterpig.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.bean.AllSiteBean;
import com.minlu.fosterpig.bean.FacilityDetail;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.List;

/**
 * Created by user on 2016/11/24.
 */
public class AllSiteAdapter extends BaseExpandableListAdapter {

    private List<AllSiteBean> groups;
    private TextView monitorAddress;
    private TextView warnNumber;
    private ImageView imageIcon;
    private ImageView isPowerOn;
//    private ImageView isHandle;

    public AllSiteAdapter(List<AllSiteBean> list) {
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
        return groups.get(groupPosition).getFacilityDetails().size();
    }

    // 根据组的位置获取的组的数据
    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    // 根据组的位置和孩子的位置获取孩子的数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getFacilityDetails().get(childPosition);
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

        View inflate = ViewsUitls.inflate(R.layout.item_all_site_group);
        TextView siteName = (TextView) inflate.findViewById(R.id.tv_all_site_group_site_name);
        TextView siteWarnAllNumber = (TextView) inflate.findViewById(R.id.tv_all_site_group_site_all_number);

        siteName.setText(groups.get(groupPosition).getAreaName());
        siteWarnAllNumber.setText("[ 总数 : " + groups.get(groupPosition).getFacilitySum() + " , 报警 : " + groups.get(groupPosition).getFacilityWarnNumber() + " ]");

        return inflate;
    }

    // 设置孩子的样式
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View inflate = ViewsUitls.inflate(R.layout.item_all_site_child);
        monitorAddress = (TextView) inflate.findViewById(R.id.tv_all_site_child_monitor_address);
        warnNumber = (TextView) inflate.findViewById(R.id.tv_all_site_child_monitor_warn_number);
        imageIcon = (ImageView) inflate.findViewById(R.id.iv_all_site_child_item_left_image);
        isPowerOn = (ImageView) inflate.findViewById(R.id.iv_all_site_child_power_supply_is_open);
//        isHandle = (ImageView) inflate.findViewById(R.id.iv_all_site_child_is_handle);

        FacilityDetail facilityDetail = groups.get(groupPosition).getFacilityDetails().get(childPosition);
        switch (facilityDetail.getFacilityType()) {
            case 1:// 氨气
                isShowPower(false);
                if (facilityDetail.getIsWarn() == 1) {
                    if (facilityDetail.getIsHandle() == 1) {
                        setItemStyle(R.mipmap.small_icon_normal_ammonia, R.color.loading_background_thin_gray, R.color.loading_background_thin_gray,
                                facilityDetail.getDataValue() + "ppm",
                                facilityDetail.getSiteName() + "-氨气");
//                        isHandle.setVisibility(View.VISIBLE);
                    } else {
                        setItemStyle(R.mipmap.small_icon_warn_ammonia, R.color.red, R.color.red,
                                facilityDetail.getDataValue() + "ppm",
                                facilityDetail.getSiteName() + "-氨气");
//                        isHandle.setVisibility(View.GONE);
                    }
                } else {
                    setItemStyle(R.mipmap.small_icon_normal_ammonia, R.color.black, R.color.black,
                            facilityDetail.getDataValue() + "ppm",
                            facilityDetail.getSiteName() + "-氨气");
//                    isHandle.setVisibility(View.GONE);
                }
                break;
            case 2:// 温度
                isShowPower(false);
                if (facilityDetail.getIsWarn() == 1) {
                    if (facilityDetail.getIsHandle() == 1) {
                        setItemStyle(R.mipmap.small_icon_normal_temperature, R.color.loading_background_thin_gray, R.color.loading_background_thin_gray,
                                facilityDetail.getDataValue() + "℃",
                                facilityDetail.getSiteName() + "-温度");
//                        isHandle.setVisibility(View.VISIBLE);
                    } else {
                        setItemStyle(R.mipmap.small_icon_warn_temperature, R.color.red, R.color.red,
                                facilityDetail.getDataValue() + "℃",
                                facilityDetail.getSiteName() + "-温度");
//                        isHandle.setVisibility(View.GONE);
                    }
                } else {
                    setItemStyle(R.mipmap.small_icon_normal_temperature, R.color.black, R.color.black,
                            facilityDetail.getDataValue() + "℃",
                            facilityDetail.getSiteName() + "-温度");
//                    isHandle.setVisibility(View.GONE);
                }
                break;
            case 3:// 湿度
                isShowPower(false);
                if (facilityDetail.getIsWarn() == 1) {
                    if (facilityDetail.getIsHandle() == 1) {
                        setItemStyle(R.mipmap.small_icon_normal_humidity, R.color.loading_background_thin_gray, R.color.loading_background_thin_gray,
                                facilityDetail.getDataValue() + "%",
                                facilityDetail.getSiteName() + "-湿度");
//                        isHandle.setVisibility(View.VISIBLE);
                    } else {
                        setItemStyle(R.mipmap.small_icon_warn_humidity, R.color.red, R.color.red,
                                facilityDetail.getDataValue() + "%",
                                facilityDetail.getSiteName() + "-湿度");
//                        isHandle.setVisibility(View.GONE);
                    }
                } else {
                    setItemStyle(R.mipmap.small_icon_normal_humidity, R.color.black, R.color.black,
                            facilityDetail.getDataValue() + "%",
                            facilityDetail.getSiteName() + "-湿度");
//                    isHandle.setVisibility(View.GONE);
                }
                break;
            default:// 市电
                isShowPower(true);
                if (facilityDetail.getIsWarn() != -1) {
                    monitorAddress.setText(facilityDetail.getSiteName() + "-市电" + (facilityDetail.getFacilityType() - 3));
                } else {
                    monitorAddress.setText("isWarn没有值");
                }
                if (facilityDetail.getIsWarn() == 1) {
                    if (facilityDetail.getIsHandle() == 1) {
                        imageIcon.setImageResource(R.mipmap.small_icon_normal_power_supply);
                        monitorAddress.setTextColor(ContextCompat.getColor(ViewsUitls.getContext(), R.color.loading_background_thin_gray));
                        if (facilityDetail.getDataValue() == 0) {
                            isPowerOn.setImageResource(R.mipmap.broken_link_normal);
                        } else {
                            isPowerOn.setImageResource(R.mipmap.link_normal);
                        }
//                        isHandle.setVisibility(View.VISIBLE);
                    } else {
                        imageIcon.setImageResource(R.mipmap.small_icon_warn_power_supply);
                        monitorAddress.setTextColor(ContextCompat.getColor(ViewsUitls.getContext(), R.color.red));
                        if (facilityDetail.getDataValue() == 0) {
                            isPowerOn.setImageResource(R.mipmap.broken_link_warn);
                        } else {
                            isPowerOn.setImageResource(R.mipmap.link_warn);
                        }
//                        isHandle.setVisibility(View.GONE);
                    }
                } else {
                    imageIcon.setImageResource(R.mipmap.small_icon_normal_power_supply);
                    monitorAddress.setTextColor(ContextCompat.getColor(ViewsUitls.getContext(), R.color.black));
                    if (facilityDetail.getDataValue() == 0) {
                        isPowerOn.setImageResource(R.mipmap.broken_link_normal);
                    } else {
                        isPowerOn.setImageResource(R.mipmap.link_normal);
                    }
//                    isHandle.setVisibility(View.GONE);
                }
                break;
        }


        return inflate;
    }

    private void setItemStyle(int icon, int textColor1, int textColor2, String text1, String text2) {
        imageIcon.setImageResource(icon);
        warnNumber.setText(text1);
        monitorAddress.setText(text2);
        warnNumber.setTextColor(ContextCompat.getColor(ViewsUitls.getContext(), textColor1));
        monitorAddress.setTextColor(ContextCompat.getColor(ViewsUitls.getContext(), textColor2));
    }

    private void isShowPower(boolean isShow) {
        if (isShow) {
            isPowerOn.setVisibility(View.VISIBLE);
            warnNumber.setVisibility(View.GONE);
        } else {
            isPowerOn.setVisibility(View.GONE);
            warnNumber.setVisibility(View.VISIBLE);
        }
    }

    // 设置孩子是否可以点击,false:表示不可点击,true:表示可以点击
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
