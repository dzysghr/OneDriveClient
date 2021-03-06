package com.dzy.onedriveclient.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.core.mvp.IBaseVIew;


public abstract class BaseFragment extends Fragment {

    protected abstract void initView();
    protected abstract void setupView();
    protected abstract @LayoutRes int getLayoutId();
    protected abstract IBasePresenter initPresenter();
    protected abstract void LazyLoad();
    protected String TAG = "BaseFragment";
    private IBasePresenter mBasePresenter;
    private boolean mFirstLoad = true;
    private boolean mViewCreated = false;
    private View mParent;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        IBasePresenter presenter = initPresenter();
        if (presenter!=null){
            mBasePresenter = presenter;
            if (this instanceof IBaseVIew){
                mBasePresenter.attachView((IBaseVIew) this);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutId(),container,false);
        mParent = v;
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //如果这是第一个fragment，setUserVisibleHint比onCreateView会比先调用，mViewCreated为false，这里判断不会为true
        if (mFirstLoad && isVisibleToUser&&mViewCreated) {
            LazyLoad();
            mFirstLoad = false;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            LazyLoad();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()){ //如果这是第一个fragment,这里结果为true
            mFirstLoad = false;
            LazyLoad();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBasePresenter!=null){
            mBasePresenter.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBasePresenter !=null){
            mBasePresenter.unSubscribe();
        }
    }

    public void  startActivity(Class<?> activity){
        Intent i = new Intent(mContext,activity);
        startActivity(i);
    }

    public IBasePresenter getBasePresenter() {
        return mBasePresenter;
    }

    public void setBasePresenter(IBasePresenter basePresenter) {
        mBasePresenter = basePresenter;
    }

    public void Toast(String msg){
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }

    public void close(){
        getFragmentManager().popBackStack();
    }

    public void dialog(String msg){
        // TODO: 2017/4/17 0017 空指针
        new AlertDialog.Builder(mContext).setMessage(msg).create().show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        Log.d(TAG, "onAttach: "+context);
    }

    public <T> T bindView(@IdRes int id){
        Object o = mParent.findViewById(id);
        if (o==null){
            throw new NullPointerException("you are binding a wrong viewId");
        }
        return (T)o;
    }

    public BaseFragment newInstance(){
        return null;
    }

    public boolean onBackPressed(){
        return false;
    }

    public void showProgress(){

    }

    public void hideProgress(){

    }

}
