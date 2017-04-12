package com.dzy.onedriveclient.model.drive;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TokenBean {
    @Id
    private Long id;
    private String token_type;
    private String expires_in;
    private String scope;
    private String access_token;
    private String refresh_token;

    @Generated(hash = 393919956)
    public TokenBean(Long id, String token_type, String expires_in, String scope,
            String access_token, String refresh_token) {
        this.id = id;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.scope = scope;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }

    @Generated(hash = 1886787915)
    public TokenBean() {
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
