package com.dzy.onedriveclient.model;

public interface IBaseFileBean {

    String getPath();
    String getName();
    String getType();
    boolean isFolder();
    String getModifyDateTime();
    int getSize();
    Object getReal();
}
