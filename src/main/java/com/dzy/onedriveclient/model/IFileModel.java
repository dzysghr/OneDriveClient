package com.dzy.onedriveclient.model;

import java.util.List;

import io.reactivex.Observable;


public interface IFileModel {
    Observable<List<IBaseFileBean>> getChildren(IBaseFileBean bean);
    Observable<Boolean> delete(IBaseFileBean bean);
    Observable<Boolean> copy(IBaseFileBean from,IBaseFileBean to);
    Observable<Boolean> cut(IBaseFileBean from,IBaseFileBean to);
    Observable<Boolean> createFolder(IBaseFileBean parent, String name);
    Observable<Boolean> exists(IBaseFileBean bean);
    Observable<IBaseFileBean> getRoot();
}
