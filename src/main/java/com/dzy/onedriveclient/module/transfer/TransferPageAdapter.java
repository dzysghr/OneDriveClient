package com.dzy.onedriveclient.module.transfer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dzysg on 2017/5/2 0002.
 */

public class TransferPageAdapter extends FragmentPagerAdapter {

    private String[] mTitle = {"下载任务","上传任务"};

    private List<Fragment> mFragments = new ArrayList<>(2);


    public TransferPageAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mTitle.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }
}
