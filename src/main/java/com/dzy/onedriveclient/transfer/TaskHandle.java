package com.dzy.onedriveclient.transfer;


import java.io.File;

/**
 * Created by dzysg on 2017/4/28 0028.
 */

public class TaskHandle {

    private TaskInfo mTaskInfo;
    private AbstractManager mManager;
    private File mFile;
    private int mState = TaskState.STATE_INIT;
    private int mSpeed;

    TaskHandle(TaskInfo taskInfo, AbstractManager manager) {
        mTaskInfo = taskInfo;
        mManager = manager;
        mFile = new File(mTaskInfo.getFilePath());
    }

    TaskInfo getTaskInfo() {
        return mTaskInfo;
    }

    public void stop(){
        mManager.stop(this);
    }

    public void start(){
        mManager.start(this);
    }

    public void delete(boolean withFile){
        mManager.delete(this,withFile);
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
