package com.minlu.fosterpig.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.customview.MyLinearLayout;
import com.minlu.fosterpig.util.StringUtils;


/**
 * Created by user on 2016/11/21.
 */
public abstract class BaseActivity extends FragmentActivity {

    private ImageView mBaseThreeLine;
    private MyLinearLayout mMyLinearLayout;
    private ImageView mBaseThreePoint;
    private ImageView mBaseFourFrame;

    private TextView mBaseTitle;

    private FrameLayout mBaseContetn;
    public Bundle savedInstanceState;
    public String stringTitle;
    private FrameLayout mBaseLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);

        this.savedInstanceState = savedInstanceState;

        initView();

        setTitleText();

        onCreateContent();
    }

    private void setTitleText() {
        Intent intentTitle = getIntent();
        if (intentTitle != null) {
            stringTitle = intentTitle.getStringExtra(StringsFiled.ACTIVITY_TITLE);
            if (!StringUtils.isEmpty(stringTitle)) {
                mBaseTitle.setText(stringTitle);
            }
        }
    }

    public abstract void onCreateContent();

    private void initView() {
        mBaseThreeLine = (ImageView) findViewById(R.id.iv_title_three_line);
        mBaseThreePoint = (ImageView) findViewById(R.id.iv_title_three_point);

        mBaseFourFrame = (ImageView) findViewById(R.id.iv_title_four_frame);

        mBaseTitle = (TextView) findViewById(R.id.tv_title_text);
        mBaseContetn = (FrameLayout) findViewById(R.id.fl_base_content);

        mMyLinearLayout = (MyLinearLayout) findViewById(R.id.my_linear_layout);
        mBaseFourFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBaseLoading = (FrameLayout) findViewById(R.id.fl_loading);
    }

    public void setLoadingVisibility(int visibility) {
        mBaseLoading.setVisibility(visibility);
    }

    public void setIsInterruptTouch(boolean is) {
        mMyLinearLayout.setIsInterruptTouch(is);
    }

    public void setBackVisibility(int visibility) {
        mBaseFourFrame.setVisibility(visibility);
    }

    public void setTitleVisibility(int visibility) {
        mBaseTitle.setVisibility(visibility);
    }

    public void setSettingVisibility(int visibility) {
        mBaseThreePoint.setVisibility(visibility);
    }

    public ImageView getThreeLine() {
        return mBaseThreeLine;
    }

    public ImageView getThreePoint() {
        return mBaseThreePoint;
    }

    public TextView getBaseTitle() {
        return mBaseTitle;
    }

    /**
     * 首先将一个xml布局打气压缩成一个View，在将该View添加到Framelayout中
     */
    public View setContent(int id) {
        View inflate = View.inflate(MyApplication.getContext(), id, null);
        mBaseContetn.addView(inflate);
        return inflate;
    }

}
