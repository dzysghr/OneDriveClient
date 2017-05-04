package com.dzy.onedriveclient.transfer;

import com.dzy.onedriveclient.model.gen.DaoSession;
import com.dzy.onedriveclient.model.gen.TaskInfoDao;
import com.dzy.onedriveclient.model.gen.UploadSessionDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

/**
 * Created by dzysg on 2017/5/3 0003.
 */

@Entity
public class UploadSession {

    @Id
    private Long id;

    private String uploadUrl;
    private String expirationDateTime;
    private transient  List<String> nextExpectedRanges;
    private Long taskInfoId;

    @ToOne(joinProperty="taskInfoId")
    private TaskInfo taskInfo;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 987896579)
    private transient UploadSessionDao myDao;

    @Generated(hash = 643682920)
    public UploadSession(Long id, String uploadUrl, String expirationDateTime,
            Long taskInfoId) {
        this.id = id;
        this.uploadUrl = uploadUrl;
        this.expirationDateTime = expirationDateTime;
        this.taskInfoId = taskInfoId;
    }

    @Generated(hash = 1382405547)
    public UploadSession() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUploadUrl() {
        return this.uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getExpirationDateTime() {
        return this.expirationDateTime;
    }

    public void setExpirationDateTime(String expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    public Long getTaskInfoId() {
        return this.taskInfoId;
    }

    public void setTaskInfoId(Long taskInfoId) {
        this.taskInfoId = taskInfoId;
    }

    @Generated(hash = 1273576793)
    private transient Long taskInfo__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1432806539)
    public TaskInfo getTaskInfo() {
        Long __key = this.taskInfoId;
        if (taskInfo__resolvedKey == null || !taskInfo__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TaskInfoDao targetDao = daoSession.getTaskInfoDao();
            TaskInfo taskInfoNew = targetDao.load(__key);
            synchronized (this) {
                taskInfo = taskInfoNew;
                taskInfo__resolvedKey = __key;
            }
        }
        return taskInfo;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1833265191)
    public void setTaskInfo(TaskInfo taskInfo) {
        synchronized (this) {
            this.taskInfo = taskInfo;
            taskInfoId = taskInfo == null ? null : taskInfo.getId();
            taskInfo__resolvedKey = taskInfoId;
        }
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
    @Generated(hash = 1644017759)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUploadSessionDao() : null;
    }

    public List<String> getNextExpectedRange() {
        return nextExpectedRanges;
    }
}
