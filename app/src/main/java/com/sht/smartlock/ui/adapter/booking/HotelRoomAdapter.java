package com.sht.smartlock.ui.adapter.booking;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sht.smartlock.Config;
import com.sht.smartlock.R;
import com.sht.smartlock.model.booking.HotelRoom;
import com.sht.smartlock.ui.activity.booking.BookingBillActivity;
import com.sht.smartlock.ui.activity.booking.RoomDetailPopWindow;
import com.sht.smartlock.ui.activity.hourroom.BookingHourRoomActivity;

import java.util.List;

/**
 * Created by Administrator on 2015/9/24.
 */
public class HotelRoomAdapter extends BaseAdapter {
    List<HotelRoom> mListHotelRoom;
    BookingHourRoomActivity mContext;
    public HotelRoomAdapter(List<HotelRoom> mListHotelRoom, BookingHourRoomActivity mContext) {
        this.mListHotelRoom = mListHotelRoom;
        this.mContext = mContext;
    }
    public List<HotelRoom> getmListHotelRoom() {
        return mListHotelRoom;
    }

    public Context getmContext() {
        return mContext;
    }

    public void addAll(List<HotelRoom> mListHotelRoom) {
        this.mListHotelRoom.addAll(mListHotelRoom);
        notifyDataSetChanged();
    }

    public void clear() {
        mListHotelRoom.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListHotelRoom.size();
    }

    @Override
    public Object getItem(int position) {
        return mListHotelRoom.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_booking_room_item, null);
            convertView.setTag(new ListCell((TextView) convertView.findViewById(R.id.tvRoomCaption),
                    (TextView) convertView.findViewById(R.id.tvPrice),
                    (ImageView) convertView.findViewById(R.id.tvOrderRoom),
                    (TextView) convertView.findViewById(R.id.tvBrief),
                    (TextView) convertView.findViewById(R.id.tvFacility),
                    (ImageView) convertView.findViewById(R.id.ivPic),
                    (TextView) convertView.findViewById(R.id.tvHour)));
        }
        ListCell lc = (ListCell) convertView.getTag();
        final HotelRoom mHotelRoom = mListHotelRoom.get(position);
        lc.getTvRoomCaption().setText(mHotelRoom.getCaption());
//        java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#0.00");
//        lc.getTvPrice().setText(df.format(mHotelRoom.getPrice()) + "");
        lc.getTvPrice().setText(String.valueOf((int)mHotelRoom.getPrice()));
        lc.getTvBrief().setText(mHotelRoom.getBrief());
        lc.getTvFacility().setText(mHotelRoom.getFacility());
        ImageLoader.getInstance().displayImage(mHotelRoom.getType_image(), lc.getIvImage());
       // lc.getIvImage().setImageResource(mHotelRoom.getType_image());
        if(mHotelRoom.getHour()!=0){
            lc.getTvHour().setVisibility(View.VISIBLE);
            lc.getTvHour().setText("/" + mHotelRoom.getHour() +"h");
        }else{
            lc.getTvHour().setVisibility(View.GONE);
        }
        lc.getTvOrderRoom().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, BookingBillActivity.class);
                i.putExtra(Config.KEY_HOTEL_ID, mContext.getStrHotelId());
                i.putExtra(Config.KEY_HOTEL_CAPTION, mContext.getStrHotelCaption());
                i.putExtra(Config.KEY_HOTEL_ROOMID, mHotelRoom.getID());
                i.putExtra(Config.KEY_HOTEL_ROOMCAPTION, mHotelRoom.getCaption());
                i.putExtra(Config.KEY_HOTEL_ROOMPRICE, mHotelRoom.getPrice());
                if(mHotelRoom.getHour()!=0){
                    i.putExtra(Config.KEY_SHOW_ROOM_MODE,Config.ROOM_MODE_HOUR);
                    i.putExtra(Config.KEY_HOUR_ROOM_SPAN_LIMIT,mHotelRoom.getHour());
                }else {
                    i.putExtra(Config.KEY_SHOW_ROOM_MODE,Config.ROOM_MODE_DAY);
                }
                mContext.startActivity(i);
            }
        });

        return convertView;
    }

    public class ListCell {
        private TextView tvRoomCaption;
        private TextView tvPrice;
        private ImageView tvOrderRoom;
        private TextView tvBrief;
        private TextView tvFacility;
        private ImageView ivImage;

        public TextView getTvHour() {
            return tvHour;
        }

        public void setTvHour(TextView tvHour) {
            this.tvHour = tvHour;
        }

        private TextView tvHour;

        public ListCell(TextView tvRoomCaption, TextView tvPrice, ImageView tvOrderRoom, TextView tvBrief, TextView tvFacility, ImageView ivImage, TextView ivHour) {
            this.tvRoomCaption = tvRoomCaption;
            this.tvPrice = tvPrice;

            this.tvOrderRoom = tvOrderRoom;
            this.tvBrief = tvBrief;
            this.tvFacility = tvFacility;
            this.ivImage = ivImage;
            this.tvHour = ivHour;
        }

        public TextView getTvBrief() {
            return tvBrief;
        }

        public void setTvBrief(TextView tvBrief) {
            this.tvBrief = tvBrief;
        }

        public TextView getTvFacility() {
            return tvFacility;
        }

        public void setTvFacility(TextView tvFacility) {
            this.tvFacility = tvFacility;
        }

        public ImageView getIvImage() {
            return ivImage;
        }

        public void setIvImage(ImageView ivImage) {
            this.ivImage = ivImage;
        }


        public TextView getTvRoomCaption() {
            return tvRoomCaption;
        }

        public void setTvRoomCaption(TextView tvRoomCaption) {
            this.tvRoomCaption = tvRoomCaption;
        }

        public TextView getTvPrice() {
            return tvPrice;
        }

        public void setTvPrice(TextView tvPrice) {
            this.tvPrice = tvPrice;
        }



        public ImageView getTvOrderRoom() {
            return tvOrderRoom;
        }

        public void setTvOrderRoom(ImageView tvOrderRoom) {
            this.tvOrderRoom = tvOrderRoom;
        }
    }
}
