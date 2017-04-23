package com.dzy.onedriveclient.module.file;

import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.IBaseFileBean;


public interface IFilePresenter extends IBasePresenter {

    void setCurrent(IBaseFileBean bean);
    void refresh();
    void getChildren(IBaseFileBean bean);
    void open(IBaseFileBean bean);
    void delete(IBaseFileBean bean);
    void goBack();
    void copy(IBaseFileBean bean);
    void cut(IBaseFileBean bean);
    void paste(IBaseFileBean bean);
    void createFolder(String name);
    void loadMore();
    void download(IBaseFileBean bean);
    void upload(IBaseFileBean bean);
}
