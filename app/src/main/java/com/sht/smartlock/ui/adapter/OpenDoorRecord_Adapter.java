package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.entity.OpenDoorRecordEntity;

import java.util.List;

/**
 * Created by Administrator on 2015/9/29.
 */
public class OpenDoorRecord_Adapter extends BaseAdapter{
    private Context context;
    private List<OpenDoorRecordEntity> list;
    public OpenDoorRecord_Adapter(Context context, List<OpenDoorRecordEntity> list){
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
        ODViewHlod holder;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.open_door_item,parent,false);
            holder=new ODViewHlod(convertView);
            convertView.setTag(holder);
        }else {
            holder= (ODViewHlod) convertView.getTag();
        }
        holder.tv_Name.setText(list.get(position).getSource());
        String[] times = list.get(position).getUnlock_time().split(" ");
        holder.tv_Date.setText(times[0]);
        holder.tv_Time.setText(times[1]);

        return convertView;
    }

    class ODViewHlod {

        TextView tv_Date,tv_Time,tv_Name;
        public ODViewHlod(View itemView) {
            tv_Date= (TextView) itemView.findViewById(R.id.tv_Date);
            tv_Time= (TextView) itemView.findViewById(R.id.tv_Time);
            tv_Name= (TextView) itemView.findViewById(R.id.tv_Name);
        }
    }
}
