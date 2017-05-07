package com.dzy.onedriveclient.module;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseActivity;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.module.file.NavigationParentFragment;
import com.dzy.onedriveclient.module.more.MoreFragment;
import com.dzy.onedriveclient.module.transfer.TransferFragment;
import com.dzy.onedriveclient.service.DownOrUploadService;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private BottomNavigationView mBottomNavigationView;
    private Map<Integer,BaseFragment> mFragments = new HashMap<>();
    private BaseFragment mCurrentFragment;
    private Toast mToast;
    private View mBottomMenuLayout;
    private View mBottomOk;
    private View mBottomCancel;
    private int mState = 0;
    public static final int SELECT_UPLOAD=2;
    public static final int SELECT_DOWN=1;
    public static final int SELECT_NORMAL=0;



    @Override
    protected void initView() {
        mBottomNavigationView = bindView(R.id.bottom_navigationView);
        mBottomMenuLayout = bindView(R.id.bottom_select_menu);
        mBottomOk = bindView(R.id.tv_ok);
        mBottomCancel = bindView(R.id.tv_cancel);

    }

    @Override
    protected void setupView() {
        startService(DownOrUploadService.class);
        mBottomOk.setOnClickListener(this);
        mBottomCancel.setOnClickListener(this);
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
        navigateTo(item.getItemId());
    }

    private void navigateTo(int id){
        BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(id+"");
        if (fragment==null){
            fragment = mFragments.get(id);
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(mCurrentFragment)
                    .add(R.id.container,fragment,id+"")
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

    public void selectPath(int state){
        mBottomNavigationView.setVisibility(View.INVISIBLE);
        mBottomMenuLayout.setVisibility(View.VISIBLE);
        if (state==SELECT_UPLOAD){
            navigateTo(R.id.menu_drive);
        }else{
            navigateTo(R.id.menu_local);
        }
        mState = state;
    }

    private void returnToNormal(){
        mBottomNavigationView.setVisibility(View.VISIBLE);
        mBottomMenuLayout.setVisibility(View.INVISIBLE);
        mState= SELECT_NORMAL;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.tv_ok){
            IBaseFileBean bean = ((NavigationParentFragment)mCurrentFragment).getCurrentBean();
            NavigationParentFragment fragment=null;

            if (mState==SELECT_UPLOAD){
                fragment= (NavigationParentFragment) mFragments.get(R.id.menu_local);
                fragment.onPathSelected(bean);
                navigateTo(R.id.menu_local);
            }else if (mState==SELECT_DOWN){
                fragment = (NavigationParentFragment) mFragments.get(R.id.menu_drive);
                fragment.onPathSelected(bean);
                navigateTo(R.id.menu_drive);
            }
        }
        returnToNormal();
    }


}
