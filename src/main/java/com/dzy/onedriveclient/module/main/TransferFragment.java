package com.dzy.onedriveclient.module.main;

import android.util.Log;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;

/**
 * Created by dzysg on 2017/4/2 0002.
 */

public class TransferFragment extends BaseFragment {
    @Override
    protected void initView() {

    }

    @Override
    protected void setupView() {

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

    }
}
