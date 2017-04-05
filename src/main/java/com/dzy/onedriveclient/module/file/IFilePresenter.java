package com.dzy.onedriveclient.module.file;

import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.IBaseFileBean;

/**
 * Created by dzysg on 2017/4/3 0003.
 */

public interface IFilePresenter extends IBasePresenter {
    void refresh();
    void open(IBaseFileBean bean);
    void delete(IBaseFileBean bean);
    void goBack();
    void copy(IBaseFileBean bean);
    void cut(IBaseFileBean bean);
    void paste(IBaseFileBean bean);
    void dowload(IBaseFileBean bean);
    void upload(IBasePresenter bean);
}
