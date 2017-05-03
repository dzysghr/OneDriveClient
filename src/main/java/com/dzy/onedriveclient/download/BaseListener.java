package com.dzy.onedriveclient.download;

/**
 * Created by dzysg on 2017/4/29 0029.
 */

public interface BaseListener {
    void onTaskInit(TaskHandle handle);
    void onUpdate(TaskHandle handle);
    void onStateChange(TaskHandle handle);
}
