package com.sht.smartlock.model;

/**
 * Created by Administrator on 2015/10/28.
 */
public class SetFreeInfo {
    private String ID;
    private String room_id;
    private String checkin_time;
    private String hotel_caption;
    private String room_no;
    private String book_id;

    public SetFreeInfo() {
    }

    public SetFreeInfo(String book_id, String checkin_time, String hotel_caption, String ID, String room_id, String room_no) {
        this.book_id = book_id;
        this.checkin_time = checkin_time;
        this.hotel_caption = hotel_caption;
        this.ID = ID;
        this.room_id = room_id;
        this.room_no = room_no;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getCheckin_time() {
        return checkin_time;
    }

    public void setCheckin_time(String checkin_time) {
        this.checkin_time = checkin_time;
    }

    public String getHotel_caption() {
        return hotel_caption;
    }

    public void setHotel_caption(String hotel_caption) {
        this.hotel_caption = hotel_caption;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }
}
