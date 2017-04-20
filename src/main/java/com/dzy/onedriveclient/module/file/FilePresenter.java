package com.dzy.onedriveclient.module.file;

import android.util.Log;

import com.dzy.onedriveclient.core.mvp.IBaseVIew;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.IFileModel;
import com.dzy.onedriveclient.utils.RxHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


public class FilePresenter implements IFilePresenter {

    protected IFileView mView;
    protected IBaseFileBean mCurrent;
    protected IFileModel mFileModel;
    protected IBaseFileBean mCopyOrCut;
    protected boolean mIsCopy;


    private Consumer<Throwable> mErrorConsumer  = new Consumer<Throwable>() {
        @Override
        public void accept(@NonNull Throwable throwable) throws Exception {
            Log.e("FilePresenter", "mErrorConsumer: ", throwable);
            mView.Toast(throwable.getMessage());
            mView.hideProgress();
            mView.showErrorView();
        }
    };


    public FilePresenter(IFileModel model){
        mFileModel = model;
    }

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
        mView.showProgress();
        mFileModel.getChildren(mCurrent,IFileModel.CACHE_NO)
                .compose(RxHelper.<List<IBaseFileBean>>io_main())
                .subscribe(new Consumer<List<IBaseFileBean>>() {
                    @Override
                    public void accept(@NonNull List<IBaseFileBean> list) throws Exception {
                        mView.showFileList(list);
                        mView.hideProgress();
                    }
                },mErrorConsumer);
    }

    @Override
    public void setCurrent(IBaseFileBean bean) {
        mCurrent = bean;
    }

    @Override
    public void open(IBaseFileBean bean) {
         if (bean==null||bean.isFolder()) {
            mCurrent = bean;
            refresh();
        }
    }

    @Override
    public void delete(IBaseFileBean bean) {
        mView.showProgress();
        mFileModel
                .delete(bean)
                .compose(RxHelper.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                if (aBoolean){
                    mView.Toast("删除成功");
                }
            }
        },mErrorConsumer);
    }

    @Override
    public void goBack() {

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
        Observable<Boolean> ob;
        if (mCopyOrCut==null){
            mView.Toast("当前无项目粘贴");
            return;
        }else{
            if (mIsCopy){
                ob = mFileModel.copy(mCopyOrCut,mCurrent);
            }else{
               ob = mFileModel.cut(mCopyOrCut,mCurrent);
            }
        }
        mView.showProgress();
        ob
                .compose(RxHelper.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                refresh();
                if (aBoolean){
                    mView.Toast("操作成功");
                }
            }
        },mErrorConsumer);
    }

    @Override
    public void upload(IBaseFileBean bean) {

    }

    @Override
    public void createFolder(String name) {
        mFileModel.createFolder(mCurrent,name)
                .compose(RxHelper.<IBaseFileBean>io_main())
                .subscribe(new Consumer<IBaseFileBean>() {
            @Override
            public void accept(@NonNull IBaseFileBean bean) throws Exception {
                    mView.Toast("操作成功");
            }
        },mErrorConsumer);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void download(IBaseFileBean bean) {

    }


}
