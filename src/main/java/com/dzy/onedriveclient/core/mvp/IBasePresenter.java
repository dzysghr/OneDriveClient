package com.dzy.onedriveclient.core.mvp;


public interface IBasePresenter {

    void attachView(IBaseVIew vIew);
    void unSubscribe();
    void resume();
    void onInit();

}
