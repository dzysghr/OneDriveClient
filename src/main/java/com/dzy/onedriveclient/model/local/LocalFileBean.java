package com.dzy.onedriveclient.model.local;

import com.dzy.onedriveclient.model.IBaseFileBean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LocalFileBean implements IBaseFileBean{

    private File mFile;
    private String mLastModifyDateTime;
    private LocalFileBean mParent;
    private static SimpleDateFormat sFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public LocalFileBean(File file) {
        mFile = file;
    }

    @Override
    public String getPath() {
        return mFile.getAbsolutePath();
    }

    @Override
    public String getName() {
        return mFile.getName();
    }

    @Override
    public String getType() {
        String name = mFile.getName();
        int index = name.lastIndexOf(File.pathSeparatorChar);
        if (index>-1){
            return name.substring(index,name.length());
        }else{
            return "unknown";
        }
    }

    @Override
    public boolean isFolder() {
        return mFile.isDirectory();
    }

    @Override
    public String getModifyDateTime() {
        if (mLastModifyDateTime==null){
            File f = mFile;
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(f.lastModified());
            mLastModifyDateTime = sFormat.format(cal.getTime());
        }
        return mLastModifyDateTime;
    }

    @Override
    public int getSize() {
        return (int) (mFile.length()/1024);
    }

    @Override
    public Object getReal() {
        return mFile;
    }

    @Override
    public String toString() {
        return mFile.toString();
    }
}
