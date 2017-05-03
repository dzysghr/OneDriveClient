package com.dzy.onedriveclient.download;

import java.util.List;

/**
 * Created by dzysg on 2017/5/2 0002.
 */

public interface TaskListener extends BaseListener{
    void onTaskListChanged(List<TaskHandle> list);
}
