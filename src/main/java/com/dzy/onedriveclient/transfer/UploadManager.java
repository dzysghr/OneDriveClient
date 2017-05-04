package com.dzy.onedriveclient.transfer;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by dzysg on 2017/5/4 0004.
 */

public class UploadManager extends AbstractManager {

    public UploadManager(CoreContext coreContext) {
        super(coreContext);
    }

    @Override
    AbstractDispatcher provideDispatcher(CoreContext coreContext, BaseListener listener) {
        return new UploadDispatcher(coreContext,listener);
    }


    @Override
    List<TaskHandle> initTaskList() {
        return null;
    }

    @Override
    Observable<TaskHandle> createTask(String url, String localPath) {
        return null;
    }
}
