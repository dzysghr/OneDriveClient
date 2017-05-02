package com.dzy.onedriveclient.download;

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

import static com.dzy.onedriveclient.download.TaskState.*;

/**
 * Created by dzysg on 2017/4/22 0022.
 */

public class DownLoadTask {



    private static final String TAG = "DownLoadTask";
    private DownloadContext mContext;
    private int mThreadCount = 1;
    private List<DownLoadThread> mThreadList = new ArrayList<>(3);
    private int mState = STATE_INIT;
    private TaskInfo mTaskInfo;
    private TaskDispatcher mDispatcher;
    private TaskHandle mTaskHandle;

    public DownLoadTask(DownloadContext context,TaskHandle handle,TaskDispatcher dispatcher) {
        DLHelper.checkNull(context,"context");
        DLHelper.checkNull(handle,"handle");
        mContext = context;
        mTaskHandle = handle;
        mTaskInfo = handle.getTaskInfo();
        mDispatcher = dispatcher;
    }

    public void execute() {
        if (mState==STATE_PAUSE){
            resume();
            return;
        }
        if (mState != STATE_INIT) {
            throw new IllegalStateException("当前状态不能调execute方法");
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
                            mDispatcher.submit(TaskDispatcher.MSG_INIT,mTaskHandle);
                        }

                        if (checkFinish(mTaskInfo)) {
                            setState(STATE_FINISH);
                            return Observable.empty();
                        }
                        if (mTaskInfo.getLength() == 0) {
                            return getContentLength();
                        } else {
                            initTaskThreads();
                            setState(STATE_READY);
                            runAllThread();
                            return Observable.empty();
                        }
                    }
                }).doOnNext(new Consumer<Long>() {
            @Override
            public void accept(@NonNull Long fileLength) throws Exception {
                mTaskInfo.setLength(fileLength);
                mContext.getTaskDao().save(mTaskInfo);
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
        if (mState==state){
            return;
        }
        mTaskHandle.setState(state);
        mDispatcher.submit(TaskDispatcher.MSG_STATE_CHANGED,mTaskHandle);
    }

    public void stop() {
        if (mState == STATE_RUNNING) {
            setState(STATE_PAUSE);
        } else {
            Log.e(TAG, "can not stop !! downloadtask is not running,current state:" + mState);
        }
    }

    private void resume() {
        if (mState == STATE_PAUSE) {
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
        long diff = now-mLastTime;
        mLastFinish +=i;
        if (diff>1000){
            mTaskHandle.setSpeed(Math.round(mLastFinish/diff*1000f));
            mDispatcher.submit(TaskDispatcher.MSG_UPDATE,mTaskHandle);
            mLastTime = now;
            mLastFinish = 0;
        }
    }

    private boolean checkFinish(TaskInfo info) {
        if (info.getLength() != 0 && info.getFinish() == info.getLength()) {
            return true;
        }
        List<ThreadInfo> threadInfos = info.getThreads();
        if (threadInfos.isEmpty()) {
            return false;
        } else {
            boolean finish = true;
            for (ThreadInfo item : threadInfos) {
                if (item.getFinished() != item.getEnd() - item.getStart()) {
                    finish = false;
                    break;
                }
            }
            return finish;
        }
    }

    public boolean isRunning(){
        boolean run=false;
        for (DownLoadThread i:mThreadList){
            if(i.mIsRunning){
                run = true;
                break;
            }
        }
        return run;
    }

    private void initTaskThreads() {
        long fileLength = mTaskInfo.getLength();
        if (fileLength <= 0) {
            throw new IllegalStateException("获取文件长度失败");
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
            mThreadList.add(new DownLoadThread(threadinfo));
            //向数据库插入线程信息
            mContext.getThreadDao().save(threadinfo);
        }
    }

    private void runAllThread() {
        if (mState == STATE_READY || mState == STATE_PAUSE) {
            setState(STATE_RUNNING);
            for (DownLoadThread item : mThreadList) {
                mContext.getExecutor().execute(item);
            }
        } else {
            Log.e("DownLoadTask", "runAllThread: state error ,state " + STATE_READY);
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

        if (finished&&mTaskInfo.getFinish() != mTaskInfo.getLength()) {
            Log.e(TAG, "任务下载错误,length " + mTaskInfo.getLength() + ",finish " + mTaskInfo.getFinish());
            throw new IllegalStateException("任务完成进度与线程进度不一致");
        }
        if (finished) {
            setState(STATE_FINISH);
            Log.d(TAG, "checkAllThreadFinished: file finish " + mTaskInfo.getFilePath());
        }else{
            mContext.getTaskDao().update(mTaskInfo);
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
            Request request = new Request.Builder()
                    .url(mTaskInfo.getUrl())
                    .addHeader("Range", "bytes=" + mThreadInfo.getStart() + "-" + mThreadInfo.getEnd()).build();
            RandomAccessFile raf = null;
            InputStream is = null;
            try {
                Response response = mContext.getOkHttpClient().newCall(request).execute();
                if (response.code() != 206) {
                    setState(STATE_ERROR);
                    return;
                }
                long start = mThreadInfo.getStart() + mThreadInfo.getFinished();
                //设置文件写入位置
                File file = new File(mTaskInfo.getFilePath());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);

                //读取数据
                is = response.body().byteStream();
                byte[] buf = new byte[1024 * 4];
                int len = -1;
                while ((len = is.read(buf)) != -1&&mIsRunning) {
                    //写入文件
                    raf.write(buf, 0, len);
                    //累加整个文件完成进度
                    updateFinish(len);
                    //累加线程完成进度
                    mThreadInfo.setFinished(mThreadInfo.getFinished() + len);

                    //下载暂停时,保存下载进度
                    if (mState == STATE_PAUSE) {
                        mContext.getThreadDao().update(mThreadInfo);
                        Log.i("download", "stop downloading ,save the thread info");
                        break;
                    }
                    if (mState == STATE_ERROR) {
                        break;
                    }
                }
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
