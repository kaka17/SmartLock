package com.sht.smartlock.model.booking;

/**
 * Created by Administrator on 2015/10/14.
 */
public class Bill {
  private  String ID;
    private  String hotel_caption;
    private String caption;
    private  String start_date;
    private   String end_date;
    private   String expect_checkin_time;
    private   String price;
    private   String room_no;
    private String checkin_flag;
    private  String num;

    public Bill(String ID, String hotel_caption, String caption, String start_date, String end_date, String expect_checkin_time, String price, String room_no, String checkin_flag, String num) {
        this.ID = ID;
        this.hotel_caption = hotel_caption;
        this.caption = caption;
        this.start_date = start_date;
        this.end_date = end_date;
        this.expect_checkin_time = expect_checkin_time;
        this.price = price;
        this.room_no = room_no;
        this.checkin_flag = checkin_flag;
        this.num = num;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getHotel_caption() {
        return hotel_caption;
    }

    public void setHotel_caption(String hotel_caption) {
        this.hotel_caption = hotel_caption;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getExpect_checkin_time() {
        return expect_checkin_time;
    }

    public void setExpect_checkin_time(String expect_checkin_time) {
        this.expect_checkin_time = expect_checkin_time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

    public String getCheckin_flag() {
        return checkin_flag;
    }

    public void setCheckin_flag(String checkin_flag) {
        this.checkin_flag = checkin_flag;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
