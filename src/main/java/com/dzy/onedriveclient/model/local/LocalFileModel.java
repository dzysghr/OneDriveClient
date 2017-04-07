package com.dzy.onedriveclient.model.local;

import android.os.Environment;
import android.util.Log;

import com.dzy.commemlib.utils.FileUtils;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.IFileModel;
import com.dzy.onedriveclient.utils.RxHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static android.content.ContentValues.TAG;


public class LocalFileModel implements IFileModel {

    private File mRoot;

    public LocalFileModel() {
        mRoot = Environment.getExternalStorageDirectory();
        Log.e(TAG, "LocalFileModel:" + mRoot.getAbsolutePath());
    }

    @Override
    public Observable<List<IBaseFileBean>> getChildren(final IBaseFileBean bean) {
        return RxHelper.create(new RxHelper.IFun<List<IBaseFileBean>>() {
            @Override
            public List<IBaseFileBean> fun() {
                if (bean == null) {
                    return genChild(mRoot);
                }
                return genChild((File) bean.getReal());
            }
        });
    }

    @Override
    public Observable<Boolean> delete(final IBaseFileBean bean) {
        return RxHelper.create(new RxHelper.IFun<Boolean>() {
            @Override
            public Boolean fun() {
                File file = (File) bean.getReal();
                if (!file.exists()) {
                    return false;
                }
                if (file.isFile()) {
                    return file.delete();
                } else {
                    FileUtils.deleteFolder(file);
                }
                return true;
            }
        });
        // TODO: 2017/4/4 0004  model层应该抛异常让上层知道操作失败的原因
    }

    @Override
    public Observable<Boolean> copy(final IBaseFileBean from, final IBaseFileBean to) {

        return RxHelper.create(new RxHelper.IFun<Boolean>() {
            @Override
            public Boolean fun() {
                if (!from.isFolder() && to.isFolder()) {
                    FileUtils.safeNIOTransferCopy((File) from.getReal(), new File((File) to.getReal(), from.getName()));
                } else if (from.isFolder() && to.isFolder()) {
                    FileUtils.copyDirectory((File) from.getReal(), new File((File) to.getReal(), from.getName()));
                }
                return true;
            }
        });
    }

    @Override
    public Observable<Boolean> cut(IBaseFileBean from, IBaseFileBean to) {
        return Observable.mergeArray(copy(from, to), delete(from));

    }

    @Override
    public Observable<Boolean> createFolder(final IBaseFileBean parent, final String name) {
        return RxHelper.create(new RxHelper.IFun<Boolean>() {
            @Override
            public Boolean fun() {
                FileUtils.createFolder(new File((File) parent.getReal(), name));
                return true;
            }
        });
    }

    @Override
    public Observable<Boolean> exists(final IBaseFileBean bean) {
        return RxHelper.create(new RxHelper.IFun<Boolean>() {
            @Override
            public Boolean fun() {
                File file = (File) bean.getReal();
                return file.exists();
            }
        });

    }

    @Override
    public Observable<IBaseFileBean> getRoot() {
        return RxHelper.create(new RxHelper.IFun<IBaseFileBean>() {
            @Override
            public IBaseFileBean fun() {
                return new LocalFileBean(mRoot);
            }
        });
    }

    private static List<IBaseFileBean> genChild(File file) {
        File[] array = file.listFiles();
        List<IBaseFileBean> list = new ArrayList<>();
        for (File item : array) {
            list.add(new LocalFileBean(item));
        }
        return list;
    }


}
