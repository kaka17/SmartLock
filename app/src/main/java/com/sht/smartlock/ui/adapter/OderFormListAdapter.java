package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.activity.base.BaseActivity;

/**
 * Created by Administrator on 2015/10/5.
 */
public class OderFormListAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private Context context;
    private String str_name[];
    private int str_picname[] = {R.drawable.bookinghotel,R.drawable.ordering,R.drawable.shop};

    public OderFormListAdapter(Context context,String str_name[]){
        this.context = context;
        this.str_name = str_name;
        layoutInflater =LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return str_name.length;
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
            view = layoutInflater.inflate(R.layout.oderformlist_item,null);
            viewHolder = new ViewHolder();
            viewHolder.image_oder_title = (ImageView)view.findViewById(R.id.image_oder_title);
            viewHolder.text_oder_title = (TextView)view.findViewById(R.id.text_oder_title);
//            viewHolder.image_oder_rightrower = (ImageView)view.findViewById(R.id.image_oder_rightrower);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.image_oder_title.setBackgroundResource(str_picname[i]);
        viewHolder.text_oder_title.setText(str_name[i]);
        return view;
    }

    class ViewHolder{
        private ImageView image_oder_title;
        private TextView text_oder_title;
//        private ImageView image_oder_rightrower;
    }

}
