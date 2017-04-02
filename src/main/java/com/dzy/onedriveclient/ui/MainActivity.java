package com.dzy.onedriveclient.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseActivity;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.ui.main.DriveFragment;
import com.dzy.onedriveclient.ui.main.LocalFileFragment;
import com.dzy.onedriveclient.ui.main.MoreFragment;
import com.dzy.onedriveclient.ui.main.TransferFragment;

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
        mFragments.put(R.id.menu_UpOrDownload,new TransferFragment());
        mCurrentFragment = mFragments.get(R.id.menu_drive);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,mCurrentFragment)
                .commit();
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
        BaseFragment fragment = mFragments.get(item.getItemId());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,fragment)
                .commit();

    }
}
