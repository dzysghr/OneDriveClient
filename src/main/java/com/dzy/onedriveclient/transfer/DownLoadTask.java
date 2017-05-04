package com.dzy.onedriveclient.transfer;

import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

import static com.dzy.onedriveclient.transfer.TaskState.*;

/**
 * Created by dzysg on 2017/4/22 0022.
 */

public class DownLoadTask implements ITask {

    private static final String TAG = "DownLoadTask";
    private CoreContext mContext;
    private int mThreadCount = 1;
    private List<DownLoadThread> mThreadList = new ArrayList<>(3);
    private int mState = STATE_INIT;
    private TaskInfo mTaskInfo;
    private DownloadDispatcher mDispatcher;
    private TaskHandle mTaskHandle;

    public DownLoadTask(CoreContext context, TaskHandle handle, DownloadDispatcher dispatcher) {
        DLHelper.checkNull(context, "context");
        DLHelper.checkNull(handle, "handle");
        mContext = context;
        mTaskHandle = handle;
        mTaskInfo = handle.getTaskInfo();
        mDispatcher = dispatcher;
    }

    @Override
    public void execute() {
        if (mState == STATE_PAUSE) {
            resume();
            return;
        }
        if (mState != STATE_INIT) {
            throw new IllegalStateException("can not execute the task in current state："+mState);
        }
        start();
    }

    private void start() {
        Observable.just(mTaskInfo)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Function<TaskInfo, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(@NonNull TaskInfo taskInfo) throws Exception {
                        if (mTaskInfo.getId() == null) {
                            mContext.getTaskDao().save(mTaskInfo);
                            mDispatcher.submit(DownloadDispatcher.MSG_INIT, mTaskHandle);
                        }
                        if (checkFinish(mTaskInfo)) {
                            setState(STATE_FINISH);
                            return Observable.empty();
                        }
                        if (mTaskInfo.getLength() == 0) {
                            return getContentLength();
                        } else {
                            return Observable.just(0L);
                        }
                    }
                })
                .doOnNext(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long fileLength) throws Exception {
                if (mTaskInfo.getLength()==0){
                    mTaskInfo.setLength(fileLength);
                    mContext.getTaskDao().save(mTaskInfo);
                }
                initTaskThreads();
                setState(STATE_READY);
                runAllThread();
            }
        })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        setState(STATE_ERROR);
                        Log.e("DownLoadTask", "DownLoadTask: ", throwable);
                    }
                });
    }

    private void setState(int state) {
        if (mState == state) {
            return;
        }
        if (state == TaskState.STATE_FINISH || state == TaskState.STATE_PAUSE) {
            mContext.getTaskDao().update(mTaskInfo);
        }
        mState = state;
        mTaskHandle.setState(state);
        mDispatcher.submit(DownloadDispatcher.MSG_STATE_CHANGED, mTaskHandle);
    }

    @Override
    public void stop() {
        if (mState == STATE_RUNNING) {
            setState(STATE_PAUSE);
        } else {
            Log.e(TAG, "can not stop !! downloadtask is not running,current state:" + mState);
        }
    }

    private void resume() {
        if (mState == STATE_PAUSE) {
            long finish = 0;
            if (mTaskInfo.getThreads().isEmpty()) {
                mTaskInfo.resetThreads();
            }
            for (ThreadInfo i : mTaskInfo.getThreads()) {
                finish = i.getFinished();
            }
            mTaskInfo.setFinish(finish);
            runAllThread();
        } else {
            Log.e(TAG, "can not resume !! downloadtask is not pausing,current state:" + mState);
        }
    }

    private long mLastTime = 0;
    private long mLastFinish = 0;

    private synchronized void updateFinish(long i) {
        mTaskInfo.setFinish(mTaskInfo.getFinish() + i);
        long now = System.currentTimeMillis();
        long diff = now - mLastTime;
        mLastFinish += i;
        if (diff > 1000) {
            mContext.getTaskDao().update(mTaskInfo);
            mTaskHandle.setSpeed(Math.round(mLastFinish / diff * 1000f));
            mDispatcher.submit(DownloadDispatcher.MSG_UPDATE, mTaskHandle);
            mLastTime = now;
            mLastFinish = 0;
        }
    }

    private boolean checkFinish(TaskInfo info) {
        return info.getLength() != 0 && info.getFinish() == info.getLength();
    }

    @Override
    public boolean isRunning() {
        boolean run = false;
        for (DownLoadThread i : mThreadList) {
            if (i.mIsRunning) {
                run = true;
                break;
            }
        }
        return run;
    }

    private void initTaskThreads() {
        long fileLength = mTaskInfo.getLength();
        if (fileLength <= 0) {
            throw new IllegalStateException("can not get File length correctly,length :"+fileLength);
        }
        long finish = 0;
        if (!mTaskInfo.getThreads().isEmpty()) {
            for (ThreadInfo item : mTaskInfo.getThreads()) {
                mThreadList.add(new DownLoadThread(item));
                finish += item.getFinished();
            }
            mTaskInfo.setFinish(finish);
            return;
        }
        if (fileLength < 1024 * 1024) {
            mThreadCount = 1;
        }
        long length = fileLength / mThreadCount;
        for (int i = 0; i < mThreadCount; i++) {
            ThreadInfo threadinfo = new ThreadInfo(null, mTaskInfo.getFileId(), length * i, ((i + 1) * length - 1), 0, mTaskInfo.getId());
            //最后一次直接到最后长度
            if (i == mThreadCount - 1) {
                threadinfo.setEnd(fileLength);
            }
            //向数据库插入线程信息
            mContext.getThreadDao().save(threadinfo);
        }
        mTaskInfo.resetThreads();
        initTaskThreads();
    }

    private void runAllThread() {
        if (mState == STATE_READY || mState == STATE_PAUSE) {
            setState(STATE_RUNNING);
            for (DownLoadThread item : mThreadList) {
                mContext.getExecutor().execute(item);
            }
        } else {
            Log.e("DownLoadTask", "runAllThread: state error ,state " + mState);
        }
    }

    private Observable<Long> getContentLength() {
        return Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> e) throws Exception {
                final Request request = new Request.Builder().url(mTaskInfo.getUrl()).build();
                e.onNext(mContext.getOkHttpClient().newCall(request).execute());
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .map(new Function<Response, Long>() {
                    @Override
                    public Long apply(@NonNull Response response) throws Exception {
                        if (response.code() != 200) {
                            throw new RuntimeException(response.body().string());
                        }
                        String lenstr = response.header("Content-Length", "-1");
                        return Long.parseLong(lenstr);
                    }
                });
    }

    private synchronized void checkAllThreadFinished() {
        boolean finished = true;
        for (DownLoadThread thread : mThreadList) {
            if (!thread.mIsFinished) {
                finished = false;
                break;
            }
        }

        if (finished && mTaskInfo.getFinish() != mTaskInfo.getLength()) {
            Log.e(TAG, "任务下载错误,length " + mTaskInfo.getLength() + ",finish " + mTaskInfo.getFinish());
            throw new IllegalStateException("任务完成进度与线程进度不一致");
        }
        if (finished) {
            setState(STATE_FINISH);
            Log.d(TAG, "checkAllThreadFinished: file finish " + mTaskInfo.getFilePath());
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DownLoadTask)) {
            return false;
        }
        DownLoadTask task = (DownLoadTask) obj;
        return mTaskInfo.equals(task.mTaskInfo);
    }

    @Override
    public int hashCode() {
        return mTaskInfo.hashCode();
    }

    private class DownLoadThread implements Runnable {

        ThreadInfo mThreadInfo;
        boolean mIsFinished;
        boolean mIsRunning;

        public DownLoadThread(ThreadInfo info) {
            mThreadInfo = info;
        }

        @Override
        public void run() {
            mIsRunning = true;
            long start = mThreadInfo.getStart() + mThreadInfo.getFinished();
            if (start==mThreadInfo.getEnd()){
                mIsRunning = false;
                mIsFinished = true;
                return;
            }

            Request request = new Request.Builder()
                    .url(mTaskInfo.getUrl())
                    .addHeader("Range", "bytes=" + start + "-" + mThreadInfo.getEnd()).build();
            RandomAccessFile raf = null;
            InputStream is = null;
            try {
                Response response = mContext.getOkHttpClient().newCall(request).execute();
                if (response.code() != 206) {
                    setState(STATE_ERROR);
                    Log.e(TAG, "run: "+response.body().string());
                    return;
                }

                //设置文件写入位置
                File file = new File(mTaskInfo.getFilePath());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                //读取数据
                is = response.body().byteStream();
                byte[] buf = new byte[1024 * 4];
                int len = -1;
                long time = System.currentTimeMillis();
                while ((len = is.read(buf)) != -1 && mIsRunning) {
                    //写入文件
                    raf.write(buf, 0, len);
                    //累加整个文件完成进度
                    updateFinish(len);
                    //累加线程完成进度
                    mThreadInfo.setFinished(mThreadInfo.getFinished() + len);


                    if (System.currentTimeMillis() - time > 1000) {
                        time = System.currentTimeMillis();
                        mContext.getThreadDao().update(mThreadInfo);
                    }

                    //下载暂停时,保存下载进度
                    if (mState == STATE_PAUSE||mState == STATE_ERROR) {
                        break;
                    }
                }
                mContext.getThreadDao().update(mThreadInfo);
                if (mThreadInfo.getFinished() == mThreadInfo.getEnd() - mThreadInfo.getStart()) {
                    mIsFinished = true;
                }
                //线程执行完毕
                Log.i("download", "one thread download finish ,file:" + mTaskInfo.getFilePath());
                //检查下载任务是否完成
                checkAllThreadFinished();

            } catch (IOException e) {
                setState(STATE_ERROR);
                e.printStackTrace();
            } finally {
                saveClose(raf);
                saveClose(is);
                mIsRunning = false;
            }
        }

        private void saveClose(Closeable closeable) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
