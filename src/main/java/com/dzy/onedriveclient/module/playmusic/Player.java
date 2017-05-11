package com.dzy.onedriveclient.module.playmusic;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Player implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    public MediaPlayer mediaPlayer; // 媒体播放器
    private ProgressBar mProgressBar; // 拖动条
    private TextView mTvLeft;
    private TextView mTvRight;


    private Timer mTimer = new Timer(); // 计时器

    // 初始化播放器  
    public Player(ProgressBar progressBar, TextView left, TextView right) {
        super();
        this.mProgressBar = progressBar;
        mTvLeft = left;
        mTvRight = right;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 每一秒触发一次  
        mTimer.schedule(timerTask, 0, 1000);
    }

    // 计时器  
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            if (mediaPlayer.isPlaying() && !mProgressBar.isPressed()) {
                handler.sendEmptyMessage(0); // 发送消息  
            }
        }
    };

    private boolean init = false;
    private int mDuration;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (init) {
                int position = mediaPlayer.getCurrentPosition();
                long pos = mProgressBar.getMax() * position / mDuration;
                mProgressBar.setProgress((int) pos);
                mTvLeft.setText(getTime(position));
            }
            else{
                init();
            }
        }
    };

    private void init() {
        mDuration = mediaPlayer.getDuration();
        if (mDuration>0){
            mTvRight.setText(getTime(mDuration));
            init=true;
        }
    }

    private String getTime(int time) {
        return String.format(Locale.getDefault(), "%02d:%02d", Math.round(time / 1000f / 60), Math.round(time / 1000f % 60));
    }

    public void play() {
        mediaPlayer.start();
    }

    /**
     *
     * @param url
     *            url地址 
     */
    public void playUrl(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url); // 设置数据源  
            mediaPlayer.prepare(); // prepare自动播放  
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 暂停  
    public void pause() {
        mediaPlayer.pause();
    }

    // 停止  
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // 播放准备  
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Log.e("mediaPlayer", "onPrepared");
    }

    // 播放完成  
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("mediaPlayer", "onCompletion");
    }

    /**
     * 缓冲更新 
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mProgressBar.setSecondaryProgress(percent);
        int currentProgress = mProgressBar.getMax()
                * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        Log.e(currentProgress + "% play", percent + " buffer");
    }

}  