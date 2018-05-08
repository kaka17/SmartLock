package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;

/**
 * Created by Administrator on 2015/9/15.
 */
public class PersonSttingAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private Context context;
    private String str_name[];

   public PersonSttingAdapter(Context context,String str_name[]){
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
            view = layoutInflater.inflate(R.layout.main_tab_mine_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_persondata = (TextView)view.findViewById(R.id.tv_persondata);
            viewHolder.image_rightarrow = (ImageView)view.findViewById(R.id.image_rightarrow);
            viewHolder.image_underline = (ImageView)view.findViewById(R.id.image_underline);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.tv_persondata.setText(str_name[i]);
        return view;
    }


    class ViewHolder{
        private TextView tv_persondata;
        private ImageView image_underline;
        private ImageView image_rightarrow;
    }

}
