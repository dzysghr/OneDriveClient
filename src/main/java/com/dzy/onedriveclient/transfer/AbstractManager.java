package com.dzy.onedriveclient.transfer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by dzysg on 2017/5/3 0003.
 */

public abstract class AbstractManager {

    private List<TaskListener> mTaskListenerList = new ArrayList<>();
    protected NotifyHandler mNotifyHandler;
    protected List<TaskHandle> mTaskList = new ArrayList<>();
    protected AbstractDispatcher mDispatcher;
    protected CoreContext mContext;

    public AbstractManager(CoreContext coreContext){
        DLHelper.checkNull(coreContext, "coreContext");
        mContext = coreContext;
        mNotifyHandler = new NotifyHandler(Looper.getMainLooper());
        mDispatcher = provideDispatcher(mContext,mNotifyHandler);
    }
    abstract AbstractDispatcher provideDispatcher(CoreContext coreContext,BaseListener listener);

    public void init(){
        mTaskList = initTaskList();
    }

    abstract List<TaskHandle> initTaskList();

    public abstract Observable<TaskHandle> createTask(String ...arg);

    public List<TaskHandle> getTaskList() {
        return mTaskList;
    }


    void notifyListChanged(){
        for (TaskListener i:mTaskListenerList){
            i.onTaskListChanged(mTaskList);
        }
    }

    public void start(TaskHandle handle){
        mDispatcher.submit(AbstractDispatcher.MSG_START,handle);
    }

    public void stop(TaskHandle handle){
        mDispatcher.submit(AbstractDispatcher.MSG_STOP,handle);
    }

    public void delete(TaskHandle handle,boolean deleteFile){
        mDispatcher.submit(AbstractDispatcher.MSG_DELETE,handle);
        mTaskList.remove(handle);
        notifyListChanged();
    }

    public void destroy() {
        mDispatcher.destroy();
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

        NotifyHandler(Looper looper) {
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
