package com.dzy.onedriveclient.transfer;

/**
 * Created by dzysg on 2017/4/23 0023.
 */

public class DownloadDispatcher extends AbstractDispatcher<DownLoadTask> {


    public DownloadDispatcher(CoreContext context, BaseListener listener) {
        super(context, listener);
    }

    @Override
    protected DownLoadTask createTask(TaskHandle handle) {
        return  new DownLoadTask(mContext, handle, this);
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
        }else{

        }
        super.delete(handle);
    }
}
