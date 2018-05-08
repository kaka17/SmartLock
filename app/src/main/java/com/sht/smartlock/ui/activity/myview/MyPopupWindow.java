package com.sht.smartlock.ui.activity.myview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.entity.MyLockEntity;
import com.sht.smartlock.util.LogUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/11/5.
 */
public class MyPopupWindow extends PopupWindow {

    private final ListView lv_Popwindow;
    View mView;
    private List<MyLockEntity> list;
    private Context context;
    private AdapterView.OnItemClickListener onItemClickListener;

    public MyPopupWindow(Context context, List<MyLockEntity> list, AdapterView.OnItemClickListener onItemClickListener) {
        super(context);
        this.list = list;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.mypopwindow, null);
        lv_Popwindow = (ListView) mView.findViewById(R.id.lv_Popwindow);

        MyAdapter adapter = new MyAdapter();
        lv_Popwindow.setAdapter(adapter);
        setContentView(mView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        adapter.notifyDataSetChanged();
//        lv_Popwindow.setDivider(null);
        lv_Popwindow.setDividerHeight(1);
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00FFFFFF")
        ));
//        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")
//        ));

        lv_Popwindow.setOnItemClickListener(onItemClickListener);
        if (list.size() > 4) {
            int totalHeight = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                View listItem = adapter.getView(i, null, lv_Popwindow);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = lv_Popwindow.getLayoutParams();
            params.height = totalHeight + (lv_Popwindow.getDividerHeight() * (adapter.getCount() - 1));
            ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
            lv_Popwindow.setLayoutParams(params);
        }

        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int left = mView.findViewById(R.id.lin_Pop).getLeft();
                int bottom = mView.findViewById(R.id.lin_Pop).getBottom();
                int y = (int) event.getY();
                int x = (int) event.getX();
                LogUtil.log("paramss left:" + left + " x:" + x + " bottom:" + bottom + " y" + y);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > bottom) {
                        dismiss();
                    }
                    if (x > left) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public class MyAdapter extends BaseAdapter {

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
            PopViewHolder mholder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.mypopwindow_item, parent, false);
                mholder = new PopViewHolder(convertView);
                convertView.setTag(mholder);
            } else {
                mholder = (PopViewHolder) convertView.getTag();
            }
            mholder.tv_Name.setText(list.get(position).getHotel_caption());
            return convertView;
        }

        class PopViewHolder {
            private TextView tv_Name;

            private PopViewHolder(View view) {
                tv_Name = (TextView) view.findViewById(R.id.tv_Name);
            }
        }
    }


}
