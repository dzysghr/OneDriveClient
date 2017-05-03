package com.dzy.onedriveclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.dzy.commemlib.rxbus.RxBus;
import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.transfer.DownloadContext;
import com.dzy.onedriveclient.transfer.DownloadManager;
import com.dzy.onedriveclient.transfer.TaskHandle;
import com.dzy.onedriveclient.event.DownloadEvent;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.model.drive.DriveFile;
import com.dzy.onedriveclient.model.local.LocalFileBean;

import java.io.File;
import java.util.concurrent.Executors;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class DownloadService extends Service {

    private static final String TAG = "DownloadService";

    private static DownloadManager mDownloadManager;
    private Disposable mDisposabeDownloadEvent;

    public static DownloadManager getDownloadManaer(){
        return mDownloadManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DownloadContext context = new DownloadContext
                .Builder(this)
                .client(ModelFactory.getOkHttpClient())
                .dao(ModelFactory.getDBModel()
                                .getDaoSession()
                                .getTaskInfoDao(),
                        ModelFactory.getDBModel()
                                .getDaoSession()
                                .getThreadInfoDao()
                )
                .executor(Executors.newCachedThreadPool())
                .build();
        mDownloadManager = new DownloadManager(context);
        mDownloadManager.init();

        mDisposabeDownloadEvent= RxBus.getDefault().toObservable(DownloadEvent.class)
                .subscribe(new Consumer<DownloadEvent>() {
                    @Override
                    public void accept(@NonNull DownloadEvent downloadEvent) throws Exception {
                        newTask(downloadEvent);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "onDownloadEvent: ", throwable);
                    }
                });
    }

    private void newTask(DownloadEvent downloadEvent){
        if (downloadEvent.to==null){
            downloadEvent.to = new LocalFileBean(Environment.getExternalStorageDirectory());
        }
        DriveFile file = (DriveFile) downloadEvent.from;
        String path =downloadEvent.to.getPath();

        File target = new File(path,file.getName());
        if (target.exists()){
            target = new File(path,file.getName()+"-1");
        }

        mDownloadManager.createTask(makeDownloadUrl(file.getId()),target.getPath())
                .subscribe(new Consumer<TaskHandle>() {
                    @Override
                    public void accept(@NonNull TaskHandle handle) throws Exception {
                        handle.start();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "createTask: ", throwable);
                        Toast.makeText(DownloadService.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String makeDownloadUrl(String id){
        return Constants.BASE_URL+"drive/items/{item-id}/content".replace("{item-id}",id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposabeDownloadEvent.dispose();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Log.e("DownloadService", "DownloadService start");
        return super.onStartCommand(intent, flags, startId);
    }
}
