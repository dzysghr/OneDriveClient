package com.dzy.onedriveclient.module.file;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.dzy.onedriveclient.R;


public class CreateFolderDialog extends Dialog {


    private DialogListener mDialogListener;

    public CreateFolderDialog(@NonNull Context context) {
        super(context);
    }

    public CreateFolderDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected CreateFolderDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dailog_create_folder);
        setTitle("新建文件夹");
        final EditText editText = (EditText) findViewById(R.id.edt_folder);

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                       if (mDialogListener!=null){
                           mDialogListener.onOK(editText.getText().toString());
                       }
                }
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateFolderDialog.this.cancel();
            }
        });
    }

    public DialogListener getDialogListener() {
        return mDialogListener;
    }

    public void setDialogListener(DialogListener dialogListener) {
        mDialogListener = dialogListener;
    }

    public interface DialogListener{
        void onOK(String name);
    }
}
