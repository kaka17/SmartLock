package com.sht.smartlock.model.booking;

/**
 * Created by Administrator on 2015/9/24.
 */
public class HotelRoom {
    private String ID;
    private String caption;
    private String content;
    private double price;
    private int hour;
    private String brief;
    private String facility;
    private String type_image;

    public HotelRoom(String ID, String caption, String content, double price, int hour, String brief, String facility, String type_image) {
        this.ID = ID;
        this.caption = caption;
        this.content = content;
        this.price = price;
        this.hour = hour;
        this.brief = brief;
        this.facility = facility;
        this.type_image = type_image;
    }

    public HotelRoom(String ID, String caption, String content, double price, int hour) {
        this.ID = ID;
        this.caption = caption;
        this.content = content;
        this.price = price;
        this.hour = hour;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getType_image() {
        return type_image;
    }

    public void setType_image(String type_image) {
        this.type_image = type_image;
    }
}
