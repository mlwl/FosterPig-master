package com.minlu.fosterpig.activity;

import android.view.View;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.fragment.AllWarnFragment;

/**
 * Created by user on 2016/11/25.
 */
public class NotificationToWarnActivity extends BaseActivity {
    @Override
    public void onCreateContent() {

        getThreeLine().setVisibility(View.GONE);
        setBackVisibility(View.VISIBLE);
        setSettingVisibility(View.GONE);

        getBaseTitle().setText("报警信息");

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_base_content, new AllWarnFragment()).commit();

    }
}
