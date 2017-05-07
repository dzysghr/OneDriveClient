package com.dzy.onedriveclient.model.drive;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;

/**
 * Created by dzysg on 2017/5/7 0007.
 */

public interface IUserModel {
    @GET("drive")
    Observable<Response<ResponseBody>> getDrive();
}
