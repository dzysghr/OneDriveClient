package com.dzy.onedriveclient.module.file;

import android.support.annotation.LayoutRes;

import com.dzy.commemlib.ui.BaseAdapter.CommonAdapter;
import com.dzy.commemlib.ui.BaseAdapter.ContentHolder;
import com.dzy.onedriveclient.R;
import com.dzy.onedriveclient.model.IBaseFileBean;

import java.util.List;

/**
 * Created by dzysg on 2017/4/3 0003.
 */

public class FileListAdapter extends CommonAdapter<IBaseFileBean> {


    public FileListAdapter(List<IBaseFileBean> datas, @LayoutRes int id) {
        super(datas, id);
    }

    @Override
    public void bindView(ContentHolder holder, int position, IBaseFileBean item) {
        IBaseFileBean bean = mDatas.get(position);

        holder.setText(R.id.tv_item_name,bean.getName());
        if (bean.isFolder()){
            holder.setImageResource(R.id.iv_item_icon,R.mipmap.folder);
        }else{
            String type = bean.getType();
            switch (type){
                case "txt":holder.setImageResource(R.id.iv_item_icon,R.mipmap.file);break;

                default:
                    holder.setImageResource(R.id.iv_item_icon,R.mipmap.file);break;
            }
        }
    }
}
