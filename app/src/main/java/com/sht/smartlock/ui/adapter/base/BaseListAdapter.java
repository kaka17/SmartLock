package com.sht.smartlock.ui.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.sht.smartlock.R;
import com.sht.smartlock.widget.EmptyLayout;
import com.sht.smartlock.widget.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter extends BaseAdapter {
    public static final int MAX_TEXT_LENGTH = 100;

    private LayoutInflater mInflater;

    public Context mContext;
    public List _data;
    private EmptyLayout mErrorLayout;

    public BaseListAdapter(Context context, List data) {
        mContext = context;
        _data = data;
    }

    public void setErrorLayout(EmptyLayout errorLayout){
        mErrorLayout = errorLayout;
    }

    public void refresh(List data){
        _data = data;
    }

    public LayoutInflater getLayoutInflater(Context context) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return mInflater;
    }

    @Override
    public int getCount() {
        return getDataSize();
    }

    public int getDataSize() {
        return _data.size();
    }

    @Override
    public Object getItem(int arg0) {
        if (arg0 < 0)
            return null;
        if (_data.size() > arg0) {
            return _data.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(isSyncErrorLayout() && mErrorLayout != null){
            if(isEmpty()){
                mErrorLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
            }else{
                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
        }
    }

    public boolean isSyncErrorLayout(){
        return true;
    }

    public void addData(List data) {
        if (_data == null) {
            _data = new ArrayList();
        }
        _data.addAll(data);
        notifyDataSetChanged();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addItem(Object elem) {
        if (_data == null) {
            _data = new ArrayList();
        }
        _data.add(elem);
        notifyDataSetChanged();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setItem(int pos, Object elem) {
        if (_data == null) {
            _data = new ArrayList();
        }
        _data.set(pos, elem);
        notifyDataSetChanged();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addItem(int pos, Object elem) {
        if (_data == null) {
            _data = new ArrayList();
        }
        _data.add(pos, elem);
        notifyDataSetChanged();
    }

    public void removeItem(Object obj) {
        _data.remove(obj);
        notifyDataSetChanged();
    }

    public void clear() {
        _data.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getRealView(position, convertView, parent);
    }

    public abstract View getRealView(int position, View convertView, ViewGroup parent);

    public void setItemBackground(View convertView, int position){
        int colorPos = position % 2;
        convertView.setBackgroundResource(colorPos == 0 ? R.drawable.item_background_item_frist : R.drawable.item_background_item_second);
    }

    public void setItemBackground(MaterialRippleLayout layout, int position){
        int colorPos = position % 2;
        layout.setRippleBackground(layout.getResources().getColor(colorPos == 0 ? R.color.item_background_item_frist_normal : R.color.item_background_item_second_normal));
    }
}
