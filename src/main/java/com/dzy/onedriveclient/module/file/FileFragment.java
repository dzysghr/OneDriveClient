package com.dzy.onedriveclient.module.file;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.dzy.commemlib.ui.BaseAdapter.CommonAdapter;
import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.IBaseFileBean;

import java.util.List;


public class FileFragment extends BaseFragment implements IFileView{

    private RecyclerView mRecyclerView;
    protected FileListAdapter mAdapter;
    protected IFilePresenter mFilePresenter;
    private IBaseFileBean mCurrent;


    @Override
    protected void initView() {
        mRecyclerView = bindView(R.id.local_recycleView);
    }

    @Override
    protected void setupView() {

        mAdapter = new FileListAdapter(null,R.layout.list_item_file);
        mAdapter.setErrorLayoutId(R.layout.list_item_error);
        mAdapter.setEmptyLayoutId(R.layout.list_item_empty);
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
        mFilePresenter.open(mCurrent);
    }



    protected void onClick(IBaseFileBean bean){
        if (bean.isFolder()){
           getParent().navigateTo(bean);
        }else{
            getParent().openItem(bean);
        }
    }

    private  NavigationParentFragment getParent(){
        return (NavigationParentFragment) getParentFragment();
    }

    protected void onLongClick(IBaseFileBean bean){
        getParent().showOptionMenu(bean);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_local;
    }

    @Override
    protected IBasePresenter initPresenter() {
        return mFilePresenter;
    }

    @Override
    protected void LazyLoad() {
        mFilePresenter.attachView(this);
        Log.d(TAG, "attachView: "+mCurrent);
    }

    @Override
    public void showFileList(List<IBaseFileBean> list) {
        mAdapter.setData(list);
    }

    @Override
    public void showErrorView() {
        mAdapter.showErrorView();
    }

    @Override
    public void close() {
        getParent().onBackPressed();
    }


    public void setCurrent(IBaseFileBean current) {
        mCurrent = current;
    }

    public IFilePresenter getFilePresenter() {
        return mFilePresenter;
    }

    public void setFilePresenter(IFilePresenter filePresenter) {
        setBasePresenter(filePresenter);
        mFilePresenter = filePresenter;
        mFilePresenter.attachView(this);
    }
}
