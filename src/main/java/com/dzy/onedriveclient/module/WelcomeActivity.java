package com.dzy.onedriveclient.module;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.core.BaseActivity;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.model.drive.TokenBean;
import com.dzy.onedriveclient.model.drive.TokenModel;
import com.dzy.onedriveclient.module.login.LoginActivity;
import com.dzy.onedriveclient.transfer.UploadSession;
import com.dzy.onedriveclient.utils.RxHelper;
import com.google.gson.Gson;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

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
                openLoginActivity();
            }
        });
    }

    private void openLoginActivity(){
        Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
        i.putExtra(Constants.INTENT_KEY_COM_TYPE,Constants.INTENT_VALUE_COM_ONEDRIVE);
        startActivity(i);
        finish();
    }

    @Override
    public void afterSetContent() {
        super.afterSetContent();
        //checkLogin();
        String json = "{\n" +
                "  \"uploadUrl\": \"https://sn3302.up.1drv.com/up/fe6987415ace7X4e1eF866337\",\n" +
                "  \"expirationDateTime\": \"2015-01-29T09:21:55.523Z\",\n" +
                "  \"nextExpectedRanges\": [\"0-\"]\n" +
                "}";

        Gson gson = new Gson();
        UploadSession session = gson.fromJson(json, UploadSession.class);
    }

    protected void checkLogin(){

        final TokenModel model = ModelFactory.getTokenModel();
        TokenBean bean = model.getTokenFromDb();
        if (bean!=null){
            model.refreshToken(bean)
                    .compose(RxHelper.<TokenBean>io_main())
                    .subscribe(new Consumer<TokenBean>() {
                        @Override
                        public void accept(@NonNull TokenBean bean) throws Exception {
                            Constants.sToken = bean;
                            startActivity(MainActivity.class);
                            finish();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            Log.e(TAG, "accept: ", throwable);
                        }
                    });
        }
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
