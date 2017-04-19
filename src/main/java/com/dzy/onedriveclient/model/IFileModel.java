package com.dzy.onedriveclient.model;

import java.util.List;

import io.reactivex.Observable;


public interface IFileModel {

    int CACHE_ONLY = 0;
    int CACHE_FIRST = 1;
    int CACHE_NO = 2;


    Observable<List<IBaseFileBean>> getChildren(String path,int cacheMode);
    Observable<List<IBaseFileBean>> getChildren(final IBaseFileBean bean,int cacheMode);
    Observable<Boolean> delete(final IBaseFileBean bean);
    Observable<Boolean> copy(final IBaseFileBean from,final IBaseFileBean to);
    Observable<Boolean> cut(final IBaseFileBean from,IBaseFileBean to);
    Observable<IBaseFileBean> createFolder(final IBaseFileBean parent,final  String name);
    Observable<Boolean> exists(final IBaseFileBean bean);
}
