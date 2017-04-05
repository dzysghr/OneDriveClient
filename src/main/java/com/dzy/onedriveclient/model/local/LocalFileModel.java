package com.dzy.onedriveclient.model.local;

import android.os.Environment;

import com.dzy.commemlib.utils.FileUtils;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.IFileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class LocalFileModel  implements IFileModel{

    private File mRoot;

    public LocalFileModel(){
        mRoot = Environment.getExternalStorageDirectory();
    }

    @Override
    public List<IBaseFileBean> getChildren(IBaseFileBean bean) {
        if (bean==null){
            return genChild(mRoot);
        }
        return genChild((File) bean.getReal());
    }

    @Override
    public void delete(IBaseFileBean bean) {
        File file = (File) bean.getReal();
        if (!file.exists()){
            return;
        }
        if (file.isFile()){
            file.delete();
        }else{
            FileUtils.deleteFolder(file);
        }
        // TODO: 2017/4/4 0004  model层应该抛异常让M层知道操作失败的原因
    }

    @Override
    public void copy(IBaseFileBean from, IBaseFileBean to){
        if (!from.isFolder()&&to.isFolder()){
            FileUtils.safeNIOTransferCopy((File)from.getReal(),new File((File)to.getReal(),from.getName()));
        }else if(from.isFolder()&&to.isFolder()){
            FileUtils.copyDirectory((File)from.getReal(),new File((File)to.getReal(),from.getName()));
        }
    }

    @Override
    public void cut(IBaseFileBean from, IBaseFileBean to) {
        copy(from,to);
        delete(from);
    }

    @Override
    public void createFolder(IBaseFileBean parent, String name) {
        FileUtils.createFolder(new File((File) parent.getReal(), name));
    }


    private static List<IBaseFileBean> genChild(File file){
         File[] array = file.listFiles();
        List<IBaseFileBean> list = new ArrayList<>();
        for (File item:array){
            list.add(new LocalFileBean(item));
        }
        return list;
    }


}
