package com.dzy.onedriveclient.transfer;

import java.io.File;

/**
 * Created by dzysg on 2017/4/23 0023.
 */

public class DownloadDispatcher extends AbstractDispatcher<DownLoadTask> {

    public DownloadDispatcher(DownloadContext context, BaseListener listener) {
        super(context, listener);
    }

    @Override
    protected void create(TaskHandle handle) {
        DownLoadTask task = new DownLoadTask(mContext, handle, this);
        mTaskMap.put(handle, task);
    }

    @Override
    public void delete(TaskHandle handle) {
        if (mTaskMap.containsKey(handle)) {
            DownLoadTask task = mTaskMap.get(handle);
            if (task.isRunning()) {
                task.stop();
                submitDelay(MSG_DELETE, handle, 1000);
                return;
            }
        }
        TaskInfo info = handle.getTaskInfo();
        if (info.getFinish() != info.getLength()) {
            new File(info.getFilePath()).deleteOnExit();
        }
        info.resetThreads();
        mContext.getThreadDao().deleteInTx(info.getThreads());
        mContext.getTaskDao().delete(info);
    }
}
