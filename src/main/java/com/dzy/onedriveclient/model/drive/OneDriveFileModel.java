package com.dzy.onedriveclient.model.drive;

import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.IFileModel;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.utils.RxHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;


public class OneDriveFileModel implements IFileModel {


    private IOAuthModel mIOAuthModel;
    private IDriveFileModel mIDriveFileModel;
    private TokenBean mTokenBean;
    private String mSelectField = "id,name,size,createdDateTime,lastModifiedDateTime,file,folder,eTag,parentReference";

    public OneDriveFileModel() {
        mIOAuthModel = ModelFactory.getOAuthModel();
        mIDriveFileModel = ModelFactory.getDriveFileModel();
        mTokenBean = Constants.sToken;
    }

    @Override
    public Observable<List<IBaseFileBean>> getChildren(String path) {
        return mIDriveFileModel.getListByPath(path, mSelectField, 10)
                .compose(RxHelper.handleChildren())
                .flatMap(new Function<List<DriveItem>, ObservableSource<List<IBaseFileBean>>>() {
                    @Override
                    public ObservableSource<List<IBaseFileBean>> apply(@NonNull List<DriveItem> driveItems) throws Exception {
                        List<IBaseFileBean> list = convertToFileList(driveItems);
                        return Observable.just(list);
                    }
                });
    }

    @Override
    public Observable<List<IBaseFileBean>> getChildren(IBaseFileBean bean) {
        if (bean == null) {
            return getChildren("root");
        } else {
            DriveFile file = (DriveFile) bean;
            DriveItem item = (DriveItem) file.getReal();
            String id =item.getId() ;
            try {
                id = URLEncoder.encode(item.getId(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return mIDriveFileModel.getListById(id, mSelectField, 10)
                    .compose(RxHelper.handleChildren())
                    .flatMap(new Function<List<DriveItem>, ObservableSource<List<IBaseFileBean>>>() {
                        @Override
                        public ObservableSource<List<IBaseFileBean>> apply(@NonNull List<DriveItem> driveItems) throws Exception {
                            List<IBaseFileBean> list = convertToFileList(driveItems);
                            return Observable.just(list);
                        }
                    });
        }
    }

    private String getPath(DriveItem item){
        String path = item.getParentReference().getPath();
        if (path.startsWith("/drive")){
            path = path.substring(6);
        }
        if (path.endsWith(":")){
            path = path.substring(0,path.length()-1);
        }
        path = path+"/"+item.getName();
        return path;
    }

    @Override
    public Observable<Boolean> delete(IBaseFileBean bean) {
        return null;
    }

    @Override
    public Observable<Boolean> copy(IBaseFileBean from, IBaseFileBean to) {
        return null;
    }

    @Override
    public Observable<Boolean> cut(IBaseFileBean from, IBaseFileBean to) {
        return null;
    }

    @Override
    public Observable<Boolean> createFolder(IBaseFileBean parent, String name) {
        return null;
    }

    @Override
    public Observable<Boolean> exists(IBaseFileBean bean) {
        return null;
    }

    private static IBaseFileBean convertToFileBean(DriveItem item) {
        return new DriveFile(item);
    }


    private static List<IBaseFileBean> convertToFileList(List<DriveItem> items) {
        List<IBaseFileBean> list = new ArrayList<>();
        for (DriveItem i : items) {
            list.add(new DriveFile(i));
        }
        return list;
    }

}
