package com.dzy.onedriveclient.module;

import android.util.Log;

import com.dzy.commemlib.utils.NetworkUtils;
import com.dzy.onedriveclient.core.BaseActivity;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.utils.StringHelper;

import java.io.IOException;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static com.dzy.onedriveclient.config.Constants.KEY_ID;


public abstract class BaseMediaActivity extends BaseActivity {

    private Call mCall;
    protected String mId;

    protected void loadData(){
        if (!NetworkUtils.isNetworkConnected(this)){
            Toast("当前无网络");
            finish();
        }
        String id = getIntent().getStringExtra(KEY_ID);
        mId = id;
        if (id==null){
            finish();
            return;
        }
        String url = StringHelper.makeDownloadUrl(id);
        Request request = new Request.Builder().url(url).build();
        mCall =  ModelFactory.getOkHttpClient().newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                _Error();
                Log.e(TAG, "onFailure: ", e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
                Response pre = response.priorResponse();
                final String url = pre.header("Location");
                if (url==null){
                    _Error();
                }else{
                    Log.e(TAG, "onResponse: " + url);
                    _Succeed(url);
                }
            }
        });
    }

    private void _Error(){
        runOnUiThread(new TimerTask() {
            @Override
            public void run() {
                loadError();
            }
        });
    }

    private void _Succeed(final String url){
        runOnUiThread(new TimerTask() {
            @Override
            public void run() {
                loadSucceed(url);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCall.cancel();
    }

    protected abstract void loadError();

    protected abstract void loadSucceed(final String url);


    @Override
    protected IBasePresenter initPresenter() {
        return null;
    }
}
