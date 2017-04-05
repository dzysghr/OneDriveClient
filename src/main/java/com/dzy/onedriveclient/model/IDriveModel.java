package com.dzy.onedriveclient.model;

import com.dzy.onedriveclient.model.drivebean.DriveItem;
import com.dzy.onedriveclient.model.drivebean.ResultBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by dzysg on 2017/4/2 0002.
 */

public interface IDriveModel {


    @GET("drive/root/children")
    Call<ResultBean> getRootList(@Query("select") String select,
                                 @Query("top") String top,
                                 @Query("skipToken") String skip);

    @GET("/drive/items/{item-id}")
    Call<DriveItem> getDriveItemInfo(@Query("select") String select,
                                     @Path("item-id") int id);

}
