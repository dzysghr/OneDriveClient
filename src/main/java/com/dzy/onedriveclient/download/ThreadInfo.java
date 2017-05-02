package com.dzy.onedriveclient.download;

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
    private String fileId;
    private long start;
    private long end;
    private long finished;
    private Long taskId;

    @Generated(hash = 1030315365)
    public ThreadInfo(Long id, String fileId, long start, long end, long finished,
            Long taskId) {
        this.id = id;
        this.fileId = fileId;
        this.start = start;
        this.end = end;
        this.finished = finished;
        this.taskId = taskId;
    }
    @Generated(hash = 930225280)
    public ThreadInfo() {
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }
    public Long getTaskId() {
        return this.taskId;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    public String getFileId() {
        return this.fileId;
    }
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    public long getStart() {
        return this.start;
    }
    public void setStart(long start) {
        this.start = start;
    }
    public long getEnd() {
        return this.end;
    }
    public void setEnd(long end) {
        this.end = end;
    }
    public long getFinished() {
        return this.finished;
    }
    public void setFinished(long finished) {
        this.finished = finished;
    }
}
