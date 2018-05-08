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
import com.sht.smartlock.ui.entity.LockGroupsChatEntity;

import java.util.List;

/**
 * Created by Administrator on 2015/10/15.
 */
public class MyLockChat_Adapter extends BaseAdapter {
    private List<LockGroupsChatEntity> list;
    private Context context;

    public MyLockChat_Adapter(List<LockGroupsChatEntity> list,Context context){
        this.list=list;
        this.context=context;

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
        ViewHolder mholder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.mylock_item,parent,false);
            mholder=new ViewHolder(convertView);
            convertView.setTag(mholder);
        }else {
            mholder= (ViewHolder) convertView.getTag();
        }
        mholder.tv_LockName.setText(list.get(position).getCaption());
        mholder.tv_Many_Person.setText(list.get(position).getOn_num());
        mholder.tv_Lock_Distance.setText(list.get(position).getAddress());
        String img=list.get(position).getPicture();
        if(img!=null)
            ImageLoader.getInstance().displayImage(img,mholder.iv_lock_logo);

        if (position==(list.size()-1)){
            mholder.view_enpty.setVisibility(View.GONE);
        }

        return convertView;
    }

    class  ViewHolder{
        private TextView tv_Many_Person;
        private TextView tv_LockName;
        private TextView tv_Lock_Distance;
        private View view_enpty;
        private ImageView iv_lock_logo;

        private ViewHolder(View itemView){
            tv_Many_Person= (TextView) itemView.findViewById(R.id.tv_Many_Person);
            tv_LockName= (TextView) itemView.findViewById(R.id.tv_LockName);
            tv_Lock_Distance= (TextView) itemView.findViewById(R.id.tv_Lock_Distance);
            iv_lock_logo= (ImageView) itemView.findViewById(R.id.iv_lock_logo);
            view_enpty=itemView.findViewById(R.id.view_enpty);
        }
    }
}
