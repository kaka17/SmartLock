package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sht.smartlock.R;

import java.util.List;

/**
 * Created by Administrator on 2017/1/14.
 */
public class LockServicerAdapter extends BaseAdapter {
    private List<String> lists;
    private List<String> listStr;
    private Context mContext;
    public LockServicerAdapter(List<String> listStr,List<String> lists,Context mContext){
        this.listStr=listStr;
        this.lists=lists;
        this.mContext=mContext;
    }
    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHoilder mhoilder;
        if (convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.lockservicer_item,parent,false);
            mhoilder=new ViewHoilder(convertView);
            convertView.setTag(mhoilder);
        }else {
            mhoilder= (ViewHoilder) convertView.getTag();
        }
        mhoilder.tvName.setText(listStr.get(position));
//        mhoilder.ivServicer.setImageResource(lists.get(position));
        ImageLoader.getInstance().displayImage(lists.get(position), mhoilder.ivServicer, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                mhoilder.tvName.setVisibility(View.VISIBLE);
                Log.e("ImgLoading","------------>onLoadingFailed"+position);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                mhoilder.tvName.setVisibility(View.GONE);
                Log.e("ImgLoading", "------------>onLoadingComplete" + position);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                mhoilder.tvName.setVisibility(View.VISIBLE);
                Log.e("ImgLoading", "------------>onLoadingCancelled" + position);
            }
        });
        return convertView;
    }
    class ViewHoilder{
        private ImageView ivServicer;
        private TextView tvName;
        public ViewHoilder(View view) {
            ivServicer= (ImageView) view.findViewById(R.id.ivServicer);
            tvName= (TextView) view.findViewById(R.id.tvName);
        }
    }
}
