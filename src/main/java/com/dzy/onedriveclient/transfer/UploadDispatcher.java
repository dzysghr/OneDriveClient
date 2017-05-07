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
        return new UploadTask(mContext,handle,this);
    }
}
