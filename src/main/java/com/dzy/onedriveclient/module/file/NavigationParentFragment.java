package com.dzy.onedriveclient.module.file;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.drive.OneDriveFileModel;
import com.dzy.onedriveclient.model.local.LocalFileModel;
import com.dzy.onedriveclient.module.MainActivity;
import com.dzy.onedriveclient.utils.OpenFileHelper;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;


public class NavigationParentFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener {


    private FragmentManager mFragmentManager;
    private TextView mTvTitle;
    private TextView mTvBack;
    private Deque<IBaseFileBean> mStacks = new ArrayDeque<>();
    private int mType;
    public static final String KEY_TYPE = "type";
    public static final int TYPE_LOCAL = 0;
    public static final int TYPE_ONEDRIVE = 1;
    private FileFragment mCurrent;
    private IFilePresenter mPresenter;
    private String[] mLocalOptionItem = new String[] {"复制","删除","剪切","上传"};
    private String[] mDriveOptionItem =new String[] {"复制","删除","剪切","下载"};
    private IBaseFileBean mFrom;

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
        if (mType==TYPE_LOCAL){
            mPresenter = new FilePresenter(new LocalFileModel());
        }else{
            mPresenter = new FilePresenter(new OneDriveFileModel());
        }

        Toolbar toolbar = bindView(R.id.toolbar);
        toolbar.inflateMenu(R.menu.file_menu);
        toolbar.setOnMenuItemClickListener(this);

        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mCurrent = getInstance();
        mFragmentManager
                .beginTransaction()
                .add(R.id.container,mCurrent)
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

    private FileFragment getInstance(){
        FileFragment fileFragment= new FileFragment();
        fileFragment.setFilePresenter(mPresenter);
        return fileFragment;
    }

    public void navigateTo(IBaseFileBean bean){
        mTvTitle.setText(bean.getName());
        FileFragment fileFragment = getInstance();
        fileFragment.setCurrent(bean);

        FragmentTransaction transaction =  mFragmentManager
                .beginTransaction();
        if (mCurrent!=null){
            transaction.hide(mCurrent);
        }
        transaction.add(R.id.container,fileFragment)
                .addToBackStack(null)
                .commit();
        mCurrent = fileFragment;
        mStacks.addLast(bean);
    }

    @Override
    public boolean onBackPressed() {
        boolean hasFragment =  mFragmentManager.popBackStackImmediate();
        mCurrent = getTop();
        if (hasFragment){
            mStacks.pollLast();
            IBaseFileBean bean = mStacks.peekLast();
            if (bean==null){
                mTvTitle.setText(R.string.root);
            }else{
                mTvTitle.setText(bean.getName());
            }
            mPresenter.setCurrent(bean);
        }
        return hasFragment;
    }

    public void openItem(IBaseFileBean bean){
        if (mType != TYPE_LOCAL){
            return;
        }
        File file = (File) bean.getReal();
        OpenFileHelper.openFile(file,getActivity());
    }

    private FileFragment getTop(){
        List<Fragment> list = mFragmentManager.getFragments();
        int len = list.size();
        for (int i = len-1; i >-1; i--) {
            Fragment fragment = list.get(i);
            if (fragment instanceof FileFragment){
                return (FileFragment) fragment;
            }
        }
        return null;
    }

    private MainActivity getMainActivity(){
        return (MainActivity) getActivity();
    }


    public void showOptionMenu(final IBaseFileBean bean){
        String[] items = mType==TYPE_LOCAL?mLocalOptionItem:mDriveOptionItem;
        new AlertDialog.Builder(getContext())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                mPresenter.copy(bean);
                                break;
                            case 1:
                                mPresenter.delete(bean);
                                break;
                            case 2:
                                mPresenter.cut(bean);
                                break;
                            case 3:
                                mFrom = bean;
                                if (mType==TYPE_LOCAL){
                                    getMainActivity().selectPath(MainActivity.SELECT_UPLOAD);

                                }else{
                                    getMainActivity().selectPath(MainActivity.SELECT_DOWN);
                                }
                                break;
                         default:
                             break;
                        }
                    }
                }).show();
    }

    public void onPathSelected(IBaseFileBean to){
        if (mType==TYPE_LOCAL){
            mPresenter.upload(mFrom,to);
        }else if (mType==TYPE_ONEDRIVE){
            mPresenter.download(mFrom,to);
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.menu_paste){
            mPresenter.paste(mStacks.peekLast());
        }else if(item.getItemId()==R.id.menu_createFolder){
            CreateFolderDialog dialog =  new CreateFolderDialog(getContext());
            dialog.setDialogListener(new CreateFolderDialog.DialogListener() {
                @Override
                public void onOK(String name) {
                    mPresenter.createFolder(name);
                }
            });
            dialog.show();
        }else if (item.getItemId()==R.id.menu_refresh){
            mPresenter.refresh();
        }
        return true;
    }

    public IBaseFileBean getCurrentBean(){
        return mStacks.peekLast();
    }
}
