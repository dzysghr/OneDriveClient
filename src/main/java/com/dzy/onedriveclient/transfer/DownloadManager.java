package com.dzy.onedriveclient.transfer;

import com.dzy.onedriveclient.model.gen.TaskInfoDao;
import com.dzy.onedriveclient.utils.RxHelper;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/** 下载管理
 * Created by dzysg on 2017/4/29 0029.
 */

public class DownloadManager extends AbstractManager {

    public DownloadManager(CoreContext coreContext) {
        super(coreContext);
    }

    @Override
    AbstractDispatcher provideDispatcher(CoreContext coreContext,BaseListener listener){
        return new DownloadDispatcher(coreContext, listener);
    }

    @Override
    List<TaskHandle> initTaskList() {
        return Observable.just(mContext.getTaskDao().queryBuilder().where(TaskInfoDao.Properties.Tag.eq("download")).list())
                .map(new Function<List<TaskInfo>, List<TaskHandle>>() {
                    @Override
                    public List<TaskHandle> apply(@NonNull List<TaskInfo> taskInfos) throws Exception {
                        for (TaskInfo i : taskInfos) {
                            TaskHandle handle = new TaskHandle(i, DownloadManager.this);
                            if (i.getLength() != 0 && i.getLength() == i.getFinish()) {
                                handle.setState(TaskState.STATE_FINISH);
                            } else {
                                handle.setState(TaskState.STATE_PAUSE);
                            }
                            mTaskList.add(handle);
                        }
                        return mTaskList;
                    }
                }).blockingFirst();
    }


    @Override
    public Observable<TaskHandle> createTask(final String url, final String localPath) {
        return Observable.just(1)
                .compose(RxHelper.<Integer>checkNetwork())
                .compose(RxHelper.<Integer>computation_main())
                .map(new Function<Integer, TaskHandle>() {
                    @Override
                    public TaskHandle apply(@NonNull Integer integer) throws Exception {
                        List<TaskInfo> taskInfoList = mContext
                                .getTaskDao()
                                .queryBuilder()
                                .where(TaskInfoDao.Properties.Url.eq(url), TaskInfoDao.Properties.FilePath.eq(localPath))
                                .build()
                                .list();
                        if (!taskInfoList.isEmpty()) {
                            throw new IllegalArgumentException("url or path conflict");
                        } else {
                            TaskInfo taskInfo = new TaskInfo(null, null, 0, localPath, "download", url, 0);
                            TaskHandle handle = new TaskHandle(taskInfo, DownloadManager.this);
                            mDispatcher.submit(AbstractDispatcher.MSG_CREATE, handle);
                            mTaskList.add(handle);
                            notifyListChanged();
                            return handle;
                        }
                    }
                });
    }


    @Override
    public void delete(TaskHandle handle, boolean deleteFile) {
        super.delete(handle, deleteFile);
        if (deleteFile && handle.getState() == TaskState.STATE_FINISH) {
            new File(handle.getPath()).delete();
        }
    }
}
