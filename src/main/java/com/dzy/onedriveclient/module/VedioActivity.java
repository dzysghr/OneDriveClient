package com.dzy.onedriveclient.module;

import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import com.dzy.onedriveclient.R;

public class VedioActivity extends BaseMediaActivity {


    private TextView mTvMsg;
    private boolean mSucceed = false;

    @Override
    protected void initView() {
        mTvMsg = bindView(R.id.tv_msg);
    }

    @Override
    protected void setupView() {
        loadData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vedio;
    }


    @Override
    protected void loadError() {
        mTvMsg.setText("解析错误");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSucceed){
            finish();
        }
    }

    @Override
    protected void loadSucceed(String url) {
        mSucceed = true;
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.parse(url);
        intent.setDataAndType(uri, "video/*");
        startActivity(intent);
    }
}
