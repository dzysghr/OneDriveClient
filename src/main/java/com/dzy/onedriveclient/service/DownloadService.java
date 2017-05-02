package com.dzy.onedriveclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.dzy.onedriveclient.download.TaskDispatcher;

public class DownloadService extends Service {


    private TaskDispatcher mTaskDispatchers;


    public DownloadService() {

    }


    @Override
    public void onCreate() {
        super.onCreate();



    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
