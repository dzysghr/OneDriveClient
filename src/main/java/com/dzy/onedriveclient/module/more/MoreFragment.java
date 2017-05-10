package com.dzy.onedriveclient.module.more;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.model.drive.Drive;
import com.dzy.onedriveclient.model.drive.IUserModel;
import com.dzy.onedriveclient.module.WelcomeActivity;
import com.dzy.onedriveclient.utils.RxHelper;
import com.dzy.onedriveclient.utils.UserInfoSPUtils;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by dzysg on 2017/4/2 0002.
 */

public class MoreFragment extends BaseFragment {


    private IUserModel mUserModel;
    private TextView mTvUsername;
    private TextView mTvSpace;
    private ProgressBar mProgressBar;
    private Drive mDrive;
    private View mSwitchAccount;
    private View mLogout;

    @Override
    protected void initView() {
        mTvUsername =  bindView(R.id.tvUserName);
        mTvSpace = bindView(R.id.tvSpace);
        mProgressBar = bindView(R.id.pb_diskSpace);
        mSwitchAccount = bindView(R.id.layout_change_account);
        mLogout = bindView(R.id.layout_logout);
    }

    @Override
    protected void setupView() {
        mSwitchAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAccount();
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLogout();
            }
        });
    }

    private void requestLogout(){
        new AlertDialog.Builder(getContext())
                .setMessage("确定要注销帐号吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                }).show();
    }

    private void logout(){
        ModelFactory.getDBModel().close();
        File db = new File(getContext().getFilesDir().getParent(), "databases");
        final String user = UserInfoSPUtils.getUser();
        File[] list = db.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(user)||name.equals(user+"-journal");
            }
        });
        for (File i:list){
            i.delete();
        }
        switchAccount();
    }

    private void switchAccount(){
        getActivity().finish();
        Intent i = new Intent(getContext(), WelcomeActivity.class);
        i.putExtra(WelcomeActivity.KEY_AUTO_LOGIN,false);
        startActivity(i);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    protected IBasePresenter initPresenter() {
        mUserModel = ModelFactory.getsUserModel();
        return null;
    }

    @Override
    protected void LazyLoad() {
        Log.e(TAG, "LazyLoad: more");
        if (mDrive!=null){
            return;
        }
        mUserModel.getDrive()
                .compose(RxHelper.<Response<ResponseBody>>io_main())
                .compose(RxHelper.handle(new TypeToken<Drive>(){}))
                .subscribe(new Consumer<Drive>() {
                    @Override
                    public void accept(@NonNull Drive drive) throws Exception {
                        setView(drive);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "accept: ",throwable);
                        Toast(throwable.getMessage());
                    }
                });
    }

    private String foramt = "空间使用情况:%.1fm/%.1fm ";
    private void setView(Drive drive){
        mDrive = drive;
        mTvUsername.setText(drive.getOwner().getUser().getDisplayName());
        long used = drive.getQuota().getUsed();
        long total = drive.getQuota().getTotal();
        mTvSpace.setText(String.format(Locale.getDefault(),foramt,used/1024f/1024,total/1024f/1024));
        mProgressBar.setProgress(Math.round(used*1.0f/total));
    }
}
