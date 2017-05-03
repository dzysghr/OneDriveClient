package com.dzy.onedriveclient.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dzy.onedriveclient.model.gen.DaoMaster;
import com.dzy.onedriveclient.model.gen.DaoSession;


public class DBModel {

    private DaoMaster mDaoMaster;
    private SQLiteDatabase mDb;
    private Context mContext;
    private DaoSession mDaoSession;

    public DBModel(Context context){
        mContext = context;
        DaoMaster.DevOpenHelper helper =new DaoMaster.DevOpenHelper(context,"bean");
        mDb =helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDb);
        mDaoSession = mDaoMaster.newSession();
    }



    public DaoSession getDaoSession(){
        return mDaoSession;
    }

    public void close(){
        if (mDb!=null&&mDb.isOpen()){
            mDb.close();
        }
    }
}
