package com.sht.smartlock.ui.entity;

/**
 * Created by Administrator on 2015/9/29.
 */
public class MyLockEntity {
    private String expect_checkin_time;
    private String end_date;
    private String price;
    private String caption;
    private String start_date;
    private String ID; //酒店id
    private String room_id;
    private String hotel_caption;
    private String room_no;

    private String checkin_time;
    private String book_id;//订单号
    private String longitude;//经度
    private String latitude;//纬度

    //
    private String other;
    private String replace;
    private String clear;
    private String awaken;
    private String dinner;
    private String shopping;
    private String service_start_time;//开始服务时间
    private String service_duration;//持续服务时间

    private int alert;//下次开门是否需要弹红包分享
    private String bonus_id;//活动红包id
    private int num;//房间第几次人
    private String lock;//多房数组

    private String lift_config;
    private int breakfast_remain;//早餐券数量
    private String breakfast;//早餐券描述

    public int getBreakfast_remain() {
        return breakfast_remain;
    }

    public void setBreakfast_remain(int breakfast_remain) {
        this.breakfast_remain = breakfast_remain;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getLift_config() {
        return lift_config;
    }

    public void setLift_config(String lift_config) {
        this.lift_config = lift_config;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public int getNum() {
        return num;
    }

    public int getAlert() {
        return alert;
    }

    public void setAlert(int alert) {
        this.alert = alert;
    }

    public String getBonus_id() {
        return bonus_id;
    }

    public void setBonus_id(String bonus_id) {
        this.bonus_id = bonus_id;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getService_start_time() {
        return service_start_time;
    }

    public void setService_start_time(String service_start_time) {
        this.service_start_time = service_start_time;
    }

    public String getService_duration() {
        return service_duration;
    }

    public void setService_duration(String service_duration) {
        this.service_duration = service_duration;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getReplace() {
        return replace;
    }

    public void setReplace(String replace) {
        this.replace = replace;
    }

    public String getClear() {
        return clear;
    }

    public void setClear(String clear) {
        this.clear = clear;
    }

    public String getAwaken() {
        return awaken;
    }

    public void setAwaken(String awaken) {
        this.awaken = awaken;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    public String getShopping() {
        return shopping;
    }

    public void setShopping(String shopping) {
        this.shopping = shopping;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCheckin_time() {
        return checkin_time;
    }

    public void setCheckin_time(String checkin_time) {
        this.checkin_time = checkin_time;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRoom_no() {
        return room_no;

    }


    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

    public String getExpect_checkin_time() {
        return expect_checkin_time;
    }

    public void setExpect_checkin_time(String expect_checkin_time) {
        this.expect_checkin_time = expect_checkin_time;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }



    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getHotel_caption() {
        return hotel_caption+"--"+room_no;
    }

    public void setHotel_caption(String hotel_caption) {
        this.hotel_caption = hotel_caption;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    @Override
    public String toString() {
        return "MyLockEntity{" +
                "expect_checkin_time='" + expect_checkin_time + '\'' +
                ", end_date='" + end_date + '\'' +
                ", price='" + price + '\'' +
                ", caption='" + caption + '\'' +
                ", start_date='" + start_date + '\'' +
                ", ID='" + ID + '\'' +
                ", room_id='" + room_id + '\'' +
                ", hotel_caption='" + hotel_caption + '\'' +
                ", room_no='" + room_no + '\'' +
                ", checkin_time='" + checkin_time + '\'' +
                ", book_id='" + book_id + '\'' +
                '}';
    }
}
