package com.dzy.onedriveclient.transfer;

import android.content.Context;

import com.dzy.onedriveclient.model.gen.DaoSession;
import com.dzy.onedriveclient.model.gen.TaskInfoDao;
import com.dzy.onedriveclient.model.gen.ThreadInfoDao;
import com.dzy.onedriveclient.model.gen.UploadSessionDao;

import java.util.concurrent.Executor;

import okhttp3.OkHttpClient;

/**
 * Created by dzysg on 2017/4/26 0026.
 */

public class CoreContext {

    private Context mContext;
    private TaskInfoDao mTaskDao;
    private ThreadInfoDao mThreadDao;
    private OkHttpClient mOkHttpClient;
    private UploadSessionDao mUploadSessionDao;
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

    public UploadSessionDao getUploadSessionDao() {
        return mUploadSessionDao;
    }

    public Executor getExecutor() {
        return mExecutor;
    }


    public static class Builder {
        CoreContext mContext = new CoreContext();

        public Builder(Context context) {
            mContext.mContext = context;
        }


        public Builder client(OkHttpClient client) {
            mContext.mOkHttpClient = client;
            return this;
        }


        public Builder dao(DaoSession session) {
            mContext.mTaskDao = session.getTaskInfoDao();
            mContext.mThreadDao = session.getThreadInfoDao();
            mContext.mUploadSessionDao = session.getUploadSessionDao();
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

        public CoreContext build() {
            checkNull(mContext.mContext, "context");
            checkNull(mContext.mExecutor, "Executor");
            checkNull(mContext.mTaskDao, "TaskInfoDao");
            checkNull(mContext.mThreadDao, "mThreadDao");
            checkNull(mContext.mOkHttpClient, "mOkHttpClient");
            return mContext;
        }
    }

}
