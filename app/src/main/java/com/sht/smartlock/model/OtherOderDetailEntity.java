package com.sht.smartlock.model;

/**
 * Created by Administrator on 2017/5/17.
 */
public class OtherOderDetailEntity {
    private String ID;
    private float total;
    private float discount;
    private String create_time;
    private String room_no;
    private float service_charge;
    private String confirm;
    private String confirm_config;
    private float total_price;
    private String item;
    private float no_discount_total;

    public float getNo_discount_total() {
        return no_discount_total;
    }

    public void setNo_discount_total(float no_discount_total) {
        this.no_discount_total = no_discount_total;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getService_charge() {
        return service_charge;
    }

    public void setService_charge(float service_charge) {
        this.service_charge = service_charge;
    }

    public float getTotal_price() {
        return total_price;
    }

    public void setTotal_price(float total_price) {
        this.total_price = total_price;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }



    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getConfirm_config() {
        return confirm_config;
    }

    public void setConfirm_config(String confirm_config) {
        this.confirm_config = confirm_config;
    }


    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
