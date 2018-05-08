package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.R;
import com.sht.smartlock.model.CollectionInfo;
import com.sht.smartlock.model.booking.Hotel;

import java.util.List;

/**
 * Created by Administrator on 2015/10/9.
 */
public class CollectionInfoAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private Context context;
    List<CollectionInfo> collectionInfoList;

    public CollectionInfoAdapter(Context context,List<CollectionInfo> collectionInfoList){
        this.context = context;
        this.collectionInfoList = collectionInfoList;
        layoutInflater =LayoutInflater.from(context);
    }

    public void addAll(List<CollectionInfo> collectionInfoList) {
        collectionInfoList.addAll(collectionInfoList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return collectionInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            view = layoutInflater.inflate(R.layout.my_collection_item,null);
            viewHolder = new ViewHolder();
            viewHolder.image_Hotel_Collection = (ImageView)view.findViewById(R.id.image_Hotel_Collection);
            viewHolder.tv_Hotel_CollectionName = (TextView)view.findViewById(R.id.tv_Hotel_CollectionName);
            viewHolder.tv_Hotel_Comment = (TextView)view.findViewById(R.id.tv_Hotel_Comment);
            viewHolder.tv_Hotel_Address = (TextView)view.findViewById(R.id.tv_Hotel_Address);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        ImageLoader.getInstance().displayImage(collectionInfoList.get(i).getPicture(), viewHolder.image_Hotel_Collection);
        viewHolder.tv_Hotel_CollectionName.setText(collectionInfoList.get(i).getCaption());
        viewHolder.tv_Hotel_Comment.setText(collectionInfoList.get(i).getTotal_com()+"人评论");
        viewHolder.tv_Hotel_Address.setText(collectionInfoList.get(i).getAddress());
        return view;
    }


    class ViewHolder{
        private ImageView image_Hotel_Collection;
        private TextView tv_Hotel_CollectionName;
        private TextView tv_Hotel_Comment;
        private TextView tv_Hotel_Address;
    }


}
