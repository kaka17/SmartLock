package com.sht.smartlock.ui.entity;

/**
 * Created by Administrator on 2016/11/2.
 */
public class ServierDetailEntity {
    private String total;
    private String state;
    private String create_time;
    private String caption;
    private String name;
    private String phone_no;
    private String content;
    private String pay_channel;
    private String service_pay_state;
    private String item;
    private String room_no;
    private String service_charge_caption;
    private String service_charge;

    public String getService_charge_caption() {
        return service_charge_caption;
    }

    public void setService_charge_caption(String service_charge_caption) {
        this.service_charge_caption = service_charge_caption;
    }

    public String getService_charge() {
        return service_charge;
    }

    public void setService_charge(String service_charge) {
        this.service_charge = service_charge;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPay_channel() {
        return pay_channel;
    }

    public void setPay_channel(String pay_channel) {
        this.pay_channel = pay_channel;
    }

    public String getService_pay_state() {
        return service_pay_state;
    }

    public void setService_pay_state(String service_pay_state) {
        this.service_pay_state = service_pay_state;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
