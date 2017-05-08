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
    private String mDbName;

    public DBModel(Context context,String dbname){
        mDbName = dbname;
        mContext = context;
        DaoMaster.DevOpenHelper helper =new DaoMaster.DevOpenHelper(context,dbname);
        mDb =helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDb);
        mDaoSession = mDaoMaster.newSession();
    }



    public DaoSession getDaoSession(){
        return mDaoSession;
    }

    public String getDBName(){
        return mDbName;
    }

    public void close(){
        if (mDb!=null&&mDb.isOpen()){
            mDb.close();
        }
    }
}
