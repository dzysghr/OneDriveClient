package com.dzy.onedriveclient.transfer;

/**
 * Created by dzysg on 2017/4/29 0029.
 */

public final class DLHelper {
    private DLHelper(){}

    public static void checkNull(Object o,String msg){
        if (o==null){
            throw new NullPointerException(msg+" is null");
        }
    }


}
