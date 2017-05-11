package com.dzy.onedriveclient.module;

import android.view.View;
import android.widget.ProgressBar;

import com.dzy.onedriveclient.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;


public class PictureActivity extends BaseMediaActivity {

    private ProgressBar mProgressBar;
    private PhotoView mPhotoView;

    @Override
    protected void initView() {
        mProgressBar = bindView(R.id.progressBar);
        mPhotoView = bindView(R.id.photoview);
    }

    @Override
    protected void setupView() {
        loadData();
    }

    @Override
    protected void loadError() {
        Toast("文件打开失败");
        finish();
    }

    @Override
    protected void loadSucceed(final String url) {
        Picasso.with(PictureActivity.this).load(url).into(mPhotoView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                mProgressBar.setVisibility(View.GONE);
                loadError();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picture;
    }
}
