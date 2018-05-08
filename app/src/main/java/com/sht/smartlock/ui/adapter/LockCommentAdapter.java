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
import com.sht.smartlock.ui.activity.myview.CircleImageView;
import com.sht.smartlock.ui.entity.Comment;
import com.sht.smartlock.util.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/1/14.
 */
public class LockCommentAdapter extends BaseAdapter {
    private List<Comment> list;
    private Context mContent;

    public LockCommentAdapter(List<Comment> list,Context mContent){
        this.list=list;
        this.mContent=mContent;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoilder mhoilder;
        if (convertView==null){
            convertView= LayoutInflater.from(mContent).inflate(R.layout.lockcomment_item,parent,false);
            mhoilder=new ViewHoilder(convertView);
            convertView.setTag(mhoilder);
        }else {
            mhoilder= (ViewHoilder) convertView.getTag();
        }
        mhoilder.tvName.setText(list.get(position).getUser_name());
        mhoilder.tvContent.setText(list.get(position).getContent());
        mhoilder.tvTime.setText(DateUtil.formatTime03(list.get(position).getCreate_time()));
        ImageLoader.getInstance().displayImage(list.get(position).getUser_img(),mhoilder.ivPic);
        return convertView;
    }
    class ViewHoilder{

        private  TextView tvName,tvContent,tvTime;
        private  CircleImageView ivPic;

        public ViewHoilder(View view) {
            ivPic = (CircleImageView) view.findViewById(R.id.ivPic);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvContent = (TextView) view.findViewById(R.id.tvContent);
            tvTime = (TextView) view.findViewById(R.id.tvTime);

        }
    }
}
