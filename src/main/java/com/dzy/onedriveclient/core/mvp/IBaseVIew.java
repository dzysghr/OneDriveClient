package com.dzy.onedriveclient.core.mvp;


public interface IBaseVIew {
    void Toast(String msg);
    void dialog(String msg);
    void close();
    void showProgress();
    void hideProgress();
}
