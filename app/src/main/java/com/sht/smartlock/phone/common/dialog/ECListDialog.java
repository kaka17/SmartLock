package com.sht.smartlock.phone.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sht.smartlock.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * com.yuntongxun.ecdemo.common.dialog in ECDemo_Android
 * Created by Jorstin on 2015/4/18.
 */
public class ECListDialog extends ECAlertDialog implements AdapterView.OnItemClickListener {

    private List<Integer> mCheckIndex;
    private ListView mListView;
    private OnDialogItemClickListener mListener;
    private boolean mItemClickDis = true;
    /**
     * @param context
     */
    public ECListDialog(Context context) {
        super(context);
        mCheckIndex = new ArrayList<Integer>();
        mListener = null;
        mListView = null;
        // setTitleNormalColor();
        View contatinView = LayoutInflater.from(context).inflate(R.layout.include_dialog_simplelist ,null);
        setContentView(contatinView);
        setContentPadding(0,0,-1,-1);
        mListView = (ListView) contatinView.findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);
    }

    /**
     *据数组资源文件创建\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
     * @param context
     * @param resourceIdArray
     */
    public ECListDialog(Context context , int resourceIdArray) {
        this(context);
        String[] stringArray = context.getResources().getStringArray(resourceIdArray);
        setAdapter(new ListDialogAdapter(getContext(), Arrays.asList(stringArray)));
    }

    /**
     * 根据集合数组创建
     * @param context
     * @param strs
     */
    public ECListDialog(Context context , List<String> strs) {
        this(context);
        setAdapter(new ListDialogAdapter(getContext(), strs));
    }

    public ECListDialog(Context context , List<String> strs  , int checkPosition) {
        this(context, strs);
        this.mCheckIndex .add(checkPosition);

    }

    public void setChecks(List<Integer> checks) {
        if(checks == null) {
            return ;
        }
        mCheckIndex.addAll(checks);
    }

    public List<Integer> getCheck() {
        if(mCheckIndex == null) {
            mCheckIndex = new ArrayList<Integer>();
        }
        return mCheckIndex;
    }

    public ECListDialog(Context context , String[] strs , int checkPosition) {
        this(context ,strs);
        this.mCheckIndex .add(checkPosition);
    }

    public ECListDialog(Context context , String[] strs) {
        this(context);
        setAdapter(new ListDialogAdapter(getContext(), Arrays.asList(strs)));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mListener != null) {
            if(!mCheckIndex.remove(Integer.valueOf(position))) {
                mCheckIndex.add(position);
            }
            mListener.onDialogItemClick(this, position);
        }
        if(mItemClickDis) {
            dismiss();
            return;
        }
        ((ListDialogAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    public void setAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }



    public class ListDialogAdapter extends IBaseAdapter<String> {


        public ListDialogAdapter(Context ctx, List<String> data) {
            super(ctx, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = this.mLayoutInflater.inflate(R.layout.listitem_dialog , null);
            }
            ((TextView) convertView.findViewById(R.id.textview)).setText(getItem(position).toString());
            if(mCheckIndex.contains(position)) {
                convertView.findViewById(R.id.imageview).setVisibility(View.VISIBLE);
            } else {
                convertView.findViewById(R.id.imageview).setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    public void setOnDialogItemClickListener(OnDialogItemClickListener l) {
        setOnDialogItemClickListener(true , l);
    }
    public void setOnDialogItemClickListener(boolean mItemClickDis , OnDialogItemClickListener l) {
        this.mItemClickDis = mItemClickDis;
        this.mListener = l;
    }

    public interface OnDialogItemClickListener {
        void onDialogItemClick(Dialog d, int position);
    }
}
