package com.dzy.onedriveclient.model.drive;

import com.dzy.onedriveclient.config.Constants;
import com.dzy.onedriveclient.model.IBaseFileBean;
import com.dzy.onedriveclient.model.IFileModel;
import com.dzy.onedriveclient.model.ModelFactory;
import com.dzy.onedriveclient.utils.RxHelper;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;


public class OneDriveFileModel implements IFileModel {


    private static final String PREFER_ASYNC = "respond-async";
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
    public Observable<List<IBaseFileBean>> getChildren(String path,int cache) {
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
    public Observable<List<IBaseFileBean>> getChildren(IBaseFileBean bean,int cache) {
        if (bean == null) {
            return getChildren("root",cache);
        } else {
            DriveFile file = (DriveFile) bean;
            DriveItem item = (DriveItem) file.getReal();
            return mIDriveFileModel.getListById(item.getId(), mSelectField, 10)
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
        DriveItem file = (DriveItem) bean.getReal();
        return mIDriveFileModel.delete(file.getId())
                .compose(RxHelper.handleEmptyRespone(204));
    }

    @Override
    public Observable<Boolean> copy(IBaseFileBean from, IBaseFileBean to) {
        DriveFile file = (DriveFile) from;
        String json;
        if (to==null){
            json = "{\n" +
                    "  \"parentReference\": {\n" +
                    "    \"path\": \"/drive/root\"\n" +
                    "  }\n" +
                    "}";
        }else{
            DriveFile fileTo = (DriveFile) to;
            json = "{\n" +
                    "  \"parentReference\": {\n" +
                    "    \"id\": \""+fileTo.getId()+"\"\n" +
                    "  }\n" +
                    "}";
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),json);
        return mIDriveFileModel.copy(file.getId(),requestBody,PREFER_ASYNC)
                .compose(RxHelper.handleEmptyRespone(202));
    }

    @Override
    public Observable<Boolean> cut(final IBaseFileBean from, IBaseFileBean to) {
        DriveFile file = (DriveFile) from;
        String json;
        if (to==null){
            json = "{\n" +
                    "  \"parentReference\": {\n" +
                    "    \"path\": \"/drive/root\"\n" +
                    "  }\n" +
                    "}";
        }else{
            DriveFile fileTo = (DriveFile) to;
            json = "{\n" +
                    "  \"parentReference\": {\n" +
                    "    \"id\": \""+fileTo.getId()+"\"\n" +
                    "  }\n" +
                    "}";
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),json);
        return mIDriveFileModel.update(file.getId(),requestBody)
                .compose(RxHelper.handle(new TypeToken<DriveItem>(){}))
                .map(new Function<DriveItem, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull DriveItem driveItem) throws Exception {
                        return driveItem!=null;
                    }
                });
    }

    @Override
    public Observable<IBaseFileBean> createFolder(IBaseFileBean parent, String name) {

        String json="{\"name\":\""+name+"\", \"folder\": { }}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),json);


        Observable<Response<ResponseBody>> observable = null;
        if (parent==null){
            observable = mIDriveFileModel.createFolderByPath("root",requestBody);
        }else {
            DriveFile file = (DriveFile) parent;

            observable = mIDriveFileModel.createFolder(file.getId(),requestBody);
        }
        return observable.compose(RxHelper.handle(new TypeToken<DriveItem>(){},201))
                .flatMap(new Function<DriveItem, ObservableSource<IBaseFileBean>>() {
                    @Override
                    public ObservableSource<IBaseFileBean> apply(@NonNull DriveItem driveItem) throws Exception {
                        return Observable.just(convertToFileBean(driveItem));
                    }
                });
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
