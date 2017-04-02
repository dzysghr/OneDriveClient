package com.dzy.onedriveclient.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dzy.onedriveclient.core.mvp.IBasePresenter;


public abstract class BaseFragment extends Fragment {

    protected abstract void initView();
    protected abstract void setupView();
    protected abstract @LayoutRes int getLayoutId();
    protected abstract IBasePresenter initPresenter();
    protected abstract void LazyLoad();
    protected String TAG = "BaseFragment";
    private IBasePresenter mPresenter;
    private boolean mFirstLoad = true;
    private boolean mViewCreated = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mPresenter = initPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutId(),container,false);
        initView();
        setupView();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewCreated = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        //如果这是第一个fragment，setUserVisibleHint比onCreateView会比先调用，mViewCreated为false，这里判断不会为true
        if (mFirstLoad && isVisibleToUser&&mViewCreated)
        {
            LazyLoad();
            mFirstLoad = false;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) //如果这是第一个fragment,这里结果为true
        {
            mFirstLoad = false;
            LazyLoad();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.unSubscribe();
        }
    }

    public void  startActivity(Class<?> activity){
        Intent i = new Intent(getActivity(),activity);
        startActivity(i);
    }

    public void Toast(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public void closeView(){
        getFragmentManager().popBackStack();
    }

    public void dailog(String msg){
        new AlertDialog.Builder(getContext()).setMessage(msg).create().show();
    }

    public BaseFragment newInstance(){
        return null;
    }
}
