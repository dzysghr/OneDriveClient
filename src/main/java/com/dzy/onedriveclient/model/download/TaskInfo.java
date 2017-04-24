package com.dzy.onedriveclient.model.download;

import com.dzy.onedriveclient.model.gen.DaoSession;
import com.dzy.onedriveclient.model.gen.TaskInfoDao;
import com.dzy.onedriveclient.model.gen.ThreadInfoDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * Created by dzysg on 2017/4/23 0023.
 */

@Entity
public class TaskInfo {

    public static final int STATE_RUNNING = 0;
    public static final int STATE_PAUSE = 1;
    public static final int STATE_ERROR = 3;

    @Id
    private Long id;
    private int fileId;
    private long length;
    private String filePath;

    @ToMany(referencedJoinProperty = "taskId")
    private List<ThreadInfo> threads;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1276444919)
    private transient TaskInfoDao myDao;

    @Generated(hash = 16028162)
    public TaskInfo(Long id, int fileId, long length, String filePath) {
        this.id = id;
        this.fileId = fileId;
        this.length = length;
        this.filePath = filePath;
    }

    @Generated(hash = 2022720704)
    public TaskInfo() {
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

    public long getLength() {
        return this.length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1254453599)
    public List<ThreadInfo> getThreads() {
        if (threads == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ThreadInfoDao targetDao = daoSession.getThreadInfoDao();
            List<ThreadInfo> threadsNew = targetDao._queryTaskInfo_Threads(id);
            synchronized (this) {
                if (threads == null) {
                    threads = threadsNew;
                }
            }
        }
        return threads;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1164718580)
    public synchronized void resetThreads() {
        threads = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 784127603)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTaskInfoDao() : null;
    }
    

}
