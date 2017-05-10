package com.dzy.onedriveclient.transfer;

import com.dzy.onedriveclient.model.gen.TaskInfoDao;
import com.dzy.onedriveclient.utils.RxHelper;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by dzysg on 2017/5/4 0004.
 */

public class UploadManager extends AbstractManager {

    public UploadManager(CoreContext coreContext) {
        super(coreContext);
    }

    @Override
    AbstractDispatcher provideDispatcher(CoreContext coreContext, BaseListener listener) {
        return new UploadDispatcher(coreContext,listener);
    }


    @Override
    List<TaskHandle> initTaskList() {
        return Observable.just(mContext.getTaskDao().queryBuilder().where(TaskInfoDao.Properties.Tag.eq("upload")).list())
                .map(new Function<List<TaskInfo>, List<TaskHandle>>() {
                    @Override
                    public List<TaskHandle> apply(@NonNull List<TaskInfo> taskInfos) throws Exception {
                        for (TaskInfo i : taskInfos) {
                            TaskHandle handle = new TaskHandle(i, UploadManager.this);
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
    public Observable<TaskHandle> createTask(String... arg) {
        final String fileid = arg[0];
        final String localPath = arg[1];
        return Observable.just(1)
                .compose(RxHelper.<Integer>checkNetwork())
                .compose(RxHelper.<Integer>computation_main())
                .map(new Function<Integer, TaskHandle>() {
                    @Override
                    public TaskHandle apply(@NonNull Integer integer) throws Exception {
                        List<TaskInfo> taskInfoList = mContext
                                .getTaskDao()
                                .queryBuilder()
                                .where(TaskInfoDao.Properties.Tag.eq("upload"),TaskInfoDao.Properties.FilePath.eq(localPath))
                                .build()
                                .list();
                        if (!taskInfoList.isEmpty()) {
                            throw new IllegalArgumentException("任务已经存在");
                        } else {
                            DLHelper.checkExistFile(localPath);
                            TaskInfo taskInfo = new TaskInfo(null, fileid,new File(localPath).length(), localPath, "upload", null, 0);
                            TaskHandle handle = new TaskHandle(taskInfo, UploadManager.this);
                            mDispatcher.submit(AbstractDispatcher.MSG_CREATE, handle);
                            mTaskList.add(handle);
                            notifyListChanged();
                            return handle;
                        }
                    }
                });
    }
}
