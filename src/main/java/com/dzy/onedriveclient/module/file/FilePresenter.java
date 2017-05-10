package com.dzy.onedriveclient.module.file;

import android.util.Log;

import com.dzy.commemlib.rxbus.RxBus;
import com.dzy.commemlib.utils.NetworkUtils;
import com.dzy.onedriveclient.config.BaseApplication;
import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.core.mvp.IBaseVIew;
import com.dzy.onedriveclient.event.DownloadEvent;
import com.dzy.onedriveclient.event.UploadEvent;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.IFileModel;
import com.dzy.onedriveclient.utils.RxHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class FilePresenter implements IFilePresenter {

    protected IFileView mView;
    protected IBaseFileBean mCurrent;
    protected IFileModel mFileModel;
    protected IBaseFileBean mCopyOrCut;
    protected boolean mIsCopy;
    private List<IBaseFileBean> mList;

    private Disposable mDisposable;


    private Consumer<Throwable> mHandleListError = new Consumer<Throwable>() {
        @Override
        public void accept(@NonNull Throwable throwable) throws Exception {
            Log.e("FilePresenter", "mHandleListError: ", throwable);
            mView.Toast(throwable.getMessage());
            mView.hideProgress();
            mView.showErrorView();
        }
    };

    private Consumer<Throwable> mSimpleError = new Consumer<Throwable>() {
        @Override
        public void accept(@NonNull Throwable throwable) throws Exception {
            Log.e("FilePresenter", "mError: ", throwable);
            mView.Toast(throwable.getMessage());
            mView.hideProgress();
        }
    };


    public FilePresenter(IFileModel model) {
        mFileModel = model;
    }

    @Override
    public void attachView(IBaseVIew vIew) {
        mView = (IFileView) vIew;
    }

    @Override
    public void unSubscribe() {
        mView = null;
        if (mDisposable!=null){
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void open(IBaseFileBean bean) {
        if (bean.isFolder()) {
            return;
        }
    }
    long i = 0;
    @Override
    public void refresh() {
        if (i++==3){
            Constants.sToken.setAccess_token("aaa");
        }
        mView.showProgress();
        mDisposable = mFileModel
                .getChildren(mCurrent, IFileModel.CACHE_NO)
                .compose(RxHelper.<List<IBaseFileBean>>io_main())
                .subscribe(new Consumer<List<IBaseFileBean>>() {
                    @Override
                    public void accept(@NonNull List<IBaseFileBean> list) throws Exception {
                        mList = list;
                        mView.showFileList(list);
                        mView.hideProgress();
                    }
                }, mHandleListError);
    }

    @Override
    public void setCurrent(IBaseFileBean bean) {
        mCurrent = bean;
    }

    @Override
    public void getChildren(IBaseFileBean bean) {
        if (bean == null || bean.isFolder()) {
            mCurrent = bean;
            refresh();
        }
    }

    @Override
    public void delete(final IBaseFileBean bean) {
        mView.showProgress();
        mFileModel
                .delete(bean)
                .compose(RxHelper.<Boolean>checkNetwork())
                .compose(RxHelper.<Boolean>io_main())
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            refresh();
                        }
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            mView.Toast("删除成功");
                        }
                    }
                }, mSimpleError);
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
    public void paste(final IBaseFileBean bean) {
        Observable<Boolean> ob;
        if (mCopyOrCut == null) {
            mView.Toast("当前无项目粘贴");
            return;
        } else {
            if (mIsCopy) {
                ob = mFileModel.copy(mCopyOrCut, mCurrent);
            } else {
                ob = mFileModel.cut(mCopyOrCut, mCurrent);
            }
        }
        mView.showProgress();
        ob
                .compose(RxHelper.<Boolean>checkNetwork())
                .compose(RxHelper.<Boolean>io_main())
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            refresh();
                        }
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            mView.Toast("操作成功");
                        }
                    }
                }, mSimpleError);
    }

    @Override
    public void createFolder(String name) {
        mFileModel.createFolder(mCurrent, name)
                .compose(RxHelper.<IBaseFileBean>checkNetwork())
                .compose(RxHelper.<IBaseFileBean>io_main())
                .subscribe(new Consumer<IBaseFileBean>() {
                    @Override
                    public void accept(@NonNull IBaseFileBean bean) throws Exception {
                        mView.Toast("操作成功");
                        mList.add(bean);
                        mView.showFileList(mList);
                    }
                }, mSimpleError);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void download(IBaseFileBean from, IBaseFileBean to) {
        if (!NetworkUtils.isNetworkConnected(BaseApplication.getApp())){
            mView.Toast("请检查网络");
        }else{
            RxBus.getDefault().post(new DownloadEvent(from,to));
        }
    }

    @Override
    public void upload(IBaseFileBean from, IBaseFileBean to) {
        if (from.isFolder()){
            mView.Toast("不能上传文件夹");
            return;
        }
        if (!NetworkUtils.isNetworkConnected(BaseApplication.getApp())){
            mView.Toast("请检查网络");
        }else{
            RxBus.getDefault().post(new UploadEvent(from,to));
        }
    }
}
