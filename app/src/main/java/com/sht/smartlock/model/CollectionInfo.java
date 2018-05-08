package com.sht.smartlock.model;

/**
 * Created by Administrator on 2015/10/9.
 */
public class CollectionInfo {
    private String ID;
    private String hotel_id;
    private String caption;
    private String picture;
    private String address;
    private String total_com;
    private String introduction;
    private int is_collection;
    public CollectionInfo() {
    }

    public CollectionInfo(String address, String caption, String hotel_id, String ID, String introduction, int is_collection, String picture, String total_com) {
        this.address = address;
        this.caption = caption;
        this.hotel_id = hotel_id;
        this.ID = ID;
        this.introduction = introduction;
        this.is_collection = is_collection;
        this.picture = picture;
        this.total_com = total_com;
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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTotal_com() {
        return total_com;
    }

    public void setTotal_com(String total_com) {
        this.total_com = total_com;
    }
}
