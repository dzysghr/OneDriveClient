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
    private int fileId;
    private int start;
    private int end;
    private int finished;
    private Long taskId;
    @Generated(hash = 818161760)
    public ThreadInfo(Long id, int fileId, int start, int end, int finished,
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
    public int getFileId() {
        return this.fileId;
    }
    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
    public int getStart() {
        return this.start;
    }
    public void setStart(int start) {
        this.start = start;
    }
    public int getEnd() {
        return this.end;
    }
    public void setEnd(int end) {
        this.end = end;
    }
    public int getFinished() {
        return this.finished;
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
}
