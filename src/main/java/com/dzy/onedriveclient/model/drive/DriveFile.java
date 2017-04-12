package com.dzy.onedriveclient.model.drive;

import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.drive.facet.ItemReference;


public class DriveFile implements IBaseFileBean {

    private DriveItem mItem;
    private boolean mIsFolder;

    public DriveFile(ItemReference reference) {
        mItem = new DriveItem();
        mItem.setId(reference.getId());
        mItem.setName(reference.getName());
        mIsFolder = true;
    }

    public DriveFile(DriveItem item){
        mItem = item;
    }

    public String getId() {
        return mItem.getId();
    }

    public void setId(String id) {
        mItem.setId(id);
    }

    @Override
    public String getName() {
        return mItem.getName();
    }

    @Override
    public String getType() {
        return "unkown";
    }

    @Override
    public boolean isFolder() {
        return mIsFolder||mItem.getFile()==null;
    }

    @Override
    public String getModifyDateTime() {
        return mItem.getLastModifiedDateTime();
    }

    @Override
    public int getSize() {
        return mItem.getSize();
    }

    @Override
    public Object getReal() {
        return mItem;
    }

    @Override
    public IBaseFileBean getParent() {
        return new DriveFile(mItem.getParentReference());
    }
}
