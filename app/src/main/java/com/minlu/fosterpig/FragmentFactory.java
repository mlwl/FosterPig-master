package com.minlu.fosterpig;

import android.support.v4.app.Fragment;

import com.minlu.fosterpig.fragment.AllSiteFragment;
import com.minlu.fosterpig.fragment.AllWarnFragment;
import com.minlu.fosterpig.fragment.MainToAlreadyWarnFragment;
import com.minlu.fosterpig.fragment.MainToWarnFragment;
import com.minlu.fosterpig.fragment.SureWarnFragment;
import com.minlu.fosterpig.fragment.VideoListFragment;


public class FragmentFactory {

    /*存储工长要造的对象的仓库*/
    public static Fragment[] fragments = new Fragment[6];

    public static Fragment create(int position) {
        Fragment fragment = null;

        /*仓库里对应位置的对象为空才需要重新创造*/
        if (fragments[position] == null) {

            switch (position) {
                case 0:
                    fragment = new AllSiteFragment();
                    break;
                case 1:
                    fragment = new AllWarnFragment();
                    break;
                case 2:
                    fragment = new SureWarnFragment();
                    break;
                case 3:
                    fragment = new MainToWarnFragment();
                    break;
                case 4:
                    fragment = new MainToAlreadyWarnFragment();
                    break;
                case 5:
                    fragment = new VideoListFragment();
                    break;
            }

            fragments[position] = fragment;

            return fragment;
        } else {
            System.out.println("已经存在");
            return fragments[position];
        }
    }

}

