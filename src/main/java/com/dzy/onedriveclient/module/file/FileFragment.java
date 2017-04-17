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
            // TODO: 2017/4/3 0003 show dialog to select open type
        }
    }

    private  NavigationParentFragment getParent(){
        return (NavigationParentFragment) getParentFragment();
    }

    protected void onLongClick(IBaseFileBean bean){

    }

    @Override
    public void onResume() {
        super.onResume();
        mFilePresenter.attachView(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_local;
    }

    @Override
    protected IBasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void LazyLoad() {

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