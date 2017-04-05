package com.dzy.onedriveclient.model;

import java.util.List;


public interface IFileModel {

    List<IBaseFileBean> getChildren(IBaseFileBean bean);
    void delete(IBaseFileBean bean);
    void copy(IBaseFileBean from,IBaseFileBean to);
    void cut(IBaseFileBean from,IBaseFileBean to);
    void createFolder(IBaseFileBean parent, String name);
    boolean exists(IBaseFileBean bean);
}
