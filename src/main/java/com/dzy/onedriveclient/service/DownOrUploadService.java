package com.dzy.onedriveclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.dzy.commemlib.rxbus.RxBus;
import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.event.UploadEvent;
import com.dzy.onedriveclient.transfer.CoreContext;
import com.dzy.onedriveclient.transfer.DownloadManager;
import com.dzy.onedriveclient.transfer.TaskHandle;
import com.dzy.onedriveclient.event.DownloadEvent;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.model.drive.DriveFile;
import com.dzy.onedriveclient.model.local.LocalFileBean;
import com.dzy.onedriveclient.transfer.UploadManager;

import java.io.File;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class DownOrUploadService extends Service {

    private static final String TAG = "DownOrUploadService";

    private static DownloadManager mDownloadManager;
    private static UploadManager mUploadManager;

    private CompositeDisposable mDisposables = new CompositeDisposable();;

    public static DownloadManager getDownloadManager() {
        return mDownloadManager;
    }

    public static UploadManager getUploadManager() {
        return mUploadManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CoreContext context = new CoreContext
                .Builder(this)
                .client(ModelFactory.getOkHttpClient())
                .dao(ModelFactory.getDBModel().getDaoSession())
                .executor(Executors.newCachedThreadPool())
                .build();
        mDownloadManager = new DownloadManager(context);
        mDownloadManager.init();


        mUploadManager = new UploadManager(context);
        mUploadManager.init();

          Disposable disposable = RxBus.getDefault().toObservable(DownloadEvent.class)
                .subscribe(new Consumer<DownloadEvent>() {
                    @Override
                    public void accept(@NonNull DownloadEvent downloadEvent) throws Exception {
                        newDownloadTask(downloadEvent);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "onDownloadEvent: ", throwable);
                    }
                });
        mDisposables.add(disposable);

        disposable = RxBus.getDefault().toObservable(UploadEvent.class)
                .subscribe(new Consumer<UploadEvent>() {
                    @Override
                    public void accept(@NonNull UploadEvent event) throws Exception {
                        newUploadTask(event);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "onUpEvent: ", throwable);
                    }
                });
        mDisposables.add(disposable);
    }


    private void newUploadTask(UploadEvent event) {
        String fileid = event.to == null ? null : ((DriveFile) event.to).getId();
        File file = (File) event.from.getReal();
        mUploadManager.createTask(fileid,file.getPath())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TaskHandle>() {
                    @Override
                    public void accept(@NonNull TaskHandle handle) throws Exception {
                        handle.start();
                        toast("已添加到队列");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "createTask: ", throwable);
                        toast(throwable.getMessage());
                    }
                });
    }

    private void newDownloadTask(DownloadEvent downloadEvent) {
        if (downloadEvent.to == null) {
            downloadEvent.to = new LocalFileBean(Environment.getExternalStorageDirectory());
        }
        DriveFile file = (DriveFile) downloadEvent.from;
        String path = downloadEvent.to.getPath();

        File target = new File(path, file.getName());
        if (target.exists()) {
            target = new File(path, file.getName() + "-1");
        }
        mDownloadManager.createTask(makeDownloadUrl(file.getId()), target.getPath())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TaskHandle>() {
                    @Override
                    public void accept(@NonNull TaskHandle handle) throws Exception {
                        handle.start();
                        toast("已添加到队列");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "createTask: ", throwable);
                        toast(throwable.getMessage());
                    }
                });
    }

    private String makeDownloadUrl(String id) {
        return Constants.BASE_URL + "drive/items/{item-id}/content".replace("{item-id}", id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposables.dispose();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("DownOrUploadService", "DownOrUploadService start");
        return super.onStartCommand(intent, flags, startId);
    }

    private void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
