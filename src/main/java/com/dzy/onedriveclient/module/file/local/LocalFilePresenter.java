package com.dzy.onedriveclient.module.file.local;

import com.dzy.onedriveclient.core.mvp.IBaseVIew;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.IFileModel;
import com.dzy.onedriveclient.module.file.IFilePresenter;
import com.dzy.onedriveclient.module.file.IFileView;
import com.dzy.onedriveclient.utils.RxHelper;

import java.util.List;
import java.util.Stack;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


public class LocalFilePresenter implements IFilePresenter {

    protected IFileView mView;
    protected IBaseFileBean mCurrent;
    protected IFileModel mFileModel;
    protected IBaseFileBean mCopyOrCut;
    protected boolean mIsCopy;
    private int mLevel = 0;
    private Stack<IBaseFileBean> mStack;

    private Consumer<Throwable> mErrorConsumer  = new Consumer<Throwable>() {
        @Override
        public void accept(@NonNull Throwable throwable) throws Exception {
            mView.Toast(throwable.getMessage());
            mView.hideProgress();
            mView.showErrorView();
        }
    };


    public LocalFilePresenter(IFileModel model){
        mFileModel = model;
        mStack = new Stack<>();
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
        mFileModel.getChildren(mCurrent)
                .compose(RxHelper.<List<IBaseFileBean>>io_main())
                .subscribe(new Consumer<List<IBaseFileBean>>() {
                    @Override
                    public void accept(@NonNull List<IBaseFileBean> list) throws Exception {
                        mView.showFileList(list);
                        mView.hideProgress();
                    }
                },mErrorConsumer);

        if (mLevel == 0) {
            mView.showTitleAndParent("root", null);
        } else {
            mView.showTitleAndParent(mCurrent.getName(),getParentName());
        }
    }

    private String getParentName(){
        if (mStack.peek()==null){
            return "<root";
        }else{
            return "<"+mStack.peek().getName();
        }
    }


    @Override
    public void open(IBaseFileBean bean) {
        if (bean.isFolder()) {
            mStack.push(mCurrent);
            mCurrent = bean;
            mLevel++;
            refresh();
        }
    }

    @Override
    public void goBack() {
        if (mLevel==0) {
            mView.close();
            return;
        }
        mLevel--;
        mCurrent = mStack.pop();
        refresh();

    }

    @Override
    public void delete(IBaseFileBean bean) {
        mView.showProgress();
        mFileModel.delete(bean).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                if (aBoolean){
                    mView.Toast("删除成功");
                }
            }
        },mErrorConsumer);
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
        mView.showProgress();
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
        ob.subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
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
                .doOnNext(new Consumer<IBaseFileBean>() {
                    @Override
                    public void accept(@NonNull IBaseFileBean bean) throws Exception {
                        refresh();
                    }
                }).subscribe(new Consumer<IBaseFileBean>() {
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
