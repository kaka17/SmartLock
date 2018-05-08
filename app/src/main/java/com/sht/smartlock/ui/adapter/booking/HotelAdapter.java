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
import com.sht.smartlock.AppContext;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.model.booking.Hotel;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2015/9/18.
 */
public class HotelAdapter extends BaseAdapter {
    private Context mContext;
    private List<Hotel> mHotelList;
   boolean nearBy;
    public HotelAdapter(Context context, List<Hotel> hotelList,boolean nearBy) {
        mContext = context;
        mHotelList = hotelList;
        this.nearBy=nearBy;
    }

    public List<Hotel> getmHotelList() {
        return mHotelList;
    }

    public Context getmContext() {
        return mContext;
    }

    public void addAll(List<Hotel> hotelList) {
        mHotelList.addAll(hotelList);
        notifyDataSetChanged();
    }

    public void clear() {
        mHotelList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mHotelList.size();
    }

    @Override
    public Object getItem(int position) {
        return mHotelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getmContext()).inflate(R.layout.activity_search_list_item, null);
            convertView.setTag(new ListCell((TextView) convertView.findViewById(R.id.tvCaption),
                    (TextView) convertView.findViewById(R.id.tvComments),
                    (TextView) convertView.findViewById(R.id.tvDistance),
                    (TextView) convertView.findViewById(R.id.tvPrice),
                    (TextView) convertView.findViewById(R.id.tvAddress),
                    (ImageView) convertView.findViewById(R.id.ivPicture),
                    (ImageView) convertView.findViewById(R.id.ivIsCoupons),
                    (ImageView) convertView.findViewById(R.id.ivDiscout)));
        }
        ListCell lc = (ListCell) convertView.getTag();
        Hotel hotel = mHotelList.get(position);

        if (!nearBy) {
            lc.getTvDistance().setVisibility(View.GONE);
        } else {
            double localLongitude = Double.parseDouble(AppContext.getProperty(Config.LONGITUDE));
            double localLatitude = Double.parseDouble(AppContext.getProperty(Config.LATITUDE));
//            if (position == 0 ) {
//                Collections.sort(mHotelList, new Comparator<Hotel>() {
//                    @Override
//                    public int compare(Hotel lhs, Hotel rhs) {
//                        return AppContext.compareDistance(lhs, rhs);
//                    }
//                });
//                hotel = mHotelList.get(0);
//            }
            lc.getTvDistance().setVisibility(View.VISIBLE);
            double longitude =Double.parseDouble(hotel.getLongitude())/1000000;
            double latitude = Double.parseDouble(hotel.getLatitude())/1000000;
            System.out.println("double "+longitude+" "+latitude);
            lc.getTvDistance().setText(AppContext.getDistanceStr(localLongitude, localLatitude, longitude, latitude));
        }
        DecimalFormat dr=new DecimalFormat("#0.00");
            lc.getTvPrice().setText("￥" + dr.format(Double.parseDouble(hotel.getMin_price())) );
//        if(hotel.getHour()==0) {
//        }else {
//            lc.getTvPrice().setText("￥" + (int) hotel.getPrice() + mContext.getString(R.string.at_least) + "/" + hotel.getHour() + mContext.getString(R.string.hour));
//        }
        lc.getTvCaption().setText(hotel.getCaption());
        lc.getTvComments().setText(hotel.getComments() + mContext.getString(R.string.person_comments));
        lc.getTvAddress().setText(hotel.getAddress());
        if (hotel.is_red_bag()){//我有该酒店的红包
            lc.getIvIsCoupons().setImageResource(R.drawable.pic_mark_coupons);
            lc.getIvIsCoupons().setVisibility(View.VISIBLE);
            if (hotel.isJudge_red_bag()){//酒店有活动
                lc.getIvDiscout().setImageResource(R.drawable.pic_mark_discout);
                lc.getIvDiscout().setVisibility(View.VISIBLE);
            }
        }else {
            lc.getIvIsCoupons().setVisibility(View.GONE);
            if (hotel.isJudge_red_bag()){//酒店有活动
                lc.getIvDiscout().setImageResource(R.drawable.pic_mark_discout);
                lc.getIvDiscout().setVisibility(View.VISIBLE);
            }else {
                lc.getIvDiscout().setVisibility(View.GONE);
            }
        }


        DisplayImageOptions options = new DisplayImageOptions.Builder()
//								.showImageOnLoading(R.drawable.default_image)
                .showImageOnFail(R.drawable.pic_hotel)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(hotel.getPicture(), lc.getIvPicture(),options);
        return convertView;
    }

    public class ListCell {
        private TextView tvCaption;
        private TextView tvComments;
        private TextView tvDistance;
        private TextView tvPrice,tvAddress;
        private ImageView ivPicture;
        private ImageView ivIsCoupons,ivDiscout;

        public ListCell(TextView tvCaption, TextView tvComments, TextView tvDistance, TextView tvPrice,TextView tvAddress, ImageView ivPicture, ImageView ivIsCoupons, ImageView ivDiscout) {
            this.tvCaption = tvCaption;
            this.tvComments = tvComments;
            this.tvDistance = tvDistance;
            this.tvPrice = tvPrice;
            this.tvAddress = tvAddress;
            this.ivPicture = ivPicture;
            this.ivIsCoupons = ivIsCoupons;
            this.ivDiscout = ivDiscout;
        }

        public TextView getTvAddress() {
            return tvAddress;
        }

        public void setTvAddress(TextView tvAddress) {
            this.tvAddress = tvAddress;
        }

        public ImageView getIvDiscout() {
            return ivDiscout;
        }

        public void setIvDiscout(ImageView ivDiscout) {
            this.ivDiscout = ivDiscout;
        }

        public ImageView getIvIsCoupons() {
            return ivIsCoupons;
        }

        public void setIvIsCoupons(ImageView ivIsCoupons) {
            this.ivIsCoupons = ivIsCoupons;
        }

        public TextView getTvCaption() {
            return tvCaption;
        }

        public void setTvCaption(TextView tvCaption) {
            this.tvCaption = tvCaption;
        }

        public TextView getTvComments() {
            return tvComments;
        }

        public void setTvComments(TextView tvComments) {
            this.tvComments = tvComments;
        }

        public TextView getTvPrice() {
            return tvPrice;
        }

        public void setTvPrice(TextView tvPrice) {
            this.tvPrice = tvPrice;
        }

        public ImageView getIvPicture() {
            return ivPicture;
        }

        public void setIvPicture(ImageView ivPicture) {
            this.ivPicture = ivPicture;
        }

        public TextView getTvDistance() {
            return tvDistance;
        }

        public void setTvDistance(TextView tvDistance) {
            this.tvDistance = tvDistance;
        }
    }
}
