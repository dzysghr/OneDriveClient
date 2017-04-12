package com.dzy.onedriveclient.model.drive;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IDriveFileModel {


    @GET("drive/{path}/children")
    Observable<Response<ResponseBody>> getListByPath(
                    @Path("path") String path,
                    @Query("select") String select,
                    @Query("top") int top);

    @GET("drive/items/{id}/children")
    Observable<Response<ResponseBody>> getListById(
            @Path(value = "id",encoded = true) String id,
            @Query("select") String select,
            @Query("top") int top);

    @GET("drive/items/{item-id}")
    Observable<Response<ResponseBody>> getDriveItemInfo(
            @Query("select") String select,
            @Path("item-id") int id);

    @POST("drive/items/{id}/children")
    Observable<Response<ResponseBody>> createFolder(@Path("id") String parentId,@Body RequestBody body);

    @POST("drive/{path}/children")
    Observable<Response<ResponseBody>> createFolderByPath(@Path("path") String path,@Body RequestBody body);
}
