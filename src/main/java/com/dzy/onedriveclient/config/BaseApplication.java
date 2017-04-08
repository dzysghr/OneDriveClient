package com.dzy.onedriveclient.config;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;


public class BaseApplication extends Application {

    private static Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance =this;
    }

    public static Application  getApp(){
        return sInstance;
    }

    synchronized boolean goToWifiSettingsIfDisconnected() {
        final NetworkInfo info = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            final Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
