package com.dzy.onedriveclient.download;

import android.content.Context;

import com.dzy.onedriveclient.model.gen.TaskInfoDao;
import com.dzy.onedriveclient.model.gen.ThreadInfoDao;

import java.util.concurrent.Executor;

import okhttp3.OkHttpClient;

/**
 * Created by dzysg on 2017/4/26 0026.
 */

public class DownloadContext {

    private Context mContext;
    private TaskInfoDao mTaskDao = null;
    private ThreadInfoDao mThreadDao = null;
    private OkHttpClient mOkHttpClient =null;
    private Executor mExecutor;

    public Context getContext() {
        return mContext;
    }

    public TaskInfoDao getTaskDao() {
        return mTaskDao;
    }

    public ThreadInfoDao getThreadDao() {
        return mThreadDao;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public Executor getExecutor() {
        return mExecutor;
    }

    public void setExecutor(Executor executor) {
        mExecutor = executor;
    }

    public static class Builder {
        DownloadContext mContext = new DownloadContext();

        public Builder(Context context) {
            mContext.mContext = context;
        }


        public Builder client(OkHttpClient client) {
            mContext.mOkHttpClient = client;
            return this;
        }


        public Builder dao(TaskInfoDao dao, ThreadInfoDao threaddao) {
            mContext.mTaskDao = dao;
            mContext.mThreadDao = threaddao;
            return this;
        }

        public Builder executor(Executor executor) {
            mContext.mExecutor = executor;
            return this;
        }


        private void checkNull(Object o, String error) {
            if (o == null) {
                throw new IllegalArgumentException("lack of params :" + error);
            }
        }

        public DownloadContext build() {
            checkNull(mContext.mContext, "context");
            checkNull(mContext.mExecutor, "Executor");
            checkNull(mContext.mTaskDao, "TaskInfoDao");
            checkNull(mContext.mThreadDao, "mThreadDao");
            checkNull(mContext.mOkHttpClient, "mOkHttpClient");
            return mContext;
        }
    }

}
