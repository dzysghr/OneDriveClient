package com.dzy.onedriveclient.utils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class RxHelper {

    public static <T> ObservableTransformer<T, T> io_main() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
//
//
//    //检查网络
//    public static <T> Observable.Transformer<BaseResult<T>, BaseResult<T>> checkNetWork() {
//        return new Observable.Transformer<BaseResult<T>, BaseResult<T>>() {
//            @Override
//            public Observable<BaseResult<T>> call(Observable<BaseResult<T>> tObservable) {
//                if (NetworkUtils.isNetworkConnected(MyApp.getAppContext())) {
//                    return tObservable;
//                } else {
//                    return Observable.error(new RuntimeException("当前无网络"));
//                }
//            }
//        };
//    }
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


    public static <T> Observable<T> create(final IFun<T> f){
       return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                try{
                    e.onNext(f.fun());
                    e.onComplete();
                }catch (Exception ex){
                    e.onError(ex);
                }
            }
        });
    }

    public static <T> Observable<T> io_main(Observable<T> ob){
        return ob.subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.io());
    }

    public interface IFun <T>{
        T fun();
    }
}