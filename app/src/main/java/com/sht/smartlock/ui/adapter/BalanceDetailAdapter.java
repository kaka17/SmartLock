package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.model.AccountBalanceDetailInfo;
import com.sht.smartlock.model.BalancedetailInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/1/27.
 */
public class BalanceDetailAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private List<AccountBalanceDetailInfo> listBalancedetail;

    public BalanceDetailAdapter(Context context,List<AccountBalanceDetailInfo> listBalancedetail){
        this.context = context;
        this.listBalancedetail = listBalancedetail;
        layoutInflater =LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listBalancedetail.size();
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
        if(view == null) {
            view = layoutInflater.inflate(R.layout.balancedetail_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvWhetherPrepaidExpenses = (TextView)view.findViewById(R.id.tvWhetherPrepaidExpenses);
            viewHolder.tvPrepaidExpensesTime = (TextView)view.findViewById(R.id.tvPrepaidExpensesTime);
            viewHolder.tvAmountMoney = (TextView)view.findViewById(R.id.tvAmountMoney);
            viewHolder.tvGifts = (TextView)view.findViewById(R.id.tvGifts);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.tvWhetherPrepaidExpenses.setText(listBalancedetail.get(i).getCaption());
        viewHolder.tvPrepaidExpensesTime.setText(listBalancedetail.get(i).getCreate_time());
        viewHolder.tvAmountMoney.setText(listBalancedetail.get(i).getBalance());
        viewHolder.tvGifts.setText(listBalancedetail.get(i).getRecord_balance());
        return view;
    }


   class ViewHolder{
       private TextView tvWhetherPrepaidExpenses;
       private TextView tvPrepaidExpensesTime;
       private TextView tvAmountMoney;
       private TextView tvGifts;
   }


}
