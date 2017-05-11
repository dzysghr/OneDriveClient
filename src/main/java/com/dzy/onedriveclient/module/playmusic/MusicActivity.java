package com.dzy.onedriveclient.module.playmusic;

import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.model.drive.facet.Thumbnail;
import com.dzy.onedriveclient.module.BaseMediaActivity;
import com.dzy.onedriveclient.utils.RxHelper;
import com.dzy.onedriveclient.utils.StringHelper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MusicActivity extends BaseMediaActivity {


    private Player mPlayer;
    private ProgressBar mProgressBar;
    private TextView mTvLeft;
    private TextView mTvRight;
    private TextView mTvName;
    private ImageView mImageView;
    private Call mCall;

    @Override
    protected void initView() {
        mProgressBar = bindView(R.id.progressBar);
        mTvLeft = bindView(R.id.tv_current);
        mTvRight = bindView(R.id.tv_end);
        mTvName = bindView(R.id.tv_music_name);
        mImageView = bindView(R.id.iv_music);
    }

    @Override
    protected void setupView() {
        mPlayer = new Player(mProgressBar,mTvLeft,mTvRight);
        loadData();
        Request request = new Request.Builder().url(StringHelper.makeThumbnailUrl(mId)).build();
        mCall = ModelFactory.getOkHttpClient().newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
                Log.e(TAG,"封面图片加载失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Thumbnail thumbnail = RxHelper.gson.fromJson(response.body().string(), Thumbnail.class);
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        Picasso.with(MusicActivity.this).load(thumbnail.getUrl()).into(mImageView);
                    }
                });
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_music;
    }

    @Override
    protected void loadError() {
        Toast("打开失败");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
        if (mCall!=null){
            mCall.cancel();
        }
    }

    @Override
    protected void loadSucceed(String url) {
        String music = getIntent().getStringExtra(Constants.KEY_NAME);
        mTvName.setText("正在播放:"+music);
        Log.e(TAG, "loadSucceed: 开始加载和播放");
        mPlayer.playUrl(url);
    }
}
