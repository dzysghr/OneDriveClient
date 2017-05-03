package com.dzy.onedriveclient.transfer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dzy.onedriveclient.model.gen.TaskInfoDao;
import com.dzy.onedriveclient.utils.RxHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by dzysg on 2017/4/29 0029.
 */

public class DownloadManager implements ITaskManager{

    private DownloadDispatcher mDownloadDispatchers;
    private DownloadContext mDownloadContext;
    private List<TaskHandle> mTaskList = new ArrayList<>();

    private List<TaskListener> mTaskListenerList;
    private NotifyHandler mNotifyHandler;


    public DownloadManager(DownloadContext downloadContext) {
        DLHelper.checkNull(downloadContext, "downloadContext");
        mDownloadContext = downloadContext;
        mTaskListenerList = new ArrayList<>();
        mDownloadDispatchers = new DownloadDispatcher(mDownloadContext, mNotifyHandler = new NotifyHandler(Looper.getMainLooper()));
    }

    public void init() {
        //mDownloadContext.getTaskDao().deleteAll();
        Observable.just(mDownloadContext.getTaskDao().loadAll())
                .map(new Function<List<TaskInfo>, List<TaskHandle>>() {
                    @Override
                    public List<TaskHandle> apply(@NonNull List<TaskInfo> taskInfos) throws Exception {
                        for (TaskInfo i : taskInfos) {
                            TaskHandle handle = new TaskHandle(i, DownloadManager.this);
                            if (i.getLength() != 0 && i.getLength() == i.getFinish()) {
                                handle.setState(TaskState.STATE_FINISH);
                            }else{
                                handle.setState(TaskState.STATE_PAUSE);
                            }
                            mTaskList.add(handle);
                        }
                        return mTaskList;
                    }
                }).blockingFirst();
    }

    public Observable<TaskHandle> createTask(final String url, final String localPath) {
        return Observable.just(1)
                .compose(RxHelper.<Integer>checkNetwork())
                .compose(RxHelper.<Integer>computation_main())
                .map(new Function<Integer, TaskHandle>() {
                    @Override
                    public TaskHandle apply(@NonNull Integer integer) throws Exception {
                        List<TaskInfo> taskInfoList = mDownloadContext
                                .getTaskDao()
                                .queryBuilder()
                                .where(TaskInfoDao.Properties.Url.eq(url), TaskInfoDao.Properties.FilePath.eq(localPath))
                                .build()
                                .list();
                        if (!taskInfoList.isEmpty()) {
                            throw new IllegalArgumentException("url or path conflict");
                        } else {
                            TaskInfo taskInfo = new TaskInfo(null, null, 0, localPath, null, url, 0);
                            TaskHandle handle = new TaskHandle(taskInfo, DownloadManager.this);
                            mDownloadDispatchers.submit(AbstractDispatcher.MSG_CREATE, handle);
                            mTaskList.add(handle);
                            notifyListChanged();
                            return handle;
                        }
                    }
                });
    }

    private void notifyListChanged(){
        for (TaskListener i:mTaskListenerList){
            i.onTaskListChanged(mTaskList);
        }
    }

    public List<TaskHandle> getAllTask() {
        return mTaskList;
    }


    @Override
    public void start(TaskHandle handle){
        mDownloadDispatchers.submit(AbstractDispatcher.MSG_START,handle);
    }

    @Override
    public void stop(TaskHandle handle){
        mDownloadDispatchers.submit(AbstractDispatcher.MSG_STOP,handle);
    }

    @Override
    public void delete(TaskHandle handle,boolean deleteFile){
        if (deleteFile&&handle.getState()==TaskState.STATE_FINISH){
            new File(handle.getPath()).delete();
        }
        mDownloadDispatchers.submit(AbstractDispatcher.MSG_DELETE,handle);
        mTaskList.remove(handle);
        notifyListChanged();
    }

    public void addTaskListener(TaskListener listener) {
        mTaskListenerList.add(listener);
    }

    public void removeTaskListener(TaskListener listener) {
        mTaskListenerList.remove(listener);
    }

    private class NotifyHandler extends Handler implements BaseListener {

        private static final int INIT = 0;
        private static final int UPDATE = 1;
        private static final int STATE = 2;

        public NotifyHandler(Looper looper) {
            super(looper);

        }

        private void sendMsg(int type, Object o) {
            Message msg = obtainMessage(type);
            msg.obj = o;
            sendMessage(msg);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT:
                    for (BaseListener i : mTaskListenerList) {
                        i.onTaskInit((TaskHandle) msg.obj);
                    }
                    break;
                case UPDATE:
                    for (BaseListener i : mTaskListenerList) {
                        i.onUpdate((TaskHandle) msg.obj);
                    }
                    break;
                case STATE:
                    for (BaseListener i : mTaskListenerList) {
                        i.onStateChange((TaskHandle) msg.obj);
                    }
                default:
                    break;
            }
        }

        @Override
        public void onTaskInit(TaskHandle handle) {
            sendMsg(INIT, handle);
            if (!mTaskList.contains(handle)) {
                mTaskList.add(handle);
            }
        }

        @Override
        public void onUpdate(TaskHandle handle) {
            sendMsg(UPDATE, handle);

        }

        @Override
        public void onStateChange(TaskHandle handle) {
            sendMsg(STATE, handle);
        }
    }

}
