package com.sht.smartlock.model.booking;

/**
 * Created by Administrator on 2015/10/21.
 */
public class HourBill {
    private  String ID;
    private  String hotel_caption;
    private String caption;
    private  String hour_start;
    private   String hour_end;
    private   String price;
    private   String room_no;
    private String checkin_flag;
    private  String num;

    public HourBill(String ID, String hotel_caption, String caption, String hour_start, String hour_end, String price, String room_no, String checkin_flag, String num) {
        this.ID = ID;
        this.hotel_caption = hotel_caption;
        this.caption = caption;
        this.hour_start = hour_start;
        this.hour_end = hour_end;
        this.price = price;
        this.room_no = room_no;
        this.checkin_flag = checkin_flag;
        this.num = num;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getHotel_caption() {
        return hotel_caption;
    }

    public void setHotel_caption(String hotel_caption) {
        this.hotel_caption = hotel_caption;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getHour_start() {
        return hour_start;
    }

    public void setHour_start(String hour_start) {
        this.hour_start = hour_start;
    }

    public String getHour_end() {
        return hour_end;
    }

    public void setHour_end(String hour_end) {
        this.hour_end = hour_end;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

    public String getCheckin_flag() {
        return checkin_flag;
    }

    public void setCheckin_flag(String checkin_flag) {
        this.checkin_flag = checkin_flag;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
