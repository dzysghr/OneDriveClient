package com.dzy.onedriveclient.module.more;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.model.drive.Drive;
import com.dzy.onedriveclient.model.drive.IUserModel;
import com.dzy.onedriveclient.utils.RxHelper;
import com.google.gson.reflect.TypeToken;

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
    @Override
    protected void initView() {
        mTvUsername =  bindView(R.id.tvUserName);
        mTvSpace = bindView(R.id.tvSpace);
        mProgressBar = bindView(R.id.pb_diskSpace);
    }

    @Override
    protected void setupView() {

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
