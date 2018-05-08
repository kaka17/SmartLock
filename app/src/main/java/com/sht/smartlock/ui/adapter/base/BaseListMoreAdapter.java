package com.sht.smartlock.ui.adapter.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.sht.smartlock.R;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.util.DeviceUtil;

import java.util.List;

public abstract class BaseListMoreAdapter extends BaseListAdapter {
    public static final int STATE_EMPTY_ITEM = 0;
    public static final int STATE_LOAD_MORE = 1;
    public static final int STATE_NO_MORE = 2;
    public static final int STATE_NO_DATA = 3;
    public static final int STATE_LESS_ONE_PAGE = 4;
    public static final int STATE_NETWORK_ERROR = 5;

    protected int state = STATE_LESS_ONE_PAGE;

    protected int _loadmoreText;
    protected int _loadFinishText;

    private boolean mLoadMoreHasBg = true;

    public BaseListMoreAdapter(Context context, List data) {
        super(context, data);
        _loadmoreText = R.string.loading;
        _loadFinishText = R.string.loading_no_more;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    @Override
    public int getCount() {
        switch (getState()) {
            case STATE_EMPTY_ITEM:
                return getDataSize() + 1;
            case STATE_NETWORK_ERROR:
            case STATE_LOAD_MORE:
                return getDataSize() + 1;
            case STATE_NO_DATA:
                return 0;
            case STATE_NO_MORE:
                return getDataSize() + 1;
            case STATE_LESS_ONE_PAGE:
                return getDataSize();
            default:
                break;
        }
        return getDataSize();
    }

    public void setLoadmoreText(int loadmoreText) {
        _loadmoreText = loadmoreText;
    }

    public void setLoadFinishText(int loadFinishText) {
        _loadFinishText = loadFinishText;
    }

    public void setLoadMoreHasBg(boolean flag){
        this.mLoadMoreHasBg = flag;
    }

    protected boolean loadMoreHasBg() {
        return mLoadMoreHasBg;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1) {
            if (getState() == STATE_LOAD_MORE || getState() == STATE_NO_MORE || state == STATE_EMPTY_ITEM || getState() == STATE_NETWORK_ERROR) {
                View loadmore =  LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_cell_footer, null);
                if (!loadMoreHasBg()) {
                    loadmore.setBackgroundColor(parent.getContext().getResources().getColor(R.color.transparent));
                }
                ProgressBar progress = (ProgressBar) loadmore.findViewById(R.id.progressbar);
                TextView text = (TextView) loadmore.findViewById(R.id.text);
                switch (getState()) {
                    case STATE_LOAD_MORE:
                        loadmore.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                        text.setText(_loadmoreText);
                        break;
                    case STATE_NO_MORE:
                        loadmore.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                        text.setVisibility(View.VISIBLE);
                        text.setText(_loadFinishText);
                        break;
                    case STATE_EMPTY_ITEM:
                        progress.setVisibility(View.GONE);
                        loadmore.setVisibility(View.GONE);
                        text.setVisibility(View.GONE);
                        break;
                    case STATE_NETWORK_ERROR:
                        loadmore.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                        text.setVisibility(View.VISIBLE);
                        if (DeviceUtil.hasInternet()) {
                            text.setText(BaseApplication.context().getString(R.string.tip_load_data_error));
                        } else {
                            text.setText(BaseApplication.context().getString(R.string.tip_network_error));
                        }
                        break;
                    default:
                        progress.setVisibility(View.GONE);
                        loadmore.setVisibility(View.GONE);
                        text.setVisibility(View.GONE);
                        break;
                }
                return loadmore;
            }
        }
        return getRealView(position, convertView, parent);
    }
}
