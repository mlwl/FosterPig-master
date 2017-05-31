package com.minlu.fosterpig.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.base.BaseHolder;
import com.minlu.fosterpig.bean.MainAllInformation;
import com.minlu.fosterpig.util.ViewsUitls;

/**
 * Created by user on 2016/11/22.
 */
public class WarnHolder extends BaseHolder<MainAllInformation> {

    private ImageView mLeftImage;
    private TextView mMonitorAddress;
    private TextView mMonitorWarnTime;
    private TextView mMonitorWarnNumber;
    private ImageView mBrokenLink;

    @Override
    public View initView() {

        View inflate = ViewsUitls.inflate(R.layout.item_swipe_menu_list_view);

        mLeftImage = (ImageView) inflate.findViewById(R.id.iv_item_left_image);
        mMonitorAddress = (TextView) inflate.findViewById(R.id.tv_monitor_address);
        mMonitorWarnTime = (TextView) inflate.findViewById(R.id.tv_monitor_warn_time);

        mMonitorWarnNumber = (TextView) inflate.findViewById(R.id.tv_monitor_warn_number);
        mBrokenLink = (ImageView) inflate.findViewById(R.id.iv_power_supply);

        return inflate;
    }

    private void isShowPowerSupply(boolean isShow) {
        if (isShow) {
            mMonitorWarnNumber.setVisibility(View.GONE);
            mBrokenLink.setVisibility(View.VISIBLE);
        } else {
            mMonitorWarnNumber.setVisibility(View.VISIBLE);
            mBrokenLink.setVisibility(View.GONE);
        }
    }

    @Override
    public void setRelfshData(MainAllInformation mData, int postion) {

        mMonitorWarnTime.setText("报警时间:" + mData.getStartWarnTime());

        switch (mData.getFacilityType()) {
            case 1:// 1氨气 a
                isShowPowerSupply(false);
                mLeftImage.setImageResource(R.mipmap.small_icon_warn_ammonia);
                mMonitorWarnNumber.setText(mData.getFacilityValue() + "ppm");
                mMonitorAddress.setText(mData.getAreaName() + "-" + mData.getSiteName() + "-氨气");
                break;
            case 2:// 2温度 t
                isShowPowerSupply(false);
                mLeftImage.setImageResource(R.mipmap.small_icon_warn_temperature);
                mMonitorWarnNumber.setText(mData.getFacilityValue() + "℃");
                mMonitorAddress.setText(mData.getAreaName() + "-" + mData.getSiteName() + "-温度");
                break;
            case 3:// 3湿度 h
                isShowPowerSupply(false);
                mLeftImage.setImageResource(R.mipmap.small_icon_warn_humidity);
                mMonitorWarnNumber.setText(mData.getFacilityValue() + "%");
                mMonitorAddress.setText(mData.getAreaName() + "-" + mData.getSiteName() + "-湿度");
                break;
            default:// 市电 p
                isShowPowerSupply(true);
                mLeftImage.setImageResource(R.mipmap.small_icon_warn_power_supply);
                mMonitorAddress.setText(mData.getAreaName() + "-" + mData.getSiteName() + "-市电" + (mData.getFacilityType() - 3));
                if (mData.getFacilityValue() == 0) {
                    mBrokenLink.setImageResource(R.mipmap.broken_link_warn);
                } else {
                    mBrokenLink.setImageResource(R.mipmap.link_normal);
                }
                break;
        }
    }
}
