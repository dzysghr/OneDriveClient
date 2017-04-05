
package com.dzy.onedriveclient.module.login;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.dzy.commemlib.utils.LogUtils;
import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.config.OauthConfig;
import com.dzy.onedriveclient.core.BaseActivity;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.module.MainActivity;

public class LoginActivity extends BaseActivity {


    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void initView() {
        mWebView = bindView(R.id.webview);
        mProgressBar = bindView(R.id.progressBar);
    }

    @Override
    protected void setupView() {

        String type = getIntent().getStringExtra(Constants.INTENT_KEY_COM_TYPE);
        if (TextUtils.isEmpty(type)){
            LogUtils.e(TAG,"type is empty");
            finish();
            return;
        }
        OauthConfig config = OauthConfig.create(type);

        //设置webview属性能够执行javascript脚本
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.setWebChromeClient(new WebChromeClient());
        //设置webView可以缩放，只可以双击缩放
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mProgressBar.setVisibility(View.VISIBLE);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtils.d(TAG, url);
                if (url.contains("?code=")) {
                    Constants.CODE = url.substring(url.indexOf("?code=") + "?code=".length());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else if (url.contains("error=access_denied")) {
                    LogUtils.e(TAG, url);
                    Toast(getString(R.string.access_denied));
                }
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                LogUtils.d("newProgress", newProgress + "");
                mProgressBar.setProgress(newProgress);
                if (newProgress >= 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

        });
        mWebView.clearCache(false);
        mWebView.loadUrl(config.toUrl());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected IBasePresenter initPresenter() {
        return null;
    }
}
