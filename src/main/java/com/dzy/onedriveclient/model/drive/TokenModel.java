package com.dzy.onedriveclient.model.drive;


import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.model.DBModel;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.model.gen.TokenBeanDao;
import com.dzy.onedriveclient.utils.RxHelper;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class TokenModel {


    private DBModel mDBModel;
    private TokenBeanDao mTokenBeanDao;
    private IOAuthModel mAuthModel;

    public TokenModel() {
        mDBModel = ModelFactory.getDBModel();
        mTokenBeanDao = mDBModel.getDaoSession().getTokenBeanDao();
        mAuthModel = ModelFactory.getOAuthModel();
    }

    public TokenBean getTokenFromDb() {
        List<TokenBean> list = mTokenBeanDao.loadAll();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public void saveToken(TokenBean bean) {
        mTokenBeanDao.deleteAll();
        mTokenBeanDao.insert(bean);
    }


    public Observable<TokenBean> refreshToken(TokenBean bean) {
        return mAuthModel.RefreshToken(Constants.APP_ID, Constants.REDIRECT_URI, bean.getRefresh_token(), Constants.GRANT_TYPE_REFRESH)
                .compose(RxHelper.handle(new TypeToken<TokenBean>(){}))
                .doOnNext(new Consumer<TokenBean>() {
                    @Override
                    public void accept(@NonNull TokenBean bean) throws Exception {
                        saveToken(bean);
                    }
                });
    }

    public Observable<TokenBean> getToken(String code) {
        return mAuthModel.getToken(Constants.APP_ID, Constants.REDIRECT_URI, code, Constants.GRANT_TYPE_CODE)
                .compose(RxHelper.handle(new TypeToken<TokenBean>(){}))
                .doOnNext(new Consumer<TokenBean>() {
                    @Override
                    public void accept(@NonNull TokenBean bean) throws Exception {
                        saveToken(bean);
                    }
                });
    }

}
