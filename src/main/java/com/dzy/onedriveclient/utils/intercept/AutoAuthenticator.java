package com.dzy.onedriveclient.utils.intercept;

import android.util.Log;

import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.model.drive.TokenBean;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by dzysg on 2017/5/9 0009.
 */

public class AutoAuthenticator implements Authenticator {

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        Log.e("AutoAuthenticator", "Token过期，自动刷新token");
        TokenBean bean = Constants.sToken;
        if (bean==null){
            bean = ModelFactory.getTokenModel().getTokenFromDb();
        }
        if (bean!=null){
            Constants.sToken = ModelFactory.getTokenModel().refreshToken(bean).blockingFirst();
            Log.e("AutoAuthenticator", "Token刷新成功");
            return response.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + Constants.sToken.getAccess_token())
                    .build();
        }
        return null;
    }
}
