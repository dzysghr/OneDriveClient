package com.dzy.onedriveclient.module.file;

import com.dzy.onedriveclient.core.mvp.IBaseVIew;
import com.dzy.onedriveclient.model.IBaseFileBean;

import java.util.List;

/**
 * Created by dzysg on 2017/4/3 0003.
 */

public interface IFileView extends IBaseVIew{

    void showFileList(List<IBaseFileBean> list);
    void showTitleAndParent(String title,String parent);
}
