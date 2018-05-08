package com.sht.smartlock.ui.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/13.
 */
public class LockDetaileInfo implements Serializable{
    private String hotel_facility;//酒店设施
    private String address;
    private String phone_no;
    private String images;
    private String caption;
    private String longitude;
    private String latitude;
    private String intr;
    private String facility_url;//设施对应的图片



    public String getFacility_url() {
        return facility_url;
    }

    public void setFacility_url(String facility_url) {
        this.facility_url = facility_url;
    }

    public String getHotel_facility() {
        return hotel_facility;
    }

    public void setHotel_facility(String hotel_facility) {
        this.hotel_facility = hotel_facility;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public String getIntr() {
        return intr;
    }

    public void setIntr(String intr) {
        this.intr = intr;
    }
}
