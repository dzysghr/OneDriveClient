package com.dzy.onedriveclient.module.file;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.dzy.commemlib.ui.BaseAdapter.CommonAdapter;
import com.dzy.commemlib.ui.ptr.PullToRefreshLayout;
import com.dzy.commemlib.ui.ptr.RefreshLinstener;
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
    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    protected void initView() {
        mRecyclerView = bindView(R.id.local_recycleView);
        mPullToRefreshLayout = bindView(R.id.ptr);
    }

    @Override
    protected void setupView() {

        mPullToRefreshLayout.setHeader(new BilibiliHeader(getContext()));
        mPullToRefreshLayout.setRefreshLinstener(new RefreshLinstener() {
            @Override
            public void onRefreshStart() {
                mFilePresenter.refresh();
            }
        });

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
        mFilePresenter.getChildren(mCurrent);
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
        mPullToRefreshLayout.succeedRefresh();
    }

    @Override
    public void showErrorView() {
        mAdapter.showErrorView();
        mPullToRefreshLayout.failRefresh();
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
