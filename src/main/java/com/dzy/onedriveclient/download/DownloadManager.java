package com.dzy.onedriveclient.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dzy.onedriveclient.model.gen.TaskInfoDao;
import com.dzy.onedriveclient.utils.RxHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by dzysg on 2017/4/29 0029.
 */

public class DownloadManager {

    private TaskDispatcher mTaskDispatcher;
    private DownloadContext mDownloadContext;
    private Map<Long,TaskHandle> mTaskMap;
    private List<TaskHandle> mTaskList;

    private int mMaxTask;
    private List<TaskListener> mTaskListenerList;



    public DownloadManager(DownloadContext downloadContext) {
        DLHelper.checkNull(downloadContext,"downloadContext");
        mDownloadContext = downloadContext;
        mTaskListenerList = new ArrayList<>();
        mTaskDispatcher = new TaskDispatcher(mDownloadContext,new NotifyHandler(Looper.getMainLooper()));
    }

    public Observable<TaskHandle> createTask(final String url,final String localPath){
        return Observable.just(1)
                .compose(RxHelper.<Integer>computation_main())
                .map(new Function<Integer, TaskHandle>() {
            @Override
            public TaskHandle apply(@NonNull Integer integer) throws Exception {
                List<TaskInfo> taskInfoList= mDownloadContext
                        .getTaskDao()
                        .queryBuilder()
                        .whereOr(TaskInfoDao.Properties.Url.eq(url),TaskInfoDao.Properties.FilePath.eq(localPath))
                        .build()
                        .list();
                if (!taskInfoList.isEmpty()){
                    throw new IllegalArgumentException("url or path conflict");
                }else{
                    TaskInfo taskInfo = new TaskInfo(null,null,0,localPath,null,url,0);
                    TaskHandle handle = new TaskHandle(taskInfo,mTaskDispatcher);
                    mTaskDispatcher.submit(TaskDispatcher.MSG_CREATE,handle);
                    return handle;
                }
            }
        });
    }

    public Observable<List<TaskHandle>> getAllTask(){
        if (mTaskMap == null){
            mTaskMap = new ConcurrentHashMap<>();
            mTaskList = new ArrayList<>();
            return Observable.just(mDownloadContext.getTaskDao().loadAll())
                    .compose(RxHelper.<List<TaskInfo>>computation_main())
                    .map(new Function<List<TaskInfo>, List<TaskHandle>>() {
                        @Override
                        public List<TaskHandle> apply(@NonNull List<TaskInfo> taskInfos) throws Exception {
                            for (TaskInfo i:taskInfos){
                                TaskHandle  handle = new TaskHandle(i,mTaskDispatcher);
                                mTaskMap.put(i.getId(),handle);
                                mTaskList.add(handle);
                            }
                            return mTaskList;
                        }
                    });
        }else{
            return Observable.just(mTaskList);
        }
    }


    public void addTaskListener(TaskListener listener){
        mTaskListenerList.add(listener);
    }

    public void removeTaskListener(TaskListener listener){
        mTaskListenerList.remove(listener);
    }

    private class NotifyHandler extends Handler implements TaskListener{

        private static final int INIT = 0;
        private static final int UPDATE = 1;
        private static final int STATE = 2;

        public NotifyHandler(Looper looper) {
            super(looper);

        }

        private void sendMsg(int type,Object o){
            Message msg = obtainMessage(type);
            msg.obj = o;
            sendMessage(msg);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case INIT:
                    for (TaskListener i: mTaskListenerList){
                        i.onTaskInit((TaskHandle) msg.obj);
                    }
                    break;
                case UPDATE:
                    for (TaskListener i: mTaskListenerList){
                        i.onUpdate((TaskHandle) msg.obj);
                    }
                    break;
                case STATE:
                    for (TaskListener i: mTaskListenerList){
                        i.onStateChange((TaskHandle) msg.obj);
                    }
                    break;
                default:break;
            }
        }

        @Override
        public void onTaskInit(TaskHandle handle) {
            sendMsg(INIT,handle);
        }

        @Override
        public void onUpdate(TaskHandle handle) {
            sendMsg(UPDATE,handle);

        }

        @Override
        public void onStateChange(TaskHandle handle) {
            sendMsg(STATE,handle);
        }
    }

}
