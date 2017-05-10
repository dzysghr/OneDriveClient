package com.dzy.onedriveclient.model.drive;

import com.dzy.onedriveclient.model.drive.facet.FileFacet;
import com.dzy.onedriveclient.model.drive.facet.FolderFacet;
import com.dzy.onedriveclient.model.drive.facet.ItemReference;

public class DriveItem {

    private String id;
    private String name;
    private long size;
    private String createdDateTime;
    private String lastModifiedDateTime;
    private FileFacet file;
    private FolderFacet folder;
    private String eTag;
    private ItemReference parentReference;

    public ItemReference getParentReference() {
        return parentReference;
    }

    public void setParentReference(ItemReference parentReference) {
        this.parentReference = parentReference;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(String lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    public FileFacet getFile() {
        return file;
    }

    public void setFile(FileFacet file) {
        this.file = file;
    }

    public FolderFacet getFolder() {
        return folder;
    }

    public void setFolder(FolderFacet folder) {
        this.folder = folder;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }
}
