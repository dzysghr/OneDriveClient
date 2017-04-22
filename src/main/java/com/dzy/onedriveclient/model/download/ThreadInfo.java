package com.dzy.onedriveclient.model.download;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 线程信息
 * Created by ziyue on 2015/7/14 0014.
 */
@Entity
public class ThreadInfo {

    @Id
    private Long id;
    private int mFileId;
    private int mStart;
    private int mEnd;
    private int mFinished;


    @Generated(hash = 1429044284)
    public ThreadInfo(Long id, int mFileId, int mStart, int mEnd, int mFinished) {
        this.id = id;
        this.mFileId = mFileId;
        this.mStart = mStart;
        this.mEnd = mEnd;
        this.mFinished = mFinished;
    }

    @Generated(hash = 930225280)
    public ThreadInfo() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFileId() {
        return mFileId;
    }

    public void setFileId(int fileId) {
        mFileId = fileId;
    }

    public int getStart() {
        return mStart;
    }

    public void setStart(int start) {
        mStart = start;
    }

    public int getEnd() {
        return mEnd;
    }

    public void setEnd(int end) {
        mEnd = end;
    }

    public int getFinished() {
        return mFinished;
    }

    public void setFinished(int finished) {
        mFinished = finished;
    }

    public int getMFileId() {
        return this.mFileId;
    }

    public void setMFileId(int mFileId) {
        this.mFileId = mFileId;
    }

    public int getMStart() {
        return this.mStart;
    }

    public void setMStart(int mStart) {
        this.mStart = mStart;
    }

    public int getMEnd() {
        return this.mEnd;
    }

    public void setMEnd(int mEnd) {
        this.mEnd = mEnd;
    }

    public int getMFinished() {
        return this.mFinished;
    }

    public void setMFinished(int mFinished) {
        this.mFinished = mFinished;
    }
}
