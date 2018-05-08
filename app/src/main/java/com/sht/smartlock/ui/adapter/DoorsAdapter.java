package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.entity.DoorsEntitys;

import java.util.List;

/**
 * Created by Administrator on 2016/12/26.
 */
public class DoorsAdapter extends BaseAdapter {

    private List<DoorsEntitys> list;
    private Context mcontext;
    public DoorsAdapter(Context mcontext,List<DoorsEntitys> list){
        this.mcontext=mcontext;
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
        ViewHolder mholder;
        if (convertView==null){
            convertView= LayoutInflater.from(mcontext).inflate(R.layout.dialog_doors,parent,false);
            mholder=new ViewHolder(convertView);
            convertView.setTag(mholder);
        }else {
            mholder= (ViewHolder) convertView.getTag();
        }
        mholder.tvName.setText(list.get(position).getLock_name());
        mholder.tvName.setText(list.get(position).getLock_name());
        return convertView;
    }
    class ViewHolder{

        private final TextView tvName;

        public ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.tvName);
        }
    }
}
