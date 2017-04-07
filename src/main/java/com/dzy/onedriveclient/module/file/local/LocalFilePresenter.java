package com.dzy.onedriveclient.module.file.local;

import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.core.mvp.IBaseVIew;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.IFileModel;
import com.dzy.onedriveclient.model.local.LocalFileModel;
import com.dzy.onedriveclient.module.file.IFilePresenter;
import com.dzy.onedriveclient.module.file.IFileView;

import java.util.List;


public class LocalFilePresenter implements IFilePresenter {

    private IFileView mView;
    private IBaseFileBean mParent;
    private IBaseFileBean mCurrent;
    private IFileModel mFileModel;
    private int mLevel = 0;

    @Override
    public void attachView(IBaseVIew vIew) {
        mView = (IFileView) vIew;
        mFileModel = new LocalFileModel();
    }

    @Override
    public void unSubscribe() {
        mView = null;
    }

    @Override
    public void resume() {
        refresh();
    }

    @Override
    public void refresh() {
        List<IBaseFileBean> list = mFileModel.getChildren(mCurrent);
        mView.showFileList(list);
        if (mLevel == 0) {
            mView.showTitleAndParent("根目录", null);
        } else {
            mView.showTitleAndParent(mCurrent.getName(), mParent == null ? "<" : "<" + mParent.getName());
        }
    }


    @Override
    public void open(IBaseFileBean bean) {
        if (bean.isFolder()) {
            mParent = mCurrent;
            mCurrent = bean;
            mLevel++;
            refresh();
        }
    }

    @Override
    public void delete(IBaseFileBean bean) {

    }

    @Override
    public void goBack() {
        if (mLevel==0) {
            mView.close();
            return;
        }
        mLevel--;
        mCurrent = mParent;
        mParent = mCurrent == null ? null : mCurrent.getParent();

        refresh();

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
    public void download(IBaseFileBean bean) {

    }

    @Override
    public void upload(IBasePresenter bean) {

    }
}
