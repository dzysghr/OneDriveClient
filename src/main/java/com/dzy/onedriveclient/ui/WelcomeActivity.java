package com.dzy.onedriveclient.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.core.BaseActivity;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.ui.login.LoginActivity;

public class WelcomeActivity extends BaseActivity {


    private Button mBtnOneDrive;

    @Override
    protected void initView() {
        mBtnOneDrive = bindView(R.id.btn_login_onedrive);
    }

    @Override
    protected void setupView() {
        mBtnOneDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                i.putExtra(Constants.INTENT_KEY_COM_TYPE,Constants.INTENT_VALUE_COM_ONEDRIVE);
                startActivity(i);
            }
        });
        // TODO: 2017/4/2 0002 判断登录与否
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected IBasePresenter initPresenter() {
        return null;
    }
}
