package com.dzy.onedriveclient.module.file.local;

import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.core.mvp.IBaseVIew;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.module.file.IFilePresenter;
import com.dzy.onedriveclient.module.file.IFileView;


public class LocalFilePresenter implements IFilePresenter {

    private IFileView mView;

    @Override
    public void attachView(IBaseVIew vIew) {
        mView = (IFileView) vIew;
    }

    @Override
    public void unSubscribe() {
        mView = null;
    }

    @Override
    public void resume() {

    }


    @Override
    public void refresh() {

    }

    @Override
    public void open(IBaseFileBean bean) {

    }

    @Override
    public void delete(IBaseFileBean bean) {

    }

    @Override
    public void goBack() {

    }

    @Override
    public void copy(IBaseFileBean bean) {

    }

    @Override
    public void cut(IBaseFileBean bean) {

    }

    @Override
    public void paste(IBaseFileBean bean) {

    }

    @Override
    public void dowload(IBaseFileBean bean) {

    }

    @Override
    public void upload(IBasePresenter bean) {

    }
}
