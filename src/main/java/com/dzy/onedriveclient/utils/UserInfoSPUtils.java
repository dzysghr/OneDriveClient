package com.dzy.onedriveclient.utils;

import android.content.SharedPreferences;

import com.dzy.onedriveclient.config.BaseApplication;

/**
 * Created by dzysg on 2017/5/7 0007.
 */

public final class UserInfoSPUtils {
    private UserInfoSPUtils(){};

    private static final String SP ="USER";
    private static final String KEY_USERNAME = "username";


    public static void setUser(String username){
        SharedPreferences preferences = BaseApplication.getApp().getSharedPreferences(SP,0);
        preferences.edit().putString(KEY_USERNAME,username).apply();
    }

    public static String getUser(){
        SharedPreferences preferences = BaseApplication.getApp().getSharedPreferences(SP,0);
        return preferences.getString(KEY_USERNAME,null);
    }
}
