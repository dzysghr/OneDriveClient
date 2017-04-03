package com.dzy.onedriveclient.model;

/**
 * Created by dzysg on 2017/4/3 0003.
 */

public interface IBaseFileBean {

    String getName();
    String getType();
    boolean isFolder();
    String getMotifyDateTime();
    int getSize();
    Object getReal();
}
