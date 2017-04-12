package com.dzy.onedriveclient.module.file.local;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dzy.commemlib.ui.BaseAdapter.CommonAdapter;
import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.local.LocalFileModel;
import com.dzy.onedriveclient.module.file.CreateFolderDialog;
import com.dzy.onedriveclient.module.file.FileListAdapter;
import com.dzy.onedriveclient.module.file.IFilePresenter;
import com.dzy.onedriveclient.module.file.IFileView;

import java.util.List;


public class LocalFileFragment extends BaseFragment implements IFileView, Toolbar.OnMenuItemClickListener {

    private RecyclerView mRecyclerView;
    private TextView mTvTitle;
    private TextView mTvBack;
    protected FileListAdapter mAdapter;
    protected IFilePresenter mFilePresenter;

    @Override
    protected void initView() {
        mRecyclerView = bindView(R.id.local_recycleView);
        mTvTitle = bindView(R.id.tv_title);
        mTvBack = bindView(R.id.tv_back);
    }

    @Override
    protected void setupView() {
        Toolbar toolbar = bindView(R.id.toolbar);
        toolbar.inflateMenu(R.menu.file_menu);
        toolbar.setOnMenuItemClickListener(this);

        mAdapter = new FileListAdapter(null,R.layout.list_item_file);
        mAdapter.setErrorLayoutId(R.layout.list_item_error);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                onClick(mAdapter.getData().get(position));
            }
        });

        mAdapter.setOnItemLongClickListener(new CommonAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View v, int position) {
                onLongClick(mAdapter.getData().get(position));
                return true;
            }
        });

        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilePresenter.goBack();
            }
        });
    }



    protected void onClick(IBaseFileBean bean){
        if (bean.isFolder()){
            mFilePresenter.open(bean);
        }else{
            // TODO: 2017/4/3 0003 show dialog to select open type
        }
    }

    protected void onLongClick(IBaseFileBean bean){

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_local;
    }

    @Override
    protected IBasePresenter initPresenter() {
        mFilePresenter = new LocalFilePresenter(new LocalFileModel());
        return mFilePresenter;
    }

    @Override
    protected void LazyLoad() {
        mFilePresenter.refresh();
        Log.e(TAG, "LazyLoad: local");
    }

    @Override
    public void showFileList(List<IBaseFileBean> list) {
        mAdapter.setData(list);
    }

    @Override
    public void showTitleAndParent(String title, String parent) {
        mTvTitle.setText(title);
        mTvBack.setText(parent);
    }

    @Override
    public void showErrorView() {
        mAdapter.showErrorView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView: ");
    }

    @Override
    public void close() {
        getActivity().finish();
    }

    @Override
    public boolean onBackPressed() {
        mFilePresenter.goBack();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.menu_paste){
            mFilePresenter.paste(null);
        }else if(item.getItemId()==R.id.menu_createFolder){
            CreateFolderDialog dialog =  new CreateFolderDialog(getContext());
            dialog.setDialogListener(new CreateFolderDialog.DialogListener() {
                @Override
                public void onOK(String name) {
                    mFilePresenter.createFolder(name);
                }
            });
            dialog.show();
        }else if (item.getItemId()==R.id.menu_refresh){
            mFilePresenter.refresh();
        }
        return true;
    }
}
