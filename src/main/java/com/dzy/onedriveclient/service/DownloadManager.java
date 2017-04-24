package com.dzy.onedriveclient.service;

import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.download.TaskInfo;
import com.dzy.onedriveclient.model.drive.IDriveFileModel;
import com.dzy.onedriveclient.model.gen.ThreadInfoDao;

import java.util.List;

/**
 * Created by dzysg on 2017/4/23 0023.
 */

public class DownloadManager {

    private IDriveFileModel mIDriveFileModel;
    private ThreadInfoDao mThreadInfoDao;
    private List<TaskInfo> mTaskInfos;


    public void start(TaskInfo taskInfo){

    }

    public void stop(TaskInfo taskInfo){

    }

    public void delete(TaskInfo taskInfo){

    }

    public void download(IBaseFileBean from,IBaseFileBean to){

    }

}
