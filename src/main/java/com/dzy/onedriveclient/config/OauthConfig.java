package com.dzy.onedriveclient.config;

/**
 * Created by dzysg on 2017/4/2 0002.
 */

public class OauthConfig {

    private String mBaseUrl;
    private String mClientId;
    private String mRedirectUrl;
    private String mScope;
    private String mResponse_type;

    private OauthConfig() {
    }

    public String toUrl(){
        return mBaseUrl+"client_id="+mClientId+"&scope="+mScope+"&response_type="+mResponse_type+"&redirect_uri"+mRedirectUrl;
    }



    public static OauthConfig create(String type) {
        type = type.toLowerCase();
        switch (type) {
            case "onedrive":
                return new Builder()
                        .baseUrl("https://login.microsoftonline.com/common/oauth2/v2.0/authorize?")
                        .clientId(Constants.APP_ID)
                        .redirectUrl("http://localhost")
                        .responseType("code")
                        .scope("files.readwrite offline_access")
                        .Build();
            default:
                break;
        }
        return null;
    }


    protected static class Builder {

        private OauthConfig mOauthConfig = new OauthConfig();

        public Builder baseUrl(String url) {
            mOauthConfig.mBaseUrl = url;
            return this;
        }

        public Builder clientId(String id) {
            mOauthConfig.mClientId = id;
            return this;
        }

        public Builder redirectUrl(String redirectUrl) {
            mOauthConfig.mRedirectUrl = redirectUrl;
            return this;
        }

        public Builder scope(String scope) {
            mOauthConfig.mScope = scope;
            return this;
        }

        public Builder responseType(String responseType) {
            mOauthConfig.mResponse_type = responseType;
            return this;
        }

        public OauthConfig Build() {
            return mOauthConfig;
        }

    }

}
