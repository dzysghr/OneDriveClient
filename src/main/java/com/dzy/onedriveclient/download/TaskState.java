package com.dzy.onedriveclient.download;

/**
 * Created by dzysg on 2017/5/1 0001.
 */

public final class TaskState {
    public static final int STATE_INIT = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_PAUSE = 3;
    public static final int STATE_ERROR = 4;
    public static final int STATE_FINISH = 5;
    public static final int STATE_WAIT = 6;
}
