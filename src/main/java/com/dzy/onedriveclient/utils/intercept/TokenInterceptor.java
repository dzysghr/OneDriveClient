package com.dzy.onedriveclient.utils.intercept;

import android.util.Log;

import com.dzy.onedriveclient.config.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dzysg on 2017/5/9 0009.
 */

public class TokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Log.e("okHTTP", "request url " + request.url().toString());
        Request.Builder builder = request.newBuilder();
        if (Constants.sToken != null && !request.url().toString().contains("api.onedrive.com")) {
            builder.addHeader("Authorization", "Bearer " + (Constants.sToken == null ? "" : Constants.sToken.getAccess_token()));
        }
        request = builder.build();
        return chain.proceed(request);
    }
}
