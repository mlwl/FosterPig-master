package com.minlu.fosterpig.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.base.MyApplication;
import com.minlu.fosterpig.fragment.AmendVideoConfigFragment;
import com.minlu.fosterpig.fragment.IsAmendVideoConfigFragment;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.List;


/**
 * Created by user on 2016/11/23.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener, IsAmendVideoConfigFragment.ClickSureListener {

    private ImageView mSwitch;

    @Override
    public void onCreateContent() {

        getThreeLine().setVisibility(View.GONE);
        setBackVisibility(View.VISIBLE);
        setSettingVisibility(View.GONE);

        View view = setContent(R.layout.activity_setting);

        MyApplication.getSaveActivity().add(this);

        initContentView(view);
    }

    private void initContentView(View view) {

        mSwitch = (ImageView) view.findViewById(R.id.iv_setting_switch);
        mSwitch.setOnClickListener(this);
        // TODO 关于我们
//        View aboutUs = view.findViewById(R.id.ll_setting_about_us);
//        aboutUs.setOnClickListener(this);
        View versionInformation = view.findViewById(R.id.ll_setting_version_information);
        versionInformation.setOnClickListener(this);
        View videoConfig = view.findViewById(R.id.ll_setting_video_config);
        videoConfig.setOnClickListener(this);

        Button mLogout = (Button) view.findViewById(R.id.bt_setting_logout);
        mLogout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // TODO 关于我们
//            case R.id.ll_setting_about_us:
//                startActivity(new Intent(ViewsUitls.getContext(), AboutUsActivity.class));
//                break;
            case R.id.ll_setting_version_information:
                startActivity(new Intent(ViewsUitls.getContext(), VersionActivity.class));
                break;
            case R.id.bt_setting_logout:
                if (ViewsUitls.isServiceWork("com.minlu.fosterpig.activity.AlarmServicer")) {
                    stopService(MyApplication.getIntentServicer());
                }
                SharedPreferencesUtil.saveboolean(ViewsUitls.getContext(), StringsFiled.IS_AUTO_LOGIN, false);
                startActivity(new Intent(ViewsUitls.getContext(), LoginActivity.class));
                List<Activity> saveActicity = MyApplication.getSaveActivity();
                for (int i = 0; i < saveActicity.size(); i++) {
                    Activity activity = saveActicity
                            .get(i);
                    activity.finish();
                }

                break;
            case R.id.iv_setting_switch:

                setSwitchServiceWarn();

                break;
            case R.id.ll_setting_video_config:

                IsAmendVideoConfigFragment isAmendVideoConfigFragment = new IsAmendVideoConfigFragment();
                isAmendVideoConfigFragment.show(getSupportFragmentManager(), "isAmendVideoConfigFragment");

                break;
        }

    }

    private void setSwitchServiceWarn() {
        // 点击时先读取状态 此状态就是当前状态boolean
        boolean informWarn = SharedPreferencesUtil.getboolean(
                ViewsUitls.getContext(), StringsFiled.INFORM_WARN, false);

        if (informWarn) {
            // ture为开启状态，所以要关闭服务
            stopService(MyApplication.getIntentServicer());
        } else {
            // 与上面相反
            startService(MyApplication.getIntentServicer());
        }

        // 在设置
        setSwitchImage(informWarn);
        // 在存储状态
        SharedPreferencesUtil.saveboolean(ViewsUitls.getContext(),
                StringsFiled.INFORM_WARN, !informWarn);
    }

    @Override
    public void onStart() {
        super.onStart();

        boolean informWarn = SharedPreferencesUtil.getboolean(
                ViewsUitls.getContext(), StringsFiled.INFORM_WARN, false);
        setSwitchImage(!informWarn);

    }

    /*参数是开关改变前的状态，所以调用此方法如果是为了改变开关就默认使用参数相反的一面，如果不是在传递参数时就直接传相反的参数*/
    public void setSwitchImage(boolean isonoff) {
        isonoff = !isonoff;
        if (isonoff) {
            // 开
            mSwitch.setImageResource(R.mipmap.setting_switch_open);
        } else {
            // 闭
            mSwitch.setImageResource(R.mipmap.setting_switch_close);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MyApplication.getSaveActivity().remove(this);
    }

    @Override
    public void onClickSure(DialogInterface dialog, int id) {
        AmendVideoConfigFragment amendVideoConfigFragment = new AmendVideoConfigFragment();
        amendVideoConfigFragment.show(getSupportFragmentManager(), "amendVideoConfigFragment");
    }
}
