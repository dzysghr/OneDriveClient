package com.dzy.onedriveclient.module.transfer;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dzysg on 2017/4/2 0002.
 */

public class TransferFragment extends BaseFragment {


    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private DownloadFragment mDownloadFragment;

    @Override
    protected void initView() {
        mTabLayout = bindView(R.id.tabs);
        mViewPager = bindView(R.id.viewPager);
    }

    @Override
    protected void setupView() {
        mFragments = new ArrayList<>();
        mFragments.add(mDownloadFragment = new DownloadFragment());
        mFragments.add(new UploadFragment());
        mViewPager.setAdapter(new TransferPageAdapter(getChildFragmentManager(),mFragments));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_transfer;
    }

    @Override
    protected IBasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void LazyLoad() {
        Log.e(TAG, "LazyLoad: transfer");
        mDownloadFragment.LazyLoad();
    }
}
