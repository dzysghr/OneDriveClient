package com.dzy.onedriveclient.utils;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.config.BaseApplication;
import com.dzy.onedriveclient.config.Constants;

/**
 * Created by dzysg on 2017/5/10 0010.
 */

public final class StringHelper {

    public static String[] mPictureEndwith;


    private StringHelper() {
    }

    public static String makeDownloadUrl(String id) {
        return Constants.BASE_URL + "drive/items/{item-id}/content".replace("{item-id}", id);
    }


    public static boolean isPicture(String name){
        if (mPictureEndwith==null){
            mPictureEndwith =  BaseApplication.getApp().getResources().getStringArray(R.array.fileEndingImage);
        }
        return OpenFileHelper.checkEndsWithInStringArray(name.toLowerCase(),mPictureEndwith);
    }
}
