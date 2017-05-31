package com.minlu.fosterpig.activity;

import android.view.View;
import android.widget.TextView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.util.BaseTools;
import com.minlu.fosterpig.util.ViewsUitls;

/**
 * Created by user on 2016/11/28.
 */
public class VersionActivity extends BaseActivity {
    @Override
    public void onCreateContent() {

        getThreeLine().setVisibility(View.GONE);
        setBackVisibility(View.VISIBLE);
        setSettingVisibility(View.GONE);

        getBaseTitle().setText("版本信息");

        View view = setContent(R.layout.activity_version);

        TextView versions = (TextView) view.findViewById(R.id.tv_versions);
        String versionName = BaseTools.getVersionName(ViewsUitls.getContext());
        versions.setText("版本号: " + versionName);

    }
}
