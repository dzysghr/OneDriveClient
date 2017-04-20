package com.dzy.onedriveclient.model.local;

import android.os.Environment;
import android.util.Log;

import com.dzy.commemlib.utils.FileUtils;
import com.dzy.commemlib.utils.LogUtils;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.IFileModel;
import com.dzy.onedriveclient.utils.RxHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;


public class LocalFileModel implements IFileModel {

    private File mRoot;
    private static String TAG = "LocalFileModel";

    public LocalFileModel() {
        mRoot = Environment.getExternalStorageDirectory();
        Log.e(TAG, "LocalFileModel:" + mRoot.getAbsolutePath());
    }

    @Override
    public Observable<List<IBaseFileBean>> getChildren(String path,int cache) {
        return getChildren(new LocalFileBean(new File(path)),cache);
    }

    @Override
    public Observable<List<IBaseFileBean>> getChildren(final IBaseFileBean bean,int cache) {
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
            public Boolean fun() throws Exception{
                File target = to==null?mRoot:(File)to.getReal();
                if (!target.exists()){
                    LogUtils.d(TAG,"the target is not exists");
                    return false;
                }
                if (target.isFile()){
                    LogUtils.d(TAG,"the target is a file");
                    return false;
                }
                File source = (File) from.getReal();
                if (source.getParent().equals(target.getPath())){
                    return false;
                }

                if (source.isFile()) {//文件复制
                    FileUtils.nioTransferCopy(source,new File(target,source.getName()));
                } else {//文件夹复制
                    FileUtils.copyDirectory(source,new File(target,source.getName()));
                }
                return true;
            }
        });
    }

    @Override
    public Observable<Boolean> cut(final IBaseFileBean from, IBaseFileBean to) {
        return copy(from, to).flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(@NonNull Boolean aBoolean) throws Exception {
                if (aBoolean){
                    return delete(from);
                }else{
                    return Observable.just(false);
                }
            }
        });
    }

    @Override
    public Observable<IBaseFileBean> createFolder(final IBaseFileBean parent, final String name) {
        return RxHelper.create(new RxHelper.IFun<IBaseFileBean>() {
            @Override
            public IBaseFileBean fun() {
                File file = mRoot;
                if (parent!=null){
                    file = (File) parent.getReal();
                }
                file = new File(file,name);
                FileUtils.createFolder(file);
                return new LocalFileBean(file);
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

    private static List<IBaseFileBean> genChild(File file) {
        List<IBaseFileBean> list = new ArrayList<>();
        File[] array = file.listFiles();
        if (array==null){
            return list;
        }
        for (File item : array) {
            list.add(new LocalFileBean(item));
        }
        return list;
    }
}
