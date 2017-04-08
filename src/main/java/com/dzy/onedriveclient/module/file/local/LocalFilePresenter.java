package com.dzy.onedriveclient.module.file.local;

import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.core.mvp.IBaseVIew;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.IFileModel;
import com.dzy.onedriveclient.model.local.LocalFileModel;
import com.dzy.onedriveclient.module.file.IFilePresenter;
import com.dzy.onedriveclient.module.file.IFileView;
import com.dzy.onedriveclient.utils.RxHelper;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


public class LocalFilePresenter implements IFilePresenter {

    private IFileView mView;
    private IBaseFileBean mParent;
    private IBaseFileBean mCurrent;
    private IFileModel mFileModel;
    private IBaseFileBean mRoot;
    private IBaseFileBean mCopyOrCut;
    private boolean mIsCopy;


    private int mLevel = 0;

    @Override
    public void attachView(IBaseVIew vIew) {
        mView = (IFileView) vIew;
        mFileModel = new LocalFileModel();
        mRoot = mFileModel.getRoot().blockingFirst();
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
        mFileModel.getChildren(mCurrent)
                .compose(RxHelper.<List<IBaseFileBean>>io_main())
                .subscribe(new Consumer<List<IBaseFileBean>>() {
                    @Override
                    public void accept(@NonNull List<IBaseFileBean> list) throws Exception {
                        mView.showFileList(list);
                        if (mLevel == 0) {
                            mView.showTitleAndParent("根目录", null);
                        } else {
                            mView.showTitleAndParent(mCurrent.getName(), mParent == null ? "<" : "<" + mParent.getName());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        mView.Toast(throwable.getMessage());
                    }
                });
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
        mCopyOrCut = bean;
        mIsCopy = true;
    }

    @Override
    public void cut(IBaseFileBean bean) {
        mCopyOrCut = bean;
        mIsCopy = false;
    }

    @Override
    public void paste(IBaseFileBean bean) {
        if (mCopyOrCut==null){
            mView.Toast("当前无项目粘贴");
            return;
        }
    }

    @Override
    public void download(IBaseFileBean bean) {

    }

    @Override
    public void upload(IBasePresenter bean) {

    }

    @Override
    public void createFolder(String name) {
        mFileModel.createFolder(mCurrent,name);
    }
}
