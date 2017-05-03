package com.dzy.onedriveclient.transfer;

/**
 * Created by dzysg on 2017/5/3 0003.
 */

public interface ITaskManager {
    void start(TaskHandle handle);
    void stop(TaskHandle handle);
    void delete(TaskHandle handle,boolean with);
}
