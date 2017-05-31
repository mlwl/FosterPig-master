package com.minlu.fosterpig.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.minlu.fosterpig.FragmentFactory;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.fragment.MainToAlreadyWarnFragment;
import com.minlu.fosterpig.fragment.MainToWarnFragment;
import com.minlu.fosterpig.util.ViewsUitls;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by user on 2016/11/22.
 */
public class WarnActivity extends BaseActivity implements View.OnClickListener {

    private List<TextView> mTextViews;
    private TextView mNoSure;
    private TextView mAlreadySure;
    private MainToWarnFragment mNoSureFragment;
    private MainToAlreadyWarnFragment mSureWarnFragment;

    @Override
    public void onCreateContent() {

        getThreeLine().setVisibility(View.GONE);
        setBackVisibility(View.VISIBLE);
        setSettingVisibility(View.GONE);

        View view = setContent(R.layout.activity_warn_sure_or_no);
        initContentView(view);
        showFragmentWhenFirstOrSecond();

/*      // 添加tab前的代码
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        switch (getIntent().getStringExtra(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY)) {
            case StringsFiled.MAIN_TO_WARN_AMMONIA:
                bundle.putInt(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY, StringsFiled.MAIN_TO_WARN_VALUE_AMMONIA);
                break;
            case StringsFiled.MAIN_TO_WARN_TEMPERATURE:
                bundle.putInt(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY, StringsFiled.MAIN_TO_WARN_VALUE_TEMPERATURE);
                break;
            case StringsFiled.MAIN_TO_WARN_HUMIDITY:
                bundle.putInt(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY, StringsFiled.MAIN_TO_WARN_VALUE_HUMIDITY);
                break;
            case StringsFiled.MAIN_TO_WARN_POWER_SUPPLY:
                bundle.putInt(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY, StringsFiled.MAIN_TO_WARN_VALUE_POWER_SUPPLY);
                break;
        }
        MainToWarnFragment mainToWarnFragment = new MainToWarnFragment();
        mainToWarnFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_base_content, mainToWarnFragment);
        fragmentTransaction.commit();*/
    }

    private void showFragmentWhenFirstOrSecond() {
        if (savedInstanceState == null) {// 第一次进入该界面，需要通过add来进行展示Fragment
            System.out.println("第一次进入该界面，需要通过add来进行展示Fragment");
            // mFromFragment为空的情况下
            tabSelectFragment(1, StringsFiled.TAG_OPEN_NO_SURE_FRAGMENT);
        } else {
            System.out.println("第二次+++++++++++++++++++++++++++++");
            mNoSureFragment = (MainToWarnFragment) getSupportFragmentManager().findFragmentByTag(StringsFiled.TAG_OPEN_NO_SURE_FRAGMENT);
            mSureWarnFragment = (MainToAlreadyWarnFragment) getSupportFragmentManager().findFragmentByTag(StringsFiled.TAG_OPEN_ALREADY_SURE_FRAGMENT);

            // 给工厂添加所有实例
            if (mNoSureFragment != null) {
                System.out.println("mNoSureFragment");
                FragmentFactory.fragments[3] = mNoSureFragment;
            } else {
                FragmentFactory.fragments[3] = new MainToWarnFragment();
            }

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if (FragmentFactory.fragments[3].isAdded()) {
                System.out.println("被add过了");
                FragmentTransaction transaction = fragmentTransaction.show(FragmentFactory.fragments[3]);
                isHide(transaction);
            } else {
                System.out.println("没有add过");
                FragmentTransaction transaction = fragmentTransaction.add(R.id.fl_select_sure_or_no, FragmentFactory.fragments[3], StringsFiled.TAG_OPEN_NO_SURE_FRAGMENT);
                isHide(transaction);
            }

            mFromFragment = FragmentFactory.fragments[3];
            alreadyPress = 1; // 防止再次点击所有站点的Tab

            fragmentTransaction.commit();
        }

    }

    private void isHide(FragmentTransaction transaction) {
        if (mSureWarnFragment != null) {
            FragmentFactory.fragments[4] = mSureWarnFragment;
            System.out.println("mSureWarnFragment");
            transaction.hide(FragmentFactory.fragments[4]);
        } else {
            FragmentFactory.fragments[4] = new MainToAlreadyWarnFragment();
        }
    }

    private void tabSelectFragment(int toFragment, String tag) {
        switch (getIntent().getStringExtra(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY)) {
            case StringsFiled.MAIN_TO_WARN_AMMONIA:
                System.out.println("tabSelectFragment=======================氨气");
                amendClickStyle(StringsFiled.MAIN_TO_WARN_VALUE_AMMONIA, toFragment, tag);
                break;
            case StringsFiled.MAIN_TO_WARN_TEMPERATURE:
                System.out.println("tabSelectFragment=======================温度");
                amendClickStyle(StringsFiled.MAIN_TO_WARN_VALUE_TEMPERATURE, toFragment, tag);
                break;
            case StringsFiled.MAIN_TO_WARN_HUMIDITY:
                System.out.println("tabSelectFragment=======================湿度");
                amendClickStyle(StringsFiled.MAIN_TO_WARN_VALUE_HUMIDITY, toFragment, tag);
                break;
            case StringsFiled.MAIN_TO_WARN_POWER_SUPPLY:
                System.out.println("tabSelectFragment=======================市电");
                amendClickStyle(StringsFiled.MAIN_TO_WARN_VALUE_POWER_SUPPLY, toFragment, tag);
                break;
        }
    }

    private void initContentView(View view) {
        mTextViews = new ArrayList<>();
        mNoSure = (TextView) view.findViewById(R.id.tv_tab_no_sure);
        mNoSure.setOnClickListener(this);
        mTextViews.add(mNoSure);
        mAlreadySure = (TextView) view.findViewById(R.id.tv_tab_already_sure);
        mAlreadySure.setOnClickListener(this);
        mTextViews.add(mAlreadySure);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tab_no_sure:
                tabSelectFragment(1, StringsFiled.TAG_OPEN_NO_SURE_FRAGMENT);
                break;
            case R.id.tv_tab_already_sure:
                tabSelectFragment(2, StringsFiled.TAG_OPEN_ALREADY_SURE_FRAGMENT);
                break;
        }

    }

    private int alreadyPress = 0;

    /*
    * bundleValue:开启Fragment时需要通过bundle传递的数据
    * tag:开启fragment时的标记
    * toFragment:从集合中取出TextView时的判断条件 以及从工厂里取出对象的角标
    * */
    private void amendClickStyle(int bundleValue, int toFragment, String tag) {
        if (toFragment != alreadyPress) {
            alreadyPress = toFragment;
            for (int i = 1; i < mTextViews.size() + 1; i++) {
                TextView view = mTextViews.get(i - 1);
                if (i == toFragment) {
                    view.setTextColor(ContextCompat.getColor(ViewsUitls.getContext(), R.color.thin_blue));
                    view.setBackgroundResource(R.drawable.shape_select_tap_background_white);
                    view.setTextAppearance(ViewsUitls.getContext(), R.style.text_blod);
                } else {
                    view.setTextColor(ContextCompat.getColor(ViewsUitls.getContext(), R.color.white));
                    view.setBackgroundResource(R.drawable.shape_select_tap_background_blue);
                    view.setTextAppearance(ViewsUitls.getContext(), R.style.text_normal);
                }
            }
            toFragment += 3;
            startSelectAreaContent(bundleValue, toFragment, tag);
        }

    }

    /*被选中的Fragment*/
    private Fragment mFromFragment = null;

    private void startSelectAreaContent(int bundleValue, int toFragment, String tag) {

        Fragment fragment = FragmentFactory.create(toFragment - 1);

        System.out.println("第" + toFragment + "个Tab");

        if (mFromFragment != fragment) {
            System.out.println("与上一个Tab不一样");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (!fragment.isAdded()) {    // 先判断是否被add过
                System.out.println("需要显示的tab没有被add");
                // 设置要传送的数据
                Bundle bundle = new Bundle();
                bundle.putInt(StringsFiled.OPEN_FRAGMENT_BUNDLE_KEY, bundleValue);
                fragment.setArguments(bundle);
                System.out.println("bundleValue=====" + bundleValue);

                if (mFromFragment == null) {
                    System.out.println("当前tab为空");
                    transaction.add(R.id.fl_select_sure_or_no, fragment, tag).commit(); // 第一次进入本页面，直接add到Activity中
                } else {
                    System.out.println("当前的tab不为空");
                    System.out.println("tag=========" + tag);
                    transaction.hide(mFromFragment).add(R.id.fl_select_sure_or_no, fragment, tag).commit(); // 隐藏当前的fragment，add下一个到Activity中
                }

            } else {
                System.out.println("需要显示的tab已经被add");
                transaction.hide(mFromFragment).show(fragment).commit(); // 隐藏当前的fragment，显示下一个
            }

            mFromFragment = fragment;
        }

    }
}
