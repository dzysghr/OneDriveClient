package com.dzy.onedriveclient.utils.intercept;

import android.text.TextUtils;
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
        if (shouldAddToken(request)) {
            Request.Builder builder = request.newBuilder();
            builder.addHeader("Authorization", "Bearer " + Constants.sToken.getAccess_token());
            return chain.proceed(builder.build());
        }
        return chain.proceed(request);
    }

    private boolean shouldAddToken(Request request) {
        if (request.url().toString().startsWith(Constants.BASE_OAUTH)||request.url().toString().startsWith("https://api.onedrive.com")) {
            return false;
        }
        if (!TextUtils.isEmpty(request.header("Authorization"))) {
            return false;
        }
        return Constants.sToken != null;
    }
}
