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
import com.dzy.onedriveclient.service.DownOrUploadService;
import com.dzy.onedriveclient.transfer.TaskHandle;
import com.dzy.onedriveclient.transfer.TaskListener;
import com.dzy.onedriveclient.transfer.TaskState;
import com.dzy.onedriveclient.transfer.UploadManager;

import java.util.List;
import java.util.Locale;

/**
 * Created by dzysg on 2017/5/2 0002.
 */

public class UploadFragment extends BaseFragment implements CommonAdapter.OnItemClickListener, CommonAdapter.OnItemLongClickListener, TaskListener {


    private RecyclerView mRecyclerView;
    private UploadManager mUploadManager;
    private TaskListAdapter mAdapter;

    @Override
    protected void initView() {
        mRecyclerView = bindView(R.id.recycleView);
    }

    @Override
    protected void setupView() {
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new TaskListAdapter(null, R.layout.list_item_task);
        mRecyclerView.setAdapter(mAdapter);
        mUploadManager = DownOrUploadService.getUploadManager();
        mUploadManager.addTaskListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_upload;
    }

    @Override
    protected IBasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void LazyLoad() {
        Log.e(TAG, "LazyLoad download page: ");
        if (mUploadManager != null) {
            mAdapter.setData(mUploadManager.getTaskList());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUploadManager.removeTaskListener(this);
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
        if (handle.getState()==TaskState.STATE_FINISH){
            Toast("文件"+handle.getFileName()+"上传完成");
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        TaskHandle handle = mAdapter.getData().get(position);
        switch (handle.getState()) {
            case TaskState.STATE_RUNNING:
                handle.stop();
                break;
            case TaskState.STATE_PAUSE:
            case TaskState.STATE_ERROR:
                handle.start();
                break;
            default:
                break;
        }

    }

    private String[] mItemOptions = {"删除任务"};

    @Override
    public boolean onItemLongClick(View v, final int position) {

        new AlertDialog.Builder(getContext()).setItems(mItemOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAdapter.getData().get(position).delete(false);
            }
        }).create().show();
        return true;
    }

    private class TaskListAdapter extends CommonAdapter<TaskHandle> {
        private String mPatten = "%.1fkb/%.1fkb   %.1fkb/s";

        public TaskListAdapter(List<TaskHandle> datas, @LayoutRes int id) {
            super(datas, id);
        }

        @Override
        public void bindView(ContentHolder holder, int position, TaskHandle item) {

            float length = item.getLength() / 1024f;
            float finish = item.getFinish() / 1024f;
            float speed = item.getSpeed() / 1024f;

            holder.setOnItemClickListener(UploadFragment.this);
            holder.setOnItemLongClickListener(UploadFragment.this);
            holder.setText(R.id.tv_fileName, item.getFileName());
            holder.setText(R.id.tv_task_progress, String.format(Locale.CHINA, mPatten, finish, length, speed));
            switch (item.getState()) {
                case TaskState.STATE_PAUSE:
                    holder.setText(R.id.tv_state, "已暂停");
                    break;
                case TaskState.STATE_RUNNING:
                    holder.setText(R.id.tv_state, "上传中");
                    break;
                case TaskState.STATE_FINISH:
                    holder.setText(R.id.tv_state, "已完成");
                    break;
                case TaskState.STATE_INIT:
                case TaskState.STATE_READY:
                    holder.setText(R.id.tv_state, "准备中");
                    break;
                case TaskState.STATE_ERROR:
                    holder.setText(R.id.tv_state, "任务错误");
                    break;
                case TaskState.STATE_WAIT:
                    holder.setText(R.id.tv_state, "等待队列");
                    break;
                default:
                    break;
            }

        }
    }
}
