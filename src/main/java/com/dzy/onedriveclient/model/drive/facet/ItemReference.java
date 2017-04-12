package com.dzy.onedriveclient.model.drive.facet;

public class ItemReference {


    /**
     * driveId : string (identifier)
     * id : string (identifier)
     * path : string (path)
     * name : string
     */

    private String driveId;
    private String id;
    private String path;
    private String name;

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
