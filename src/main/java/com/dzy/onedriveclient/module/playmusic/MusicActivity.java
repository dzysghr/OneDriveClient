package com.dzy.onedriveclient.module.playmusic;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.module.BaseMediaActivity;

public class MusicActivity extends BaseMediaActivity {


    private Player mPlayer;
    private ProgressBar mProgressBar;
    private TextView mTvLeft;
    private TextView mTvRight;
    private TextView mTvName;

    @Override
    protected void initView() {
        mProgressBar = bindView(R.id.progressBar);
        mTvLeft = bindView(R.id.tv_current);
        mTvRight = bindView(R.id.tv_end);
        mTvName = bindView(R.id.tv_music_name);
    }

    @Override
    protected void setupView() {
        mPlayer = new Player(mProgressBar,mTvLeft,mTvRight);
        loadData();
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
    }

    @Override
    protected void loadSucceed(String url) {
        String music = getIntent().getStringExtra(Constants.KEY_NAME);
        mTvName.setText("正在播放:"+music);
        Log.e(TAG, "loadSucceed: 开始加载和播放");
        mPlayer.playUrl(url);
    }
}
