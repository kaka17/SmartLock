package com.sht.smartlock.model;

/**
 * Created by Administrator on 2015/10/10.
 */
public class OrderingInfo {
    private String ID;
    private String hotel_id;
    private String service_id;
    private String quantity;
    private String price;
    private String discount;
    private String state;
    private String pay_state;
    private String pay_type;
    private String pay_channel;
    private String pay_no;
    private String create_time;
    private String hotel_caption;
    private String total;
//    private String items;
    private String room_id;

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }



    public OrderingInfo() {
    }

    public OrderingInfo(String create_time, String discount, String hotel_caption, String hotel_id, String ID, String items, String pay_channel, String pay_no, String pay_state, String pay_type, String price, String quantity, String service_id, String state, String total) {
        this.create_time = create_time;
        this.discount = discount;
        this.hotel_caption = hotel_caption;
        this.hotel_id = hotel_id;
        this.ID = ID;
        this.pay_channel = pay_channel;
        this.pay_no = pay_no;
        this.pay_state = pay_state;
        this.pay_type = pay_type;
        this.price = price;
        this.quantity = quantity;
        this.service_id = service_id;
        this.state = state;
        this.total = total;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getHotel_caption() {
        return hotel_caption;
    }

    public void setHotel_caption(String hotel_caption) {
        this.hotel_caption = hotel_caption;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
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

    public String getPay_no() {
        return pay_no;
    }

    public void setPay_no(String pay_no) {
        this.pay_no = pay_no;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
