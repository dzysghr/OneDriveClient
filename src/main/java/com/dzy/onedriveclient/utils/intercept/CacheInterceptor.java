package com.dzy.onedriveclient.utils.intercept;

import android.util.Log;

import com.dzy.commemlib.utils.NetworkUtils;
import com.dzy.onedriveclient.config.BaseApplication;
import com.dzy.onedriveclient.config.Constants;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dzysg on 2017/5/9 0009.
 */

public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean isAuth = false;
        //非认证且无网络，强制使用cache
        if (!request.url().toString().startsWith(Constants.BASE_OAUTH)) {
            if (!NetworkUtils.isNetworkConnected(BaseApplication.getApp())) {
                Log.e("CacheInterceptor", "intercept: force cache");
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                return chain.proceed(request);
            }else{
                request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
            }
        }else{
            isAuth = true;
        }
        if (isAuth){
            return chain.proceed(request)
                    .newBuilder()
                    .header("Cache-Control","public,max-age=3600")
                    .removeHeader("Pragma").build();
        }
       return chain.proceed(request);

    }
}
