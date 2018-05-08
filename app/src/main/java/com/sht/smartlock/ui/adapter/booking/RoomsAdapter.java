package com.sht.smartlock.ui.adapter.booking;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.R;
import com.sht.smartlock.model.booking.HotelRoom;
import com.sht.smartlock.ui.activity.myinterface.MyItemClickListener;
import com.sht.smartlock.ui.entity.HotelRoomEntity;

import java.util.List;

/**
 * Created by Administrator on 2017/1/11.
 */
public class RoomsAdapter extends BaseAdapter {
    private List<HotelRoom> list;
    private Context mContext;
    private MyItemClickListener myItemClickListener;
    private boolean judge_red_bag=false;//酒店是否有红包
    private boolean is_red_bag=false;//我是否有改酒店的红包
    DisplayImageOptions options;

    public RoomsAdapter(List<HotelRoom> list,Context mContext){
        this.list=list;
        this.mContext=mContext;
        options= new DisplayImageOptions.Builder()
//								.showImageOnLoading(R.drawable.default_image)
                .showImageOnFail(R.drawable.pic_hotel_details)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }
    private void isJudge_red_bag(boolean judge_red_bag){
        this.judge_red_bag=judge_red_bag;
    }
    private void is_red_bag(boolean is_red_bag){
        this.is_red_bag=is_red_bag;
    }
    public void setOnItemClickListenter(MyItemClickListener myItemClickListener){
        this.myItemClickListener=myItemClickListener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHoilder mhoilder;
        if (convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.rooms_item,parent,false);
            mhoilder=new ViewHoilder(convertView);
            convertView.setTag(mhoilder);
        }else {
            mhoilder= (ViewHoilder) convertView.getTag();
        }

        mhoilder.tvName.setText(list.get(position).getCaption());
        mhoilder.tvPice.setText("￥"+list.get(position).getPrice());
        mhoilder.tvServicer.setText(list.get(position).getFacility());
        if (list.get(position).getHour()!=0){
            mhoilder.tvHour.setText("/"+list.get(position).getHour()+"h");
            mhoilder.tvHour.setVisibility(View.VISIBLE);
        }else {
            mhoilder.tvHour.setVisibility(View.GONE);
        }
//        if (hotel.is_red_bag()){//我有该酒店的红包
//            lc.getIvIsCoupons().setImageResource(R.drawable.pic_mark_coupons);
//            lc.getIvIsCoupons().setVisibility(View.VISIBLE);
//            if (hotel.isJudge_red_bag()){//酒店有活动
//                lc.getIvDiscout().setImageResource(R.drawable.pic_mark_discout);
//                lc.getIvDiscout().setVisibility(View.VISIBLE);
//            }
//        }else {
//            lc.getIvIsCoupons().setVisibility(View.GONE);
//            if (hotel.isJudge_red_bag()){//酒店有活动
//                lc.getIvDiscout().setImageResource(R.drawable.pic_mark_discout);
//                lc.getIvDiscout().setVisibility(View.VISIBLE);
//            }else {
//                lc.getIvDiscout().setVisibility(View.GONE);
//            }
//        }


        ImageLoader.getInstance().displayImage(list.get(position).getType_image(),mhoilder.ivLockImg,options);
        mhoilder.ivBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myItemClickListener.onItemClick(v, position);
            }
        });

        return convertView;
    }
    class ViewHoilder{
        private  TextView tvName,tvServicer,tvPice,tvHour;
        private ImageView ivLockImg,ivConpons,ivDiscout,ivBook;

        public ViewHoilder(View convertView) {
            ivLockImg = (ImageView) convertView.findViewById(R.id.ivLockImg);
            ivConpons = (ImageView) convertView.findViewById(R.id.ivConpons);
            ivDiscout = (ImageView) convertView.findViewById(R.id.ivDiscout);
            ivBook = (ImageView) convertView.findViewById(R.id.ivBook);
            tvName = (TextView) convertView.findViewById(R.id.tvName);
            tvServicer = (TextView) convertView.findViewById(R.id.tvServicer);
            tvPice = (TextView) convertView.findViewById(R.id.tvPice);
            tvHour = (TextView) convertView.findViewById(R.id.tvHour);
        }
    }
}
