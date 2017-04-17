package com.dzy.onedriveclient.module.file;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.module.file.local.LocalFileFragment;
import com.dzy.onedriveclient.module.file.online.DriveFragment;

import java.util.ArrayDeque;
import java.util.Deque;


public class NavigationParentFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener {


    private FragmentManager mFragmentManager;
    private TextView mTvTitle;
    private TextView mTvBack;
    private Deque<IBaseFileBean> mStacks = new ArrayDeque<>();
    private int mType;
    public static final String KEY_TYPE = "type";
    public static final int TYPE_LOCAL = 0;
    public static final int TYPE_ONEDRIVE = 1;


    public static NavigationParentFragment newInstance(int type){
        Bundle b = new Bundle();
        b.putInt(NavigationParentFragment.KEY_TYPE,type);
        NavigationParentFragment fragment = new NavigationParentFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void initView() {
        mFragmentManager = getChildFragmentManager();
        mTvTitle = bindView(R.id.tv_title);
        mTvBack = bindView(R.id.tv_back);
        mType = getArguments().getInt(KEY_TYPE);
    }

    @Override
    protected void setupView() {
        Toolbar toolbar = bindView(R.id.toolbar);
        toolbar.inflateMenu(R.menu.file_menu);
        toolbar.setOnMenuItemClickListener(this);

        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mFragmentManager
                .beginTransaction()
                .add(R.id.container,getInstance())
                .commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_navigation;
    }

    @Override
    protected IBasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void LazyLoad() {

    }

    private LocalFileFragment getInstance(){
        if (mType==TYPE_LOCAL){
            return new LocalFileFragment();
        }else{
            return new DriveFragment();
        }
    }

    public void navigateTo(IBaseFileBean bean){
        mTvTitle.setText(bean.getName());
        LocalFileFragment fileFragment = getInstance();
        fileFragment.setCurrent(bean);
        mFragmentManager
                .beginTransaction()
                .add(R.id.container,fileFragment)
                .addToBackStack(null)
                .commit();
        mStacks.addLast(bean);

    }

    @Override
    public boolean onBackPressed() {
       boolean hasFragment =  mFragmentManager.popBackStackImmediate();
        if (hasFragment){
            if (!mStacks.isEmpty()){
                mStacks.removeLast();
            }
            IBaseFileBean bean = mStacks.pollLast();
            if (bean==null){
                mTvTitle.setText(R.string.root);
            }else{
                mTvTitle.setText(bean.getName());
            }
        }
        return hasFragment;
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.menu_paste){


        }else if(item.getItemId()==R.id.menu_createFolder){
            CreateFolderDialog dialog =  new CreateFolderDialog(getContext());
            dialog.setDialogListener(new CreateFolderDialog.DialogListener() {
                @Override
                public void onOK(String name) {

                }
            });
            dialog.show();
        }else if (item.getItemId()==R.id.menu_refresh){

        }
        return true;
    }
}
