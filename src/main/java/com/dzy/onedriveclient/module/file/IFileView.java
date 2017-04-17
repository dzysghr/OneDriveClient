package com.dzy.onedriveclient.module.file;

import com.dzy.onedriveclient.core.mvp.IBaseVIew;
import com.dzy.onedriveclient.model.IBaseFileBean;

import java.util.List;

public interface IFileView extends IBaseVIew{

    void showFileList(List<IBaseFileBean> list);
    void showErrorView();
}
