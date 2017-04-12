package com.dzy.onedriveclient.module.file.online;

import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.drive.OneDriveFileModel;
import com.dzy.onedriveclient.module.file.IFilePresenter;
import com.dzy.onedriveclient.module.file.local.LocalFileFragment;

public class DriveFragment extends LocalFileFragment {

    private IFilePresenter mIFilePresenter;

    @Override
    protected IBasePresenter initPresenter() {
        mFilePresenter = new DrivePresenter(new OneDriveFileModel());
        return mFilePresenter;
    }
}
