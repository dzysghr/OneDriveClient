package com.dzy.onedriveclient.event;

import com.dzy.onedriveclient.model.IBaseFileBean;

/**
 * Created by dzysg on 2017/4/26 0026.
 */

public final class UploadEvent {
    public IBaseFileBean from;
    public IBaseFileBean to;

    public UploadEvent(){}

    public UploadEvent(IBaseFileBean from, IBaseFileBean to) {
        this.from = from;
        this.to = to;
    }
}
