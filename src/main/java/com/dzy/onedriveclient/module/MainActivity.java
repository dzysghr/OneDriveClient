package com.dzy.onedriveclient.module;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseActivity;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.module.file.local.LocalFileFragment;
import com.dzy.onedriveclient.module.file.online.DriveFragment;
import com.dzy.onedriveclient.module.more.MoreFragment;
import com.dzy.onedriveclient.module.transfer.TransferFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private BottomNavigationView mBottomNavigationView;
    private Map<Integer,BaseFragment> mFragments = new HashMap<>();
    private BaseFragment mCurrentFragment;

    @Override
    protected void initView() {
        mBottomNavigationView = bindView(R.id.bottom_navigationView);
    }

    @Override
    protected void setupView() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                createOrShowFragment(item);
                return true;
            }
        });
        mFragments.put(R.id.menu_drive,new DriveFragment());
        mFragments.put(R.id.menu_local, new LocalFileFragment());
        mFragments.put(R.id.menu_more, new MoreFragment());
        mFragments.put(R.id.menu_transfer,new TransferFragment());
        mCurrentFragment = mFragments.get(R.id.menu_drive);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,mCurrentFragment,DriveFragment.class.getSimpleName())
                .show(mCurrentFragment)
                .commit();
        // TODO: 2017/4/5 0005 查看生命周期
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;

    }

    @Override
    protected IBasePresenter initPresenter() {
        return null;
    }

    private void createOrShowFragment(MenuItem item){
        BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(mFragments.get(item.getItemId()).getClass().getSimpleName());
        if (fragment==null){
            fragment = mFragments.get(item.getItemId());
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(mCurrentFragment)
                    .add(R.id.container,fragment,fragment.getClass().getSimpleName())
                    .commit();

        }else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(mCurrentFragment)
                    .show(fragment)
                    .commit();

        }
        mCurrentFragment = fragment;

    }
}
