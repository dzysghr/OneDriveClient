package com.dzy.onedriveclient.transfer;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dzysg on 2017/4/23 0023.
 */

public abstract class AbstractDispatcher<T extends ITask> {

    public static final int MSG_CREATE = 0;
    public static final int MSG_INIT = 1;
    public static final int MSG_START = 2;
    public static final int MSG_STOP = 3;
    public static final int MSG_DELETE = 5;
    public static final int MSG_UPDATE = 7;
    public static final int MSG_STATE_CHANGED = 8;


    protected DownloadContext mContext;
    protected HashMap<TaskHandle, T> mTaskMap = new HashMap<>();
    protected HandlerThread mHandlerThread;
    protected List<T> mRunningQueue = new ArrayList<>();
    protected List<T> mWaitting = new ArrayList<>();
    protected int mMaxTask = 5;
    protected WorkerHandler mHandler;
    protected BaseListener mTaskListener;

    public AbstractDispatcher(DownloadContext context, BaseListener listener) {
        mContext = context;
        mHandlerThread = new HandlerThread("DownloadDispatcher handler");
        mHandlerThread.start();
        mHandler = new WorkerHandler(mHandlerThread.getLooper());
        mTaskListener = listener;
    }

    public void destroy() {
        mHandlerThread.quit();
    }

    public void submit(int msg, Object o) {
        Message m = mHandler.obtainMessage(msg);
        m.obj = o;
        m.sendToTarget();
    }

    public void submitDelay(int msg, Object o, int time) {
        Message m = mHandler.obtainMessage(msg);
        m.obj = o;
        mHandler.sendMessageDelayed(m, time);
    }

    protected abstract void delete(TaskHandle handle);
    protected abstract void create(TaskHandle handle);

    private void start(TaskHandle handle) {
        if (!mTaskMap.containsKey(handle)) {
            create(handle);
        }
        T task = mTaskMap.get(handle);
        if (mRunningQueue.size() >= mMaxTask) {
            mWaitting.add(task);
            handle.setState(TaskState.STATE_WAIT);
        } else {
            mRunningQueue.add(task);
            task.execute();
        }
    }

    private void update(TaskHandle handle) {
        mTaskListener.onUpdate(handle);
    }

    private void stateChanged(TaskHandle handle) {
        if (handle.getState() == TaskState.STATE_FINISH) {
            scheduleNext(handle);
        }
        mTaskListener.onStateChange(handle);
    }

    private void scheduleNext(TaskHandle handle) {
        mRunningQueue.remove(mTaskMap.get(handle));
        if (!mWaitting.isEmpty()) {
            T next = mWaitting.remove(0);
            mRunningQueue.add(next);
            next.execute();
        }
    }



    private void stop(TaskHandle handle) {
        if (mTaskMap.containsKey(handle)) {
            mTaskMap.get(handle).stop();
            scheduleNext(handle);
        }
    }

    private class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CREATE:
                    create((TaskHandle) msg.obj);
                    break;
                case MSG_START:
                    start((TaskHandle) msg.obj);
                    break;
                case MSG_DELETE:
                    delete((TaskHandle) msg.obj);
                    break;
                case MSG_STOP:
                    stop((TaskHandle) msg.obj);
                    break;
                case MSG_UPDATE:
                    update((TaskHandle) msg.obj);
                    break;
                case MSG_STATE_CHANGED:
                    stateChanged((TaskHandle) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }

}
