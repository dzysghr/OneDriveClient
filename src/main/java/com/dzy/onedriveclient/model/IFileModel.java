package com.dzy.onedriveclient.model;

import java.util.List;

import io.reactivex.Observable;


public interface IFileModel {

    Observable<List<IBaseFileBean>> getChildren(String path);
    Observable<List<IBaseFileBean>> getChildren(final IBaseFileBean bean);
    Observable<Boolean> delete(final IBaseFileBean bean);
    Observable<Boolean> copy(final IBaseFileBean from,final IBaseFileBean to);
    Observable<Boolean> cut(final IBaseFileBean from,IBaseFileBean to);
    Observable<Boolean> createFolder(final IBaseFileBean parent,final  String name);
    Observable<Boolean> exists(final IBaseFileBean bean);
}
