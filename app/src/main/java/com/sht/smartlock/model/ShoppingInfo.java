package com.sht.smartlock.model;

/**
 * Created by Administrator on 2015/10/13.
 */
public class ShoppingInfo {
    private String hotel_id;
    private String hotel_caption;
    private String create_time;
    private String pay_type;
    private String total;
    private String state;
    private String ID;
    private String room_id;

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public ShoppingInfo() {
    }

    public ShoppingInfo(String create_time, String hotel_caption, String hotel_id, String pay_type, String state, String total) {
        this.create_time = create_time;
        this.hotel_caption = hotel_caption;
        this.hotel_id = hotel_id;
        this.pay_type = pay_type;
        this.state = state;
        this.total = total;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getHotel_caption() {
        return hotel_caption;
    }

    public void setHotel_caption(String hotel_caption) {
        this.hotel_caption = hotel_caption;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
