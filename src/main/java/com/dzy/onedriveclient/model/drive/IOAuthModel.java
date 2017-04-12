package com.dzy.onedriveclient.model.drive;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;




public interface IOAuthModel {

//    POST https://login.microsoftonline.com/common/oauth2/v2.0/token
//    Content-Type: application/x-www-form-urlencoded
//     client_id={client_id}&redirect_uri={redirect_uri}&client_secret={client_secret}
//     &code={code}&grant_type=authorization_code
    
    @POST("token")
    @FormUrlEncoded
    Observable<Response<ResponseBody>> getToken(@Field("client_id") String clientid,
                             @Field("redirect_uri") String redirect_uri,
                             @Field("code") String code,
                             @Field("grant_type") String grant_type
    );

//    POST https://login.microsoftonline.com/common/oauth2/v2.0/token
//    Content-Type: application/x-www-form-urlencoded
//    client_id={client_id}&redirect_uri={redirect_uri}&client_secret={client_secret}
//    &refresh_token={refresh_token}&grant_type=refresh_token
    @POST("token")
    @FormUrlEncoded
    Observable<Response<ResponseBody>> RefreshToken(@Field("client_id") String clientid,
                                                    @Field("redirect_uri") String redirect_uri,
                                                    @Field("refresh_token") String refresh_Token,
                                                    @Field("grant_type") String grant_type
    );

}
