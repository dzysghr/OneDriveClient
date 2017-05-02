package com.dzy.onedriveclient.download;


import java.io.File;

/**
 * Created by dzysg on 2017/4/28 0028.
 */

public class TaskHandle {


    private TaskInfo mTaskInfo;
    private TaskDispatcher mTaskDispatchers;
    private File mFile;
    private int mState;
    private long mFinish;
    private long mLength;
    private int mSpeed;


    TaskHandle(TaskInfo taskInfo, TaskDispatcher taskDispatcher) {
        mTaskInfo = taskInfo;
        mTaskDispatchers = taskDispatcher;
        mFile = new File(mTaskInfo.getFilePath());
    }

    TaskDispatcher getTaskDispatcher(){
        return mTaskDispatchers;
    }

    TaskInfo getTaskInfo() {
        return mTaskInfo;
    }

    public void stop(){
        mTaskDispatchers.submit(TaskDispatcher.MSG_STOP,this);
    }

    public void start(){
        mTaskDispatchers.submit(TaskDispatcher.MSG_START,this);
    }

    public void delete(){
        mTaskDispatchers.submit(TaskDispatcher.MSG_DELETE,this);
    }

    public String Path(){
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
