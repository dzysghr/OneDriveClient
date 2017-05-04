package com.dzy.onedriveclient.transfer;

/**
 * Created by dzysg on 2017/5/3 0003.
 */

public class UploadDispatcher extends AbstractDispatcher<UploadTask> {


    public UploadDispatcher(CoreContext context, BaseListener listener) {
        super(context, listener);
    }

    @Override
    protected UploadTask createTask(TaskHandle handle) {
        return null;
    }

    @Override
    protected void delete(TaskHandle handle) {
        super.delete(handle);

    }
}
