package com.dzy.onedriveclient.model.drive;

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
    TokenBean getToken(@Field("client_id") String clientid,
                       @Field("redirect_uri") String redirect_uri,
                       @Field("client_secret") String client_secret,
                       @Field("code") String code,
                       @Field("grant_type") String authorization_code
    );

//    POST https://login.microsoftonline.com/common/oauth2/v2.0/token
//    Content-Type: application/x-www-form-urlencoded
//    client_id={client_id}&redirect_uri={redirect_uri}&client_secret={client_secret}
//    &refresh_token={refresh_token}&grant_type=refresh_token
    @POST("token")
    @FormUrlEncoded
    TokenBean RefreshToken(@Field("client_id") String clientid,
                       @Field("redirect_uri") String redirect_uri,
                       @Field("client_secret") String client_secret,
                       @Field("refresh_token") String refresh_Token,
                       @Field("grant_type") String authorization_code
    );

}
