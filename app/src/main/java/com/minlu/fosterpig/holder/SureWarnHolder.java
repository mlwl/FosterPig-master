package com.minlu.fosterpig.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.base.BaseHolder;
import com.minlu.fosterpig.bean.AlreadySureWarn;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ViewsUitls;

/**
 * Created by user on 2016/11/22.
 */
public class SureWarnHolder extends BaseHolder<AlreadySureWarn> {

    private ImageView mLeftImage;
    private TextView mMonitorAddress;
    private TextView mMonitorWarnTime;
    private TextView mMonitorWarnNumber;
    private TextView mSureWarnTime;
    private ImageView mRightView;

    @Override
    public View initView() {

        View inflate = ViewsUitls.inflate(R.layout.item_sure_warn_list_view);

        mLeftImage = (ImageView) inflate.findViewById(R.id.iv_item_sure_warn_left_image);
        mMonitorAddress = (TextView) inflate.findViewById(R.id.tv_sure_warn_monitor_address);
        mMonitorWarnTime = (TextView) inflate.findViewById(R.id.tv_sure_warn_monitor_warn_time);
        mMonitorWarnNumber = (TextView) inflate.findViewById(R.id.tv_sure_warn_monitor_warn_number);
        mSureWarnTime = (TextView) inflate.findViewById(R.id.tv_sure_warn_time);
        mRightView = (ImageView) inflate.findViewById(R.id.iv_item_sure_warn_right_image);

        return inflate;
    }

    @Override
    public void setRelfshData(AlreadySureWarn mData, int postion) {

        String alarmTime = mData.getAlarmTime();
        if (alarmTime.length() > 2) {
            alarmTime = alarmTime.substring(2, alarmTime.length());
        }
        if (StringUtils.isEmpty(alarmTime)) {
            mMonitorWarnTime.setText("报警:---");
        } else {
            mMonitorWarnTime.setText("报警:" + alarmTime);
        }
        String handleTime = mData.getHandleTime();
        if (handleTime.length() > 2) {
            handleTime = handleTime.substring(2, handleTime.length());
        }
        if (StringUtils.isEmpty(handleTime)) {
            mSureWarnTime.setText("确认:---");
        } else {
            mSureWarnTime.setText("确认:" + handleTime);
        }

        String facilityName = "";
        int imageResource = R.mipmap.ic_launcher;
        switch (mData.getType()) {
            case 1:
                setIsShowRightImage(false);
                facilityName = "氨气";
                imageResource = R.mipmap.small_icon_normal_ammonia;
                mMonitorWarnNumber.setText(mData.getValue() + "ppm");
                break;
            case 2:
                setIsShowRightImage(false);
                facilityName = "温度";
                imageResource = R.mipmap.small_icon_normal_temperature;
                mMonitorWarnNumber.setText(mData.getValue() + "℃");
                break;
            case 3:
                setIsShowRightImage(false);
                facilityName = "湿度";
                imageResource = R.mipmap.small_icon_normal_humidity;
                mMonitorWarnNumber.setText(mData.getValue() + "%");
                break;
            default:
                facilityName = "市电" + (mData.getType() - 3);
                imageResource = R.mipmap.small_icon_normal_power_supply;
                setIsShowRightImage(true);
                if (mData.getValue() == 0) {
                    mRightView.setImageResource(R.mipmap.broken_link_warn);
                }
                if (mData.getValue() == 1) {
                    mRightView.setImageResource(R.mipmap.link_normal);
                }
                break;
        }
        mMonitorAddress.setText(mData.getAreaName() + "-" + mData.getStationName() + "-" + facilityName);
        mLeftImage.setImageResource(imageResource);
    }

    private void setIsShowRightImage(boolean isShowRightImage) {
        if (isShowRightImage) {
            mMonitorWarnNumber.setVisibility(View.GONE);
            mRightView.setVisibility(View.VISIBLE);
        } else {
            mMonitorWarnNumber.setVisibility(View.VISIBLE);
            mRightView.setVisibility(View.GONE);
        }
    }
}
