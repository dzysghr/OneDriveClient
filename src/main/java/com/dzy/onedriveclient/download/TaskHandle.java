package com.dzy.onedriveclient.download;


import java.io.File;

/**
 * Created by dzysg on 2017/4/28 0028.
 */

public class TaskHandle {


    private TaskInfo mTaskInfo;
    private DownloadManager mDownloadManager;
    private File mFile;
    private int mState = TaskState.STATE_INIT;
    private long mFinish;
    private long mLength;
    private int mSpeed;


    TaskHandle(TaskInfo taskInfo, DownloadManager downloadManager) {
        mTaskInfo = taskInfo;
        mDownloadManager = downloadManager;
        mFile = new File(mTaskInfo.getFilePath());
    }

    DownloadManager getManager(){
        return mDownloadManager;
    }

    TaskInfo getTaskInfo() {
        return mTaskInfo;
    }

    public void stop(){
        mDownloadManager.stop(this);
    }

    public void start(){
        mDownloadManager.start(this);
    }

    public void delete(boolean withFile){
        mDownloadManager.delete(this,withFile);
    }

    public String getPath(){
        return mFile.getPath();
    }

    public String getParentPath(){
        return mFile.getParent();
    }

    public String getFileName(){
        return mFile.getName();
    }

    public long getFinish() {
        return mTaskInfo.getFinish();
    }

    public long getLength() {
        return mTaskInfo.getLength();
    }

    public int getSpeed() {
        return mSpeed;
    }

    public long getId(){
        Long id = mTaskInfo.getId();
        return id==null?-1:id;
    }

    void setSpeed(int speed) {
        mSpeed = speed;
    }

    public int getState() {
        return mState;
    }

    void setState(int state) {
        mState = state;
    }
}
