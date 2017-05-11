package com.dzy.onedriveclient.model.drive.facet;

/**
 * Created by dzysg on 2017/5/11 0011.
 */

public class Thumbnail {

    /**
     * width : 100
     * height : 100
     * url : http://onedrive.com/asd123a/asdjlkasjdkasdjlk.jpg
     */

    private int width;
    private int height;
    private String url;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
