package com.dzy.onedriveclient.model.drive;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultBean {

    @SerializedName("@odata.context")
    public String context;

    public List<DriveItem> value;

    @SerializedName("@odata.nextLink")
    public String next;
}
