package com.sht.smartlock.ui.activity.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.chat.applib.adapter.ChatAllHistoryAdapter;

/**
 * 取代ListView的LinearLayout，使之能够成功嵌套在ScrollView中
 * @author terry_龙
 */
public class LinearLayoutForListView extends LinearLayout {
    private ChatAllHistoryAdapter adapter;
    private OnClickListener onClickListener = null;
    private MyItemClickListener itemClickListener;
    /**
     * 绑定布局
     */
    public void bindLinearLayout() {
        int count = adapter.getCount();
        this.removeAllViews();
        for (int i = 0; i < count; i++) {
            View v = adapter.getView(i, null, null);
            v.setOnClickListener(this.onClickListener);
            if (itemClickListener!=null){
                itemClickListener.onItemClick(v,i);
            }
            addView(v, i);
        }
        Log.v("countTAG", "" + count);
    }
    public LinearLayoutForListView(Context context) {
        super(context);
    }
    public LinearLayoutForListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 获取Adapter
     *
     * @return adapter
     */
    public ChatAllHistoryAdapter getAdpater() {
        return adapter;
    }

    /**
     * 设置数据
     *
     * @param adpater
     */
    public void setAdapter(ChatAllHistoryAdapter adpater) {
        this.adapter = adpater;
        bindLinearLayout();
    }

    /**
     * 获取点击事件
     *
     * @return
     */
    public OnClickListener getOnclickListner() {
        return onClickListener;
    }

    /**
     * 设置点击事件
     *
     * @param onClickListener
     */
    public void setOnclickLinstener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public void  setItemClickListener(MyItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }



}
