package com.dzy.onedriveclient.transfer;

import android.util.Log;

import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.model.HTTPException;
import com.dzy.onedriveclient.model.gen.UploadSessionDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import okio.Source;

import static com.dzy.onedriveclient.transfer.TaskState.STATE_FINISH;
import static com.dzy.onedriveclient.transfer.TaskState.STATE_INIT;
import static com.dzy.onedriveclient.transfer.TaskState.STATE_PAUSE;
import static com.dzy.onedriveclient.transfer.TaskState.STATE_READY;
import static com.dzy.onedriveclient.transfer.TaskState.STATE_RUNNING;

/**
 * Created by dzysg on 2017/5/3 0003.
 */

public class UploadTask implements ITask {

    private static final String TAG = "UploadTask";
    private CoreContext mContext;
    private int mState = STATE_INIT;
    private TaskInfo mTaskInfo;
    private AbstractDispatcher mDispatcher;
    private TaskHandle mTaskHandle;
    private UploadSession mSession;
    private UploadThread mUploadThread;
    private boolean mNeedUpdateSession = true;

    public UploadTask(CoreContext context, TaskHandle handle, AbstractDispatcher dispatcher) {
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
        start();
    }

    private void start() {
        Observable.just(mTaskInfo)
                .subscribeOn(Schedulers.newThread())
                .doOnNext(new Consumer<TaskInfo>() {
                    @Override
                    public void accept(@NonNull TaskInfo info) throws Exception {
                        DLHelper.checkExistFile(mTaskInfo.getFilePath());
                        if (info.getId() == null) {
                            mContext.getTaskDao().save(info);
                        }
                        if (mSession == null) {
                            mSession = getUploadSessionFromDB(info);
                            if (mSession == null || checkSessionExpire(mSession)) {
                                mSession = createUploadSession(info);
                                mTaskInfo.setFinish(0);
                                mSession.setTaskInfoId(info.getId());
                                mContext.getTaskDao().save(info);
                                mContext.getUploadSessionDao().save(mSession);
                            }
                        }
                        assert mSession != null;
                        setState(TaskState.STATE_READY);
                        startThread();
                    }
                }).subscribe(new Consumer<TaskInfo>() {
            @Override
            public void accept(@NonNull TaskInfo info) throws Exception {
                Log.e(TAG, "uploadTask start ");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                setState(TaskState.STATE_ERROR);
                Log.e(TAG, "uploadTask error ", throwable);
            }
        });

    }

    private void setState(int state) {
        if (mState == state) {
            return;
        }
        mState = state;
        mTaskHandle.setState(state);
        mDispatcher.submit(AbstractDispatcher.MSG_STATE_CHANGED, mTaskHandle);
    }

    private void startThread() {
        if (mState == STATE_PAUSE || mState == STATE_READY || mState == STATE_RUNNING) {
            mUploadThread = new UploadThread();
            mContext.getExecutor().execute(mUploadThread);
            setState(STATE_RUNNING);
        } else {
            Log.e(TAG, "startThread: you can not startThread in state:" + mState);
        }
    }

    private boolean checkSessionExpire(UploadSession s) throws ParseException {
        UploadSession session = s;
        //判断是否过期
        if (DLHelper.isExpire(DLHelper.parseDate(session.getExpirationDateTime()))) {
            mContext.getUploadSessionDao().delete(session);
            return true;
        }
        return false;
    }

    private UploadSession getUploadSessionFromDB(TaskInfo info) throws ParseException {
        //从数据库查找Session
        List<UploadSession> list = mContext.getUploadSessionDao()
                .queryBuilder()
                .where(UploadSessionDao.Properties.TaskInfoId.eq(info.getId()))
                .list();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }


    private UploadSession updateUploadSession(UploadSession session) throws IOException {
        Request request = new Request.Builder()
                .url(session.getUploadUrl())
                .build();
        Response response = mContext.getOkHttpClient().newCall(request).execute();
        if (response.code() != 200) {
            Log.e(TAG, "updateUploadSession error:" + response.body().string());
            throw new HTTPException(response.code());
        }
        String json = response.body().string();
        return DLHelper.parseUploadSession(json, session);
    }

    private UploadSession createUploadSession(TaskInfo info) throws Exception {
        File file = new File(info.getFilePath());
        String json = "{\n" +
                "  \"item\": {\n" +
                "    \"@microsoft.graph.conflictBehavior\": \"rename\",\n" +
                "    \"name\": \"" + file.getName() + "\"" +
                "  }\n" +
                "}";
        String url = null;
        if (info.getFileId() == null) {
            url = Constants.BASE_URL + "drive/root:/" + file.getName() + ":/createUploadSession";
        } else {
            url = Constants.BASE_URL + "drive/items/" + info.getFileId() + ":/" + file.getName() + ":/createUploadSession";
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "");
        //创建一个请求对象
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody) //okhttp3.internal.Util.EMPTY_REQUEST
                .build();
        Response response = mContext.getOkHttpClient().newCall(request).execute();
        if (response.code() != 200) {
            Log.e(TAG, response.body().string());
            throw new HTTPException(response.code());
        }
        String body = response.body().string();
        mNeedUpdateSession = false;
        return DLHelper.parseUploadSession(body, null);
    }


    private void resume() {
        startThread();
    }

    @Override
    public void stop() {
        if (mState == TaskState.STATE_RUNNING) {
            setState(TaskState.STATE_PAUSE);
            if (mUploadThread.isRunning) {
                mUploadThread.interrupt();
            }
        } else {
            throw new IllegalStateException("can not stop the task,state " + mState);
        }

    }

    private void onFragmentSucceed(UploadSession session) {
        if (session.getNextExpectedRange() != null) {
            mSession.setNextExpectedRange(session.getNextExpectedRange());
            mNeedUpdateSession = false;
        }
    }

    private void onUploadSucceed() {
        mTaskInfo.setFinish(mTaskInfo.getLength());
        setState(STATE_FINISH);
    }

    private void onThreadEnd() {
        mContext.getTaskDao().update(mTaskInfo);
        if (mUploadThread.mHaveError) {
            setState(TaskState.STATE_ERROR);
        }
        if (mTaskInfo.getLength() == mTaskInfo.getFinish()) {
            onUploadSucceed();
        }
    }

    private long mLastTime;
    private long mLastFinish;

    private void updateProgress(long len) {
        mTaskInfo.setFinish(mTaskInfo.getFinish() + len);
        long now = System.currentTimeMillis();
        long diff = now - mLastTime;
        mLastFinish += len;
        if (diff > 1000) {
            mTaskHandle.setSpeed(Math.round(mLastFinish / diff * 1000f));
            mDispatcher.submit(AbstractDispatcher.MSG_UPDATE, mTaskHandle);
            mLastTime = now;
            mLastFinish = 0;
        }
    }

    @Override
    public boolean isRunning() {
        return mUploadThread != null && mUploadThread.isRunning;
    }

    @Override
    public void cancel() {
        if (mUploadThread != null && mUploadThread.isRunning) {
            mUploadThread.interrupt();
        }
        if (mSession == null) {
            mContext.getTaskDao().delete(mTaskInfo);
            try {
                mSession = getUploadSessionFromDB(mTaskInfo);
                if (mSession != null) {
                    mContext.getUploadSessionDao().delete(mSession);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (mSession == null) {
            return;
        }
        Observable.just(mSession).subscribeOn(Schedulers.newThread())
                .doOnNext(new Consumer<UploadSession>() {
                    @Override
                    public void accept(@NonNull UploadSession session) throws Exception {
                        if (session.getUploadUrl() != null) {
                            Request request = new Request.Builder()
                                    .url(mSession.getUploadUrl())
                                    .delete()
                                    .build();
                            mContext.getOkHttpClient().newCall(request).execute();
                        }
                    }
                }).subscribe();
    }

    private class FileStreamRequestBody extends RequestBody {

        private long mStart;
        private long mEnd;
        private File mFile;
        private BufferedSink bufferedSink;
        private FileInputStream mStream;
        private Source mSource;
        private static final int sDefaultSegment = 2048;

        public FileStreamRequestBody(File file, long start, long end) {
            mStart = start;
            mEnd = end;
            mFile = file;
        }

        @Override
        public long contentLength() throws IOException {
            return mEnd - mStart + 1;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse("application/octet-stream");
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            if (bufferedSink == null) {
                //包装
                bufferedSink = Okio.buffer(sink(sink));
            }
            if (mSource == null) {
                mStream = new FileInputStream(mFile);
                mStream.skip(mStart);
                mSource = Okio.source(mStream);
            }
            Log.e(TAG, "write start: ");
            //写入
            //bufferedSink.write(mSource,contentLength());
            long read;
            long total = 0;
            long segment = sDefaultSegment;
            long len = contentLength();
            while ((read = mSource.read(sink.buffer(), segment)) != -1) {
                sink.flush();
                total += read;
                //Log.e(TAG, "write: update" + read);
                updateProgress(read);
                if (segment != sDefaultSegment) {
                    break;
                }
                if (len - total < segment) {
                    segment = len - total;
                }
            }
            Log.e(TAG, "write end: ");
            //必须调用flush，否则最后一部分数据可能不会被写入
            bufferedSink.flush();
            mSource.close();
            bufferedSink.close();
        }

        private Sink sink(Sink sink) {
            return sink;
//            return new ForwardingSink(sink) {
//                @Override
//                public void write(Buffer source, long byteCount) throws IOException {
//                    super.write(source, byteCount);
//                    Log.e(TAG, "write: update"+byteCount);
//                    //增加当前写入的字节数
//                    updateProgress(byteCount);
//                }
//            };
        }
    }

    private class UploadThread implements Runnable {

        private File mFile;
        private static final long mMaxSize = 2 * 1024 * 1024;
        private long mStart;
        private long mEnd;
        private Call mCall;
        private boolean isRunning;
        private boolean mCancel = false;
        private boolean mHaveError = false;

        public UploadThread() {
            mFile = new File(mTaskInfo.getFilePath());
        }

        public void interrupt() {
            if (isRunning && mCall != null) {
                mCall.cancel();
            }
            mCancel = true;
        }

        private void parseStartEnd(String nextrange) {
            String[] array = nextrange.split("-");
            mStart = Long.parseLong(array[0]);
            if (array.length == 2) {
                mEnd = Long.parseLong(array[1]);
            } else if (array.length == 1) {
                mEnd = Math.min(mStart + mMaxSize, mFile.length() - 1);
            } else {
                throw new RuntimeException("parse nextRange error ,str:" + nextrange);
            }
        }

        @Override
        public void run() {
            isRunning = true;
            try {
                while (!mCancel) {
                    if (mNeedUpdateSession) {
                        updateUploadSession(mSession);
                        Log.e(TAG, "Session update succeed");
                    }
                    mNeedUpdateSession = true;
                    String nextRange = mSession.getNextExpectedRange().remove(0);
                    parseStartEnd(nextRange);
                    mTaskInfo.setFinish(mStart);
                    String range = "bytes " + mStart + "-" + mEnd + "/" + mFile.length();
                    Request request = new Request.Builder()
                            .url(mSession.getUploadUrl())
                            .header("Content-Range", range)
                            .put(new FileStreamRequestBody(mFile, mStart, mEnd))
                            .build();
                    mCall = mContext.getOkHttpClient().newCall(request);
                    Log.e(TAG, "开始上传分片: " + mStart + "-" + mEnd);
                    Response response = mCall.execute();
                    Log.e(TAG, "结束上传分片: " + mStart + "-" + mEnd);
                    int code = response.code();
                    if (code == 202) {
                        UploadSession session = DLHelper.parseUploadSession(response.body().string(), null);
                        onFragmentSucceed(session);
                    } else if (code == 201) {
                        onUploadSucceed();
                        break;
                    } else {
                        Log.e(TAG, "run in uploadThread : " + response.body().string());
                        throw new HTTPException(code);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (!mCancel) {
                    mHaveError = true;
                }
            } finally {
                isRunning = false;
                onThreadEnd();
            }
        }
    }
}
