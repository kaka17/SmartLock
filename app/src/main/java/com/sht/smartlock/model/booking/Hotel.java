package com.sht.smartlock.model.booking;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/9/18.
 */
public class Hotel implements Serializable {
    private String caption;
    private int comments;
    private float price;
    private  String ID;
    private String picture;
    private String introduction;
    private String longitude;
    private String latitude;
    private int hour;
    private int is_collection;

    // 新 的 附近钟点房
    private String hour_term;//钟点房时长分钟数
//    private String remain;//剩余房间数
    private String min_price;//最低房间价格
    private String address;//酒店地址
    private boolean judge_red_bag;//酒店是否有红包
    private boolean is_red_bag;//我是否有改酒店的红包

    public Hotel(String caption, int comments, float price, String ID, String picture, String introduction, String longitude, String latitude, int hour, int is_collection,boolean is_red_bag,boolean judge_red_bag) {
        this.caption = caption;
        this.comments = comments;
        this.price = price;
        this.ID = ID;
        this.picture = picture;
        this.introduction = introduction;
        this.longitude = longitude;
        this.latitude = latitude;
        this.hour = hour;
        this.is_collection = is_collection;
        this.judge_red_bag = judge_red_bag;
        this.is_red_bag = is_red_bag;
    }

    public boolean isJudge_red_bag() {
        return judge_red_bag;
    }

    public void setJudge_red_bag(boolean judge_red_bag) {
        this.judge_red_bag = judge_red_bag;
    }

    public boolean is_red_bag() {
        return is_red_bag;
    }

    public void setIs_red_bag(boolean is_red_bag) {
        this.is_red_bag = is_red_bag;
    }

    public String getHour_term() {
        return hour_term;
    }

    public void setHour_term(String hour_term) {
        this.hour_term = hour_term;
    }

//    public String getRemain() {
//        return remain;
//    }
//
//    public void setRemain(String remain) {
//        this.remain = remain;
//    }

    public String getMin_price() {
        return min_price;
    }

    public void setMin_price(String min_price) {
        this.min_price = min_price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getIs_collection() {
        return is_collection;
    }

    public void setIs_collection(int is_collection) {
        this.is_collection = is_collection;
    }
}
