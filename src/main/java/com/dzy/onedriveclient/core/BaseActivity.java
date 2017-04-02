package com.dzy.onedriveclient.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dzy.onedriveclient.core.mvp.IBasePresenter;

/**
 * Created by dzysg on 2017/4/1 0001.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract void initView();
    protected abstract void setupView();
    protected abstract @LayoutRes int getLayoutId();
    protected abstract IBasePresenter initPresenter();
    protected String TAG = "BaseActivity";
    private IBasePresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        beforeSetContent();
        View v = getContentView();
        if (v==null){
            setContentView(getLayoutId());
        }else{
            setContentView(v);
        }
        afterSetContent();

        initView();
        setupView();
        mPresenter = initPresenter();
    }

    public View getContentView(){
        return null;
    }

    public void beforeSetContent(){

    }

    public void afterSetContent(){

    }

    public <T> T bindView(@IdRes int id){
        return (T)findViewById(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter!=null){
            mPresenter.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter!=null){
            mPresenter.unSubscribe();
        }

    }

    public void  startActivity(Class<?> activity){
        Intent i = new Intent(this,activity);
        startActivity(i);
    }

    public void Toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    public void closeView(){
        finish();
    }

    public void dailog(String msg){
        new AlertDialog.Builder(this).setMessage(msg).create().show();
    }

}
