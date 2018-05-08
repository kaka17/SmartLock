package com.sht.smartlock.model;

/**
 * Created by Administrator on 2016/8/22.
 */
public class BillInfo {
    private String room_no;
    private String caption;
    private String time;
    private String booking_id;
    private String total;
    private String user_service_id;

    public BillInfo() {
    }

    public BillInfo(String room_no, String caption, String time, String booking_id, String total, String user_service_id) {
        this.room_no = room_no;
        this.caption = caption;
        this.time = time;
        this.booking_id = booking_id;
        this.total = total;
        this.user_service_id = user_service_id;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUser_service_id() {
        return user_service_id;
    }

    public void setUser_service_id(String user_service_id) {
        this.user_service_id = user_service_id;
    }
}
