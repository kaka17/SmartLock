package com.sht.smartlock.ui.entity;

/**
 * Created by Administrator on 2016/11/28.
 */
public class CouponsEntity {
    private String caption;
    private String hotel_name;
    private String room_type_name;
    private String start_time;
    private String end_time;
    private String type;
    private String yh_price;
    private String dj_price;
    private float discount;
    private String comment;
    private String used_time;
    private String time;

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public void setHotel_name(String hotel_name) {
        this.hotel_name = hotel_name;
    }

    public String getRoom_type_name() {
        return room_type_name;
    }

    public void setRoom_type_name(String room_type_name) {
        this.room_type_name = room_type_name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYh_price() {
        return yh_price;
    }

    public void setYh_price(String yh_price) {
        this.yh_price = yh_price;
    }

    public String getDj_price() {
        return dj_price;
    }

    public void setDj_price(String dj_price) {
        this.dj_price = dj_price;
    }



    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsed_time() {
        return used_time;
    }

    public void setUsed_time(String used_time) {
        this.used_time = used_time;
    }
}
