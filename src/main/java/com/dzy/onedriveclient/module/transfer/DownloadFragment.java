package com.dzy.onedriveclient.module.transfer;

import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.dzy.commemlib.ui.BaseAdapter.CommonAdapter;
import com.dzy.commemlib.ui.BaseAdapter.ContentHolder;
import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.core.BaseFragment;
import com.dzy.onedriveclient.core.mvp.IBasePresenter;
import com.dzy.onedriveclient.download.DownloadManager;
import com.dzy.onedriveclient.download.TaskHandle;
import com.dzy.onedriveclient.download.TaskListener;
import com.dzy.onedriveclient.download.TaskState;
import com.dzy.onedriveclient.service.DownloadService;
import com.dzy.onedriveclient.utils.OpenFileHelper;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Created by dzysg on 2017/5/2 0002.
 */

public class DownloadFragment extends BaseFragment implements TaskListener, CommonAdapter.OnItemClickListener, CommonAdapter.OnItemLongClickListener {


    private RecyclerView mRecyclerView;
    private DownloadManager mDownloadManager;
    private TaskListAdapter mAdapter;

    @Override
    protected void initView() {
        mRecyclerView = bindView(R.id.recycleView);
    }

    @Override
    protected void setupView() {
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new TaskListAdapter(null,R.layout.list_item_task);
        mRecyclerView.setAdapter(mAdapter);
        mDownloadManager = DownloadService.getDownloadManaer();
        mDownloadManager.addTaskListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_download;
    }

    @Override
    protected IBasePresenter initPresenter() {
        return null;
    }


    @Override
    protected void LazyLoad() {
        Log.e(TAG, "LazyLoad download page: ");
        if (mDownloadManager!=null){
            mAdapter.setData(mDownloadManager.getAllTask());
        }
    }

    @Override
    public void onTaskListChanged(List<TaskHandle> list) {
        mAdapter.setData(list);
    }

    @Override
    public void onTaskInit(TaskHandle handle) {

    }

    @Override
    public void onUpdate(TaskHandle handle) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStateChange(TaskHandle handle) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadManager.removeTaskListener(this);
    }

    @Override
    public void onItemClick(View v, int position) {
        TaskHandle handle = mAdapter.getData().get(position);
        switch (handle.getState()){
            case TaskState.STATE_RUNNING:
                handle.stop();
                break;
            case TaskState.STATE_PAUSE:
                handle.start();
                break;
            case TaskState.STATE_FINISH:
                boolean re = OpenFileHelper.openFile(new File(handle.getPath()),getContext());
                if (!re){
                    Toast("打开失败,文件格式不支持或文件不存在");
                }
                break;
            default:
                break;
        }

    }


    private String[] mItemOptions = {"删除记录","删除记录和文件"};
    @Override
    public boolean onItemLongClick(View v, final int position) {

        new  AlertDialog.Builder(getContext()).setItems(mItemOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    mAdapter.getData().get(position).delete(false);
                }else{
                    mAdapter.getData().get(position).delete(true);
                }
            }
        }).create().show();
        return true;
    }

    private class TaskListAdapter extends CommonAdapter<TaskHandle> {
        private  String mPatten = "%.1fkb/%.1fkb   %.1fkb/s";

        public TaskListAdapter(List<TaskHandle> datas, @LayoutRes int id) {
            super(datas, id);
        }

        @Override
        public void bindView(ContentHolder holder, int position, TaskHandle item) {

            float length = item.getLength()/1024f;
            float finish = item.getFinish()/1024f;
            float speed = item.getSpeed()/1024f;

            holder.setOnItemClickListener(DownloadFragment.this);
            holder.setOnItemLongClickListener(DownloadFragment.this);
            holder.setText(R.id.tv_fileName,item.getFileName());
            holder.setText(R.id.tv_task_progress,String.format(Locale.CHINA,mPatten,finish,length,speed));
            switch (item.getState()){
                case TaskState.STATE_PAUSE:
                    holder.setText(R.id.tv_state,"已暂停");
                    break;
                case TaskState.STATE_RUNNING:
                    holder.setText(R.id.tv_state,"下载中");
                    break;
                case TaskState.STATE_FINISH:
                   holder.setText(R.id.tv_state,"已完成");
                    break;
                case TaskState.STATE_INIT:
                case TaskState.STATE_READY:
                    holder.setText(R.id.tv_state,"准备中");
                    break;
                case TaskState.STATE_ERROR:
                    holder.setText(R.id.tv_state,"任务错误");
                    break;
                case TaskState.STATE_WAIT:
                    holder.setText(R.id.tv_state,"等待队列");
                    break;
                default:
                    break;
            }

        }
    }
}
