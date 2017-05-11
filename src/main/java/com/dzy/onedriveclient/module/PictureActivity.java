package com.dzy.onedriveclient.module;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseActivity;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.utils.StringHelper;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class PictureActivity extends BaseActivity {


    public static final String KEY_ID = "id";
    private ProgressBar mProgressBar;
    private PhotoView mPhotoView;

    @Override
    protected void initView() {
        mProgressBar = bindView(R.id.progressBar);
        mPhotoView = bindView(R.id.photoview);
    }

    @Override
    protected void setupView() {
        String id = getIntent().getStringExtra(KEY_ID);
        if (id==null){
            Toast("打开失败");
            finish();
            return;
        }
        String url = StringHelper.makeDownloadUrl(id);
        Request request = new Request.Builder().url(url).build();
        ModelFactory.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadError();
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
                Response pre = response.priorResponse();
                String url = pre.header("Location");
                if (url==null){
                    loadError();
                }else{
                    Log.e(TAG, "onResponse: " + url);
                    loadPicture(url);
                }
            }
        });

    }

    private void loadError(){
        mProgressBar.post(new TimerTask() {
            @Override
            public void run() {
                Toast("文件打开失败");
                finish();
            }
        });
    }

    private void loadPicture(final String url){
        mProgressBar.post(new TimerTask() {
            @Override
            public void run() {
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
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picture;
    }

    @Override
    protected IBasePresenter initPresenter() {
        return null;
    }
}
