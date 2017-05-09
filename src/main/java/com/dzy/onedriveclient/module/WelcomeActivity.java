package com.dzy.onedriveclient.module;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dzy.commemlib.utils.NetworkUtils;
import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.core.BaseActivity;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.DBModel;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.model.drive.TokenBean;
import com.dzy.onedriveclient.model.drive.TokenModel;
import com.dzy.onedriveclient.module.login.LoginActivity;
import com.dzy.onedriveclient.utils.RxHelper;
import com.dzy.onedriveclient.utils.UserInfoSPUtils;

import java.io.File;
import java.io.FilenameFilter;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WelcomeActivity extends BaseActivity {

    private Button mBtnOneDrive;
    private Button mBtnCancel;
    private Button mBtnChangeAccount;
    private TextView mTvUsername;
    private TextView mTvMsg;
    private Disposable mDisposable;
    public static final String KEY_AUTO_LOGIN ="auto_login";


    @Override
    protected void initView() {
        mTvUsername = bindView(R.id.tv_username);
        mTvMsg = bindView(R.id.tv_msg);
        mBtnOneDrive = bindView(R.id.btn_login_onedrive);
        mBtnCancel = bindView(R.id.btn_cancel);
        mBtnChangeAccount = bindView(R.id.btn_change_account);
    }

    @Override
    protected void setupView() {
        mBtnOneDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        mBtnChangeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserList();
            }
        });
        if (getIntent().getBooleanExtra(KEY_AUTO_LOGIN,true)){
            checkLogin();
        }else{
            mBtnOneDrive.setVisibility(View.VISIBLE);
            mBtnChangeAccount.setVisibility(View.VISIBLE);
        }
    }

    private void openLoginActivity() {
        Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
        i.putExtra(Constants.INTENT_KEY_COM_TYPE, Constants.INTENT_VALUE_COM_ONEDRIVE);
        startActivity(i);
        finish();
    }

    protected void checkLogin() {
        String user = UserInfoSPUtils.getUser();
        if (user==null){
            return;
        }else{
            ModelFactory.setDBModel(new DBModel(getApplicationContext(),user));
        }
        final TokenModel model = ModelFactory.getTokenModel();
        TokenBean bean = model.getTokenFromDb();
        if (bean != null) {
           setLoginState(user);
            if (NetworkUtils.isNetworkConnected(this)){
                refreshToken(bean,model);
            }else{
                Constants.sToken = bean;
                startActivity(MainActivity.class);
                finish();
            }

        }
    }

    private void refreshToken(TokenBean bean,TokenModel model){
        mDisposable = model.refreshToken(bean)
                .compose(RxHelper.<TokenBean>checkNetwork())
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
                        cancel();
                        mTvMsg.setText("登录失败");

                    }
                });
    }

    private void setLoginState(String username){
        mBtnCancel.setVisibility(View.VISIBLE);
        mTvUsername.setVisibility(View.VISIBLE);
        mTvUsername.setText(username);
        mTvMsg.setVisibility(View.VISIBLE);
        mTvMsg.setText("自动登录中...");
    }

    private void cancel(){
        if (mDisposable!=null){
            mDisposable.dispose();
            mDisposable = null;
        }
        mTvMsg.setText("");
        mBtnCancel.setVisibility(View.GONE);
        mBtnChangeAccount.setVisibility(View.VISIBLE);
        mBtnOneDrive.setVisibility(View.VISIBLE);
    }

    private void showUserList(){
        File file = new File(getFilesDir().getParentFile(),"databases");
        final String[] list = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.endsWith("-journal");
            }
        });
        if (list.length==0){
            Toast("当前无帐户");
        }else {
            new AlertDialog.Builder(this).setItems(list, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String user = list[which];
                    UserInfoSPUtils.setUser(user);
                    checkLogin();
                }
            }).show();
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
