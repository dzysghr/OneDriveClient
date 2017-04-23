package com.dzy.onedriveclient.module;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.Toast;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseActivity;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.module.file.NavigationParentFragment;
import com.dzy.onedriveclient.module.more.MoreFragment;
import com.dzy.onedriveclient.module.transfer.TransferFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private BottomNavigationView mBottomNavigationView;
    private Map<Integer,BaseFragment> mFragments = new HashMap<>();
    private BaseFragment mCurrentFragment;
    private Toast mToast;

    @Override
    protected void initView() {
        mBottomNavigationView = bindView(R.id.bottom_navigationView);
    }

    @Override
    protected void setupView() {
        mToast = Toast.makeText(this,"再次点击返回退出",Toast.LENGTH_SHORT);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                createOrShowFragment(item);
                return true;
            }
        });
        mFragments.put(R.id.menu_drive,NavigationParentFragment.newInstance(NavigationParentFragment.TYPE_ONEDRIVE));
        mFragments.put(R.id.menu_local,NavigationParentFragment.newInstance(NavigationParentFragment.TYPE_LOCAL));
        mFragments.put(R.id.menu_more, new MoreFragment());
        mFragments.put(R.id.menu_transfer,new TransferFragment());
        mCurrentFragment = mFragments.get(R.id.menu_drive);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,mCurrentFragment,R.id.menu_drive+"")
                .show(mCurrentFragment)
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
        BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(item.getItemId()+"");
        if (fragment==null){
            fragment = mFragments.get(item.getItemId());
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(mCurrentFragment)
                    .add(R.id.container,fragment,item.getItemId()+"")
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

    @Override
    public void onBackPressed() {
        if (!mCurrentFragment.onBackPressed()){
            if (mToast.getView().getParent()==null){
                mToast.show();
            }else{
                finish();
            }
        }
    }
}
