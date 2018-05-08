package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.entity.TaskEntity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/1.
 */
public class TaskCompleteAdapter extends BaseAdapter {
    private Context context;
    private List<TaskEntity> list;

    public TaskCompleteAdapter(Context context,List<TaskEntity> list){
        this.context=context;
        this.list=list;
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
            convertView= LayoutInflater.from(context).inflate(R.layout.task_item,parent,false);
            mhoilder=new ViewHoilder(convertView);
            convertView.setTag(mhoilder);
        }else {
            mhoilder= (ViewHoilder) convertView.getTag();
        }

        return convertView;
    }
    class ViewHoilder{
        private RelativeLayout relComplete,relReceive,relRelease;
        private TextView tvComplete,tvCompleteTime,tvReceive,tvReceiveTime,tvServiceror,tvRelease,tvReleaseTime;
        private ImageView ivComplete,ivReceive,ivRelease;
        public ViewHoilder(View view) {
            relComplete= (RelativeLayout) view.findViewById(R.id.relComplete);
            relReceive= (RelativeLayout) view.findViewById(R.id.relReceive);
            relRelease= (RelativeLayout) view.findViewById(R.id.relRelease);
            //
            ivComplete= (ImageView) view.findViewById(R.id.ivComplete);
            ivReceive= (ImageView) view.findViewById(R.id.ivReceive);
            ivRelease= (ImageView) view.findViewById(R.id.ivRelease);

            tvComplete= (TextView) view.findViewById(R.id.tvComplete);
            tvCompleteTime= (TextView) view.findViewById(R.id.tvCompleteTime);

            tvReceive= (TextView) view.findViewById(R.id.tvReceive);
            tvReceiveTime= (TextView) view.findViewById(R.id.tvReceiveTime);
            tvServiceror= (TextView) view.findViewById(R.id.tvServiceror);

            tvRelease= (TextView) view.findViewById(R.id.tvRelease);
            tvReleaseTime= (TextView) view.findViewById(R.id.tvReleaseTime);



        }
    }
}
