package com.dzy.onedriveclient.utils;

import android.util.Log;

import com.dzy.commemlib.utils.NetworkUtils;
import com.dzy.onedriveclient.config.BaseApplication;
import com.dzy.onedriveclient.model.HTTPException;
import com.dzy.onedriveclient.model.drive.DriveItem;
import com.dzy.onedriveclient.model.drive.ResultBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static android.content.ContentValues.TAG;


public class RxHelper {

    private static Gson gson = new Gson();

    public static <T> ObservableTransformer<T, T> io_main() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static ObservableTransformer<Response<ResponseBody>,List<DriveItem>> handleChildren() {
        return new ObservableTransformer<Response<ResponseBody>, List<DriveItem>>() {
            @Override
            public ObservableSource<List<DriveItem>> apply(@NonNull Observable<Response<ResponseBody>> upstream) {
                return  upstream.flatMap(new Function<Response<ResponseBody>, ObservableSource<List<DriveItem>>>() {
                    @Override
                    public ObservableSource<List<DriveItem>> apply(@NonNull Response<ResponseBody> response) throws Exception {
                        String body;
                        int code = response.code();
                        if (code==200){
                            body = response.body().string();
                            ResultBean bean = gson.fromJson(body, ResultBean.class);
                            return Observable.just(bean.value);
                        }else {
                            Log.e(TAG, "request error:"+response.toString());
                            Log.e(TAG, "request error:"+response.errorBody().string());
                            return Observable.error(new HTTPException(code));
                        }
                    }
                });
            }
        };
    }

    public static <T> ObservableTransformer<Response<ResponseBody>,T> handle(final TypeToken<T> typeToken) {
        return new ObservableTransformer<Response<ResponseBody>,T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<Response<ResponseBody>> upstream) {
                return  upstream.flatMap(new Function<Response<ResponseBody>, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(@NonNull Response<ResponseBody> response) throws Exception {
                        String body;
                        int code = response.code();
                        if (code==200){
                            body = response.body().string();
                            T bean = gson.fromJson(body,typeToken.getType());
                            return Observable.just(bean);
                        }else {
                            Log.e(TAG, "request error:"+response.errorBody().string());
                            return Observable.error(new HTTPException(code));
                        }
                    }
                });
            }
        };
    }




//    public static <T> ObservableTransformer<Response<ResponseBody>, T> handle(final Type type) {
//        return new ObservableTransformer<Response<ResponseBody>, T>() {
//            @Override
//            public ObservableSource<T> apply(@NonNull Observable<Response<ResponseBody>> upstream) {
//                return  upstream.flatMap(new Function<Response<ResponseBody>, ObservableSource<T>>() {
//                    @Override
//                    public ObservableSource<T> apply(@NonNull Response<ResponseBody> response) throws Exception {
//                        String body="";
//                        if (response.code()==200){
//                            body = response.body().string();
//                            ResultBean bean = gson.fromJson(body, ResultBean.class);
//                            return Observable.just(bean.value);
//                        }else{
//                            // TODO: 2017/4/9 0009 这里根据返回码确定错误类型
//                            Log.e(TAG, "request error:"+response.errorBody().string());
//                            return Observable.error(new RuntimeException("网络请求错误"));
//                        }
//                    }
//                });
//            }
//        };
//    }

    //
//
    public static <T> ObservableTransformer<T, T> checkNetwork() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {

                if (NetworkUtils.isNetworkConnected(BaseApplication.getApp())) {
                    return upstream;
                } else {
                    return Observable.error(new RuntimeException("网络错误"));
                }

            }
        };
    }
//
//
//    //处理返回码
//    public static <T> Observable.Transformer<BaseResult<T>, T> handleResult() {
//        return new Observable.Transformer<BaseResult<T>, T>() {
//            @Override
//            public Observable<T> call(Observable<BaseResult<T>> tObservable) {
//                return tObservable.flatMap(new Func1<BaseResult<T>, Observable<T>>() {
//                    @Override
//                    public Observable<T> call(BaseResult<T> result) {
//                        //如果成功,反回正常数据
//                        if (result.getState_code() == 0) {
//                            return Observable.just(result.getData());
//                        }
//                        //如果session失效
//                        else if (result.getState_code() == 3) {
//                            MyApp.getSessionManager().saveSeesion(null);
//                            return Observable.error(new SessionException(result.getError_msg()));
//                        } else {
//                            return Observable.error(new RuntimeException(result.getError_msg()));
//                        }
//                    }
//                });
//            }
//        };
//    }


    public static <T> Observable<T> create(final IFun<T> f) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                e.onNext(f.fun());
                e.onComplete();
            }
        });
    }

    public interface IFun<T> {
        T fun();
    }
}