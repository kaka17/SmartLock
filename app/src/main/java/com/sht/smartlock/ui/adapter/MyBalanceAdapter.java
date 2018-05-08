package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.model.MyBalanceInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/1/22.
 */
public class MyBalanceAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private Context context;
    private List<MyBalanceInfo> balanceInfo;
    private ListItemClickHelp clickHelp;

    public  MyBalanceAdapter(Context context,List<MyBalanceInfo> balanceInfo,ListItemClickHelp clickHelp){
        this.context = context;
        this.balanceInfo = balanceInfo;
        this.clickHelp = clickHelp;
        layoutInflater =LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return balanceInfo.size();
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
    public View getView(int i, View view,final ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null) {
            view = layoutInflater.inflate(R.layout.balance_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTotalamount = (TextView)view.findViewById(R.id.tvTotalamount);
            viewHolder.btnAmount = (ImageView)view.findViewById(R.id.btnAmount);
            viewHolder.tvHotleName = (TextView)view.findViewById(R.id.tvHotleName);
            viewHolder.ivSong = (ImageView)view.findViewById(R.id.ivSong);
            viewHolder.ivLian = (ImageView)view.findViewById(R.id.ivLian);
            view.setTag(viewHolder);
            viewHolder.btnAmount.setTag(view);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.btnAmount.getTag();

        final int position = i;
        final View v = view;
        final int btnbancle = viewHolder.btnAmount.getId();

        viewHolder.btnAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickHelp.onClick(v, viewGroup, position, btnbancle);
            }
        });

        if(balanceInfo.get(i).getGive_flag().equals("1")){
            viewHolder.ivSong.setVisibility(View.VISIBLE);
        }else{
            viewHolder.ivSong.setVisibility(View.GONE);
        }
        if(balanceInfo.get(i).getChain_flag().equals("1")){
            viewHolder.ivLian.setVisibility(View.VISIBLE);
        }else {
            viewHolder.ivLian.setVisibility(View.GONE);
        }

        viewHolder.tvHotleName.setText(balanceInfo.get(i).getHotel_caption());
        viewHolder.tvTotalamount.setText("￥" + balanceInfo.get(i).getBalance());
        return view;
    }

    class ViewHolder{
        private TextView tvTotalamount;
        private ImageView btnAmount;
        private TextView tvHotleName;
        private ImageView ivSong,ivLian;

    }


    public interface ListItemClickHelp {
        void onClick(View item, View widget, int position, int which);
    }

}
