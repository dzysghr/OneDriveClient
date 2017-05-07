package com.dzy.onedriveclient.transfer;

/**
 * Created by dzysg on 2017/5/3 0003.
 */

public interface ITask {
    void execute();
    void stop();
    boolean isRunning();
    void cancel();
}
