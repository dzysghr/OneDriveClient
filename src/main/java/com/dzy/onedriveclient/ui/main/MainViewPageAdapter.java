package com.dzy.onedriveclient.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class MainViewPageAdapter extends FragmentPagerAdapter {

    static SoftReference<List<Fragment>> mSoftFragments;
    List<Fragment> mFragments;

    public MainViewPageAdapter(FragmentManager fm) {
        super(fm);
        if (mSoftFragments==null)
        {
            createFragment();
        }
        else
        {
            mFragments = mSoftFragments.get();
            if (mFragments==null)
                createFragment();
        }
    }


    private void createFragment()
    {
        mFragments = new ArrayList<>(4);
        mFragments.add(new DriveFragment());
        mFragments.add(new LocalFileFragment());
        mFragments.add(new TransferFragment());
        mFragments.add(new MoreFragment());
        //把四个fragment缓存起来，主题切换时可以不用再重建
        mSoftFragments = new SoftReference<>(mFragments);
        Log.e("tag","createFragment");
    }

    @Override
    public Fragment getItem(int position) {

        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

}
