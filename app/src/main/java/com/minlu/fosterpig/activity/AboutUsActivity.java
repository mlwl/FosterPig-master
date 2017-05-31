package com.minlu.fosterpig.activity;

import android.view.View;
import android.webkit.WebView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.base.BaseActivity;

/**
 * Created by user on 2016/11/28.
 */
public class AboutUsActivity extends BaseActivity {
    @Override
    public void onCreateContent() {

        getThreeLine().setVisibility(View.GONE);
        setBackVisibility(View.VISIBLE);
        setSettingVisibility(View.GONE);

        getBaseTitle().setText("关于我们");

        WebView webView = (WebView) setContent(R.layout.activity_about_us).findViewById(R.id.web_view);
        webView.loadUrl("file:///android_asset/about_us.html");

    }
}
