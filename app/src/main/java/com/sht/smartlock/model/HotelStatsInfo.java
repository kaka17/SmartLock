package com.sht.smartlock.model;

/**
 * Created by Administrator on 2015/10/17.
 */
public class HotelStatsInfo {
    private String ID;
    private String hotel_id;
    private String hotel_caption;
    private String caption;
    private String start_date;
    private String end_date;
    private String expect_checkin_time;
    private Double order_price;
    private String price;
    private String room_no;
    private String checkin_flag;
    private String num;
    private String longitude;
    private String latitude;
    private String address;
    private String name;
    private String state;
    private String checkin_name;
    private String term_type;
    private String pay_type;
    private String pay_state;

    public HotelStatsInfo(String ID, String hotel_id, String hotel_caption, String caption, String start_date, String end_date, String expect_checkin_time, Double order_price, String price, String room_no, String checkin_flag, String num, String longitude, String latitude, String address, String name, String state, String checkin_name, String term_type, String pay_type, String pay_state) {
        this.ID = ID;
        this.hotel_id = hotel_id;
        this.hotel_caption = hotel_caption;
        this.caption = caption;
        this.start_date = start_date;
        this.end_date = end_date;
        this.expect_checkin_time = expect_checkin_time;
        this.order_price = order_price;
        this.price = price;
        this.room_no = room_no;
        this.checkin_flag = checkin_flag;
        this.num = num;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.name = name;
        this.state = state;
        this.checkin_name = checkin_name;
        this.term_type = term_type;
        this.pay_type = pay_type;
        this.pay_state = pay_state;
    }

    public Double getOrder_price() {
        return order_price;
    }

    public void setOrder_price(Double order_price) {
        this.order_price = order_price;
    }

    public String getPay_state() {
        return pay_state;
    }

    public void setPay_state(String pay_state) {
        this.pay_state = pay_state;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getTerm_type() {
        return term_type;
    }

    public void setTerm_type(String term_type) {
        this.term_type = term_type;
    }

    public String getCheckin_name() {
        return checkin_name;
    }

    public void setCheckin_name(String checkin_name) {
        this.checkin_name = checkin_name;
    }

    public HotelStatsInfo() {
    }



    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCheckin_flag() {
        return checkin_flag;
    }

    public void setCheckin_flag(String checkin_flag) {
        this.checkin_flag = checkin_flag;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }



    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
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

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    @Override
    public String toString() {
        return "HotelStatsInfo{" +
                "ID='" + ID + '\'' +
                ", hotel_caption='" + hotel_caption + '\'' +
                ", caption='" + caption + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", expect_checkin_time='" + expect_checkin_time + '\'' +
                ", price='" + price + '\'' +
                ", room_no='" + room_no + '\'' +
                ", checkin_flag='" + checkin_flag + '\'' +
                ", num='" + num + '\'' +
                ", longtitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", pay_type='" + pay_type + '\'' +
                ", pay_state='" + pay_state + '\'' +
                '}';
    }
}
