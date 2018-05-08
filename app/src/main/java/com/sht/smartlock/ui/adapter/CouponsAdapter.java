package com.sht.smartlock.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sht.smartlock.R;
import com.sht.smartlock.ui.entity.CouponsEntity;
import com.sht.smartlock.util.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/28.
 */
public class CouponsAdapter extends BaseAdapter {
    private List<CouponsEntity> list;
    private Context mContext;

    public CouponsAdapter(List<CouponsEntity> list,Context mContext){
        this.list=list;
        this.mContext=mContext;
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
        ViewHoilder mhoilder;
        if (convertView==null){
           convertView= LayoutInflater.from(mContext).inflate(R.layout.coupons_item,parent,false);
            mhoilder=new ViewHoilder(convertView);
            convertView.setTag(mhoilder);
        }else {
            mhoilder= (ViewHoilder) convertView.getTag();
        }

        switch (list.get(position).getType()){
            case "1"://优惠券
                mhoilder.ivImg.setImageResource(R.drawable.pic_youhuiquan);
                mhoilder.tvCoupons.setText(mContext.getString(R.string.Coupons01));
                mhoilder.tvMoney.setVisibility(View.VISIBLE);
                mhoilder.tvMoney.setText(list.get(position).getYh_price());
                break;
            case "2"://折扣
                mhoilder.ivImg.setImageResource(R.drawable.pic_zhekou);
                mhoilder.tvCoupons.setText(list.get(position).getRoom_type_name());
                mhoilder.tvMoney.setText(list.get(position).getDiscount()*10+"折");
                mhoilder.tvMoney.setVisibility(View.VISIBLE);
                break;
            case "3"://代金券
                mhoilder.ivImg.setImageResource(R.drawable.pic_daijinquan);
                mhoilder.tvCoupons.setText(mContext.getString(R.string.Coupons03));
                mhoilder.tvMoney.setText(list.get(position).getDj_price());
                mhoilder.tvMoney.setVisibility(View.VISIBLE);
                break;
            case "4"://礼品券
                mhoilder.ivImg.setImageResource(R.drawable.pic_liquan);
                mhoilder.tvCoupons.setText(mContext.getString(R.string.Coupons04));
                mhoilder.tvMoney.setVisibility(View.GONE);
                break;
        }

        mhoilder.tvHotleName.setText(list.get(position).getHotel_name());
        mhoilder.tvContent.setText(list.get(position).getComment());
        mhoilder.tvTime.setText(mContext.getString(R.string.CouponsTime)+DateUtil.formatTime02(list.get(position).getStart_time()) + "-" + DateUtil.formatTime02(list.get(position).getEnd_time()));

        if (!list.get(position).getUsed_time().equals("null")){
            mhoilder.ivOver.setImageResource(R.drawable.pic_mark_used);
            mhoilder.ivOver.setVisibility(View.VISIBLE);
        }else {
            if (list.get(position).getTime().equals("1")){//未过期
                mhoilder.ivOver.setVisibility(View.GONE);
            }else if(list.get(position).getTime().equals("2")){//已过期
                mhoilder.ivOver.setVisibility(View.VISIBLE);
                mhoilder.ivOver.setImageResource(R.drawable.pic_mark_overtime);
            }else {
                mhoilder.ivOver.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class ViewHoilder{

        private  ImageView ivImg,ivOver;
        private TextView tvHotleName,tvContent,tvTime,tvCoupons,tvMoney;
        private ViewHoilder(View view){
            ivImg = (ImageView) view.findViewById(R.id.ivImg);
            ivOver = (ImageView) view.findViewById(R.id.ivOver);
            tvHotleName=(TextView) view.findViewById(R.id.tvHotleName);
            tvContent=(TextView) view.findViewById(R.id.tvContent);
            tvTime=(TextView) view.findViewById(R.id.tvTime);
            tvCoupons=(TextView) view.findViewById(R.id.tvCoupons);
            tvMoney=(TextView) view.findViewById(R.id.tvMoney);


        }
    }
}
