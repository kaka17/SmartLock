package com.sht.smartlock.model;

/**
 * Created by Administrator on 2015/10/9.
 */
public class CommentInfo {
    private String ID;
    private String hotel_id;
    private String caption;
    private String picture;
    private String address;
    private String state;
    private String content;
    private String create_time;
    private String total_com;
    private int is_collection;
    private String introduction;

    public CommentInfo() {
    }

    public CommentInfo(String ID, String hotel_id, String caption, String picture, String address, String state, String content, String create_time, String total_com, int is_collection, String introduction) {
        this.ID = ID;
        this.hotel_id = hotel_id;
        this.caption = caption;
        this.picture = picture;
        this.address = address;
        this.state = state;
        this.content = content;
        this.create_time = create_time;
        this.total_com = total_com;
        this.is_collection = is_collection;
        this.introduction = introduction;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getIs_collection() {
        return is_collection;
    }

    public void setIs_collection(int is_collection) {
        this.is_collection = is_collection;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTotal_com() {
        return total_com;
    }

    public void setTotal_com(String total_com) {
        this.total_com = total_com;
    }
}
