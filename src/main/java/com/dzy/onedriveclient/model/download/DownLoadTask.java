package com.dzy.onedriveclient.model.download;

import android.content.Context;

import com.dzy.onedriveclient.model.IFileModel;
import com.dzy.onedriveclient.model.drive.DriveFile;
import com.dzy.onedriveclient.model.gen.ThreadInfoDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dzysg on 2017/4/22 0022.
 */

public class DownLoadTask {
    private Context mContext;
    private DriveFile mFileInfo;
    private ThreadInfoDao mDao = null;
    private int mFinished = 0;
    public boolean isPause = false;
    private int mThreadCount = 1;
    private IFileModel mFileModel;
    private List<DownLoadThread> mThreadList;
    public static ExecutorService sExecutor = Executors.newCachedThreadPool();


    private static class DownLoadThread implements Runnable{
        @Override
        public void run() {




        }
    }
}
