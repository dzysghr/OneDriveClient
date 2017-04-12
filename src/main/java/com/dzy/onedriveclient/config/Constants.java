package com.dzy.onedriveclient.config;

import com.dzy.onedriveclient.model.drive.TokenBean;

/**
 * Created by dzysg on 2017/4/1 0001.
 */

public final class Constants {


    public static final String INTENT_KEY_COM_TYPE = "company";
    public static final String INTENT_VALUE_COM_ONEDRIVE = "onedrive";
    public static final String INTENT_VALUE_COM_GOOGLE = "googledrive";

    public static String REDIRECT_URI = "https://login.microsoftonline.com/common/oauth2/nativeclient";
    public static final String BASE_URL = "https://graph.microsoft.com/v1.0/me/";
    public static final String BASE_OAUTH = "https://login.microsoftonline.com/common/oauth2/v2.0/";

    public static final String APP_ID = "435ea474-864a-4da7-bc0e-caa5223eedf0";

    public static final String APP_KEY ="sP2EdekTf3chQd2W6p2A23L";

    public static final String GRANT_TYPE_CODE = "authorization_code";
    public static final String GRANT_TYPE_REFRESH = "refresh_token";



    public static String CODE = "";

    public static TokenBean sToken;

}
