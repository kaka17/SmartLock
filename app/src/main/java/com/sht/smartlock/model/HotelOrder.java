package com.sht.smartlock.model;

/**
 * Created by Administrator on 2015/10/8.
 */
public class HotelOrder {
    private String ID;
    private String hotel_caption;
    private String caption;
    private String start_date;
    private String end_date;
    private String price;
    private String discount;
    private String state;
    private String pay_state;
    private String pay_type;
    private String pay_channel;
    private String checkout_time;
    private String special_id;
    private String expect_checkin_time;
    private String checkin_name;
    private String checkin_phone;
    private String personally;
    private String checkin_time;
    private String term_type;
    private String hour_term;

    public HotelOrder(String ID, String hotel_caption, String caption, String start_date, String end_date, String price, String discount, String state, String pay_state, String pay_type, String pay_channel, String checkout_time, String special_id, String expect_checkin_time, String checkin_name, String checkin_phone, String personally, String checkin_time, String term_type, String hour_term) {
        this.ID = ID;
        this.hotel_caption = hotel_caption;
        this.caption = caption;
        this.start_date = start_date;
        this.end_date = end_date;
        this.price = price;
        this.discount = discount;
        this.state = state;
        this.pay_state = pay_state;
        this.pay_type = pay_type;
        this.pay_channel = pay_channel;
        this.checkout_time = checkout_time;
        this.special_id = special_id;
        this.expect_checkin_time = expect_checkin_time;
        this.checkin_name = checkin_name;
        this.checkin_phone = checkin_phone;
        this.personally = personally;
        this.checkin_time = checkin_time;
        this.term_type = term_type;
        this.hour_term = hour_term;
    }

    public String getTerm_type() {
        return term_type;
    }

    public void setTerm_type(String term_type) {
        this.term_type = term_type;
    }

    public String getHour_term() {
        return hour_term;
    }

    public void setHour_term(String hour_term) {
        this.hour_term = hour_term;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCheckin_name() {
        return checkin_name;
    }

    public void setCheckin_name(String checkin_name) {
        this.checkin_name = checkin_name;
    }

    public String getCheckin_phone() {
        return checkin_phone;
    }

    public void setCheckin_phone(String checkin_phone) {
        this.checkin_phone = checkin_phone;
    }

    public String getCheckout_time() {
        return checkout_time;
    }

    public String getCheckin_time() {
        return checkin_time;
    }

    public void setCheckin_time(String checkin_time) {
        this.checkin_time = checkin_time;
    }

    public void setCheckout_time(String checkout_time) {
        this.checkout_time = checkout_time;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
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

    public String getPay_channel() {
        return pay_channel;
    }

    public void setPay_channel(String pay_channel) {
        this.pay_channel = pay_channel;
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

    public String getPersonally() {
        return personally;
    }

    public void setPersonally(String personally) {
        this.personally = personally;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSpecial_id() {
        return special_id;
    }

    public void setSpecial_id(String special_id) {
        this.special_id = special_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
