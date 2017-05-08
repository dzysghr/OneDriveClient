package com.dzy.onedriveclient.model;

import android.util.Log;

import com.dzy.onedriveclient.config.BaseApplication;
import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.model.drive.IDriveFileModel;
import com.dzy.onedriveclient.model.drive.IOAuthModel;
import com.dzy.onedriveclient.model.drive.TokenModel;
import com.dzy.onedriveclient.model.drive.IUserModel;
import com.dzy.onedriveclient.utils.UserInfoSPUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ModelFactory {


    private static OkHttpClient sOkHttpClient;

    private static IOAuthModel sIOAuthModel;

    private static IDriveFileModel sIDriveFileModel;

    private static DBModel sDBModel;

    private static TokenModel sTokenModel;

    private static IUserModel sUserModel;

    public static synchronized OkHttpClient getOkHttpClient(){
        if (sOkHttpClient==null){
            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    try {

                        Request request = chain.request();
                        Log.e("OKHTTP", "request url "+request.url().toString());
                        Request.Builder builder = request.newBuilder();
                        if (Constants.sToken!=null&&!request.url().toString().contains("api.onedrive.com")){
                            builder.addHeader("Authorization", "Bearer " + (Constants.sToken == null ? "" : Constants.sToken.getAccess_token()));
                        }
                        request = builder.build();
                        return chain.proceed(request);
                    } catch ( IOException e) {
                        Log.e("intercept","");
                        return null;
                    }
                }
            };
            sOkHttpClient = new OkHttpClient.Builder().writeTimeout(100, TimeUnit.SECONDS).addNetworkInterceptor(interceptor).build();
        }
        return sOkHttpClient;
    }

    public static synchronized IOAuthModel getOAuthModel(){
        if (sIOAuthModel==null){
             Retrofit retrofit = new Retrofit.Builder()
                     .client(getOkHttpClient())
                     .baseUrl(Constants.BASE_OAUTH)
                     .addConverterFactory(GsonConverterFactory.create())
                     .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                     .build();

            sIOAuthModel =  retrofit.create(IOAuthModel.class);
        }
        return sIOAuthModel;
    }

    public static synchronized IDriveFileModel getDriveFileModel(){
        if (sIDriveFileModel==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .client(getOkHttpClient())
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            sIDriveFileModel =  retrofit.create(IDriveFileModel.class);
        }
        return sIDriveFileModel;
    }

    public static synchronized DBModel getDBModel() {
        if (sDBModel==null){
            sDBModel = new DBModel(BaseApplication.getApp(),UserInfoSPUtils.getUser());
        }
        return sDBModel;
    }

    public static void setDBModel(DBModel db) {
        if (sDBModel!=null){
            if (sDBModel.getDBName().equals(db.getDBName())){
                return;
            }
            sDBModel.close();
        }
        sTokenModel = new TokenModel(db);
        sDBModel = db;
    }

    public static synchronized TokenModel getTokenModel(){
        if (sTokenModel==null){
            sTokenModel = new TokenModel(getDBModel());
        }
        return sTokenModel;
    }

    public static IUserModel getsUserModel() {
        if (sUserModel==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .client(getOkHttpClient())
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            sUserModel =  retrofit.create(IUserModel.class);
        }
        return sUserModel;
    }
}
