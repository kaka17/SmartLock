package com.sht.smartlock.ui.adapter;

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
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.model.booking.Hotel;
import com.sht.smartlock.ui.entity.NearHotelEntity;

import java.util.List;

/**
 * Created by Administrator on 2016/11/14.
 */
public class NearHotelAdapter extends BaseAdapter {
    private Context context;
    private List<Hotel> list;
    private boolean nearBy;

    public NearHotelAdapter(Context context, List<Hotel> list, boolean nearBy) {
        this.context = context;
        this.list = list;
        this.nearBy = nearBy;
    }

    public List<Hotel> getmHotelList() {
        return list;
    }

    public void addAll(List<Hotel> hotelList) {
        list.addAll(hotelList);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
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

        ViewHoilder mholder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.nearhotelitem, parent, false);
            mholder = new ViewHoilder(convertView);
            convertView.setTag(mholder);
        } else {
            mholder = (ViewHoilder) convertView.getTag();
        }

        Hotel hotel = list.get(position);

        if (!nearBy) {
            mholder.tvDistance.setVisibility(View.GONE);
        } else {
            double localLongitude = Double.parseDouble(AppContext.getProperty(Config.LONGITUDE));
            double localLatitude = Double.parseDouble(AppContext.getProperty(Config.LATITUDE));
            mholder.tvDistance.setVisibility(View.VISIBLE);
            double longitude = Double.parseDouble(hotel.getLongitude()) / 1000000;
            double latitude = Double.parseDouble(hotel.getLatitude()) / 1000000;
            System.out.println("double " + longitude + " " + latitude);
            mholder.tvDistance.setText(context.getString(R.string.distance_to_you) + AppContext.getDistanceStr(localLongitude, localLatitude, longitude, latitude));
        }
        mholder.tvPrice.setText("￥" + hotel.getMin_price());
//        if(hotel.getHour()==0) {
//        }else {
//            lc.getTvPrice().setText("￥" + (int) hotel.getPrice() + mContext.getString(R.string.at_least) + "/" + hotel.getHour() + mContext.getString(R.string.hour));
//        }
        mholder.tvCaption.setText(hotel.getCaption());
        mholder.tvComments.setText(hotel.getComments() + context.getString(R.string.person_comments));
        if (hotel.is_red_bag()){//我有该酒店的红包
           mholder.ivIsCoupons.setImageResource(R.drawable.pic_mark_coupons);
            mholder.ivIsCoupons.setVisibility(View.VISIBLE);
            if (hotel.isJudge_red_bag()){//酒店有活动
                mholder.ivDiscout.setImageResource(R.drawable.pic_mark_discout);
                mholder.ivDiscout.setVisibility(View.VISIBLE);
            }
        }else {
            mholder.ivIsCoupons.setVisibility(View.GONE);
            if (hotel.isJudge_red_bag()){//酒店有活动
                mholder.ivDiscout.setImageResource(R.drawable.pic_mark_discout);
                mholder.ivDiscout.setVisibility(View.VISIBLE);
            }else {
                mholder.ivDiscout.setVisibility(View.GONE);
            }
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
//								.showImageOnLoading(R.drawable.default_image)
                .showImageOnFail(R.drawable.pic_hotel)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(hotel.getPicture(), mholder.ivPicture, options);


        return convertView;
    }

    class ViewHoilder {
        private TextView tvCaption;
        private TextView tvComments;
        private TextView tvDistance;
        private TextView tvPrice;
        private ImageView ivPicture;
        private ImageView ivIsCoupons,ivDiscout;

        public ViewHoilder(View convertView) {
            this.tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
            this.tvComments = (TextView) convertView.findViewById(R.id.tvComments);
            this.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
            this.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            this.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            this.ivIsCoupons = (ImageView) convertView.findViewById(R.id.ivIsCoupons);
            this.ivDiscout = (ImageView) convertView.findViewById(R.id.ivDiscout);
        }
    }
}
