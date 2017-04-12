package com.dzy.onedriveclient.utils;

import com.dzy.onedriveclient.model.HTTPException;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public abstract class RxErrorHandler implements Consumer<Throwable> {

    @Override
    public void accept(@NonNull Throwable throwable) throws Exception {
        if (throwable instanceof HTTPException){
            int code = ((HTTPException) throwable).code;
            switch (code){
                case 401:tokenExpire();break;
                default:
            }
        }else{
            onError(throwable.getMessage());
        }
    }

    private void tokenExpire(){



    }

    public abstract void onError(String msg);
}
