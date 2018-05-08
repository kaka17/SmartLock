package com.sht.smartlock.model;

/**
 * Created by Administrator on 2016/8/23.
 */
public class BillingDetailsInfo {
    private String room_debt;
    private String cater_debt;
    private String shopping_debt;
    private String quantity;
    private String price;
    private String caption;
    private String create_time;
    private String other_debt;
    private String unit_room_debt;
    private String term_type;

    public String getTerm_type() {
        return term_type;
    }

    public void setTerm_type(String term_type) {
        this.term_type = term_type;
    }

    public String getUnit_room_debt() {
        return unit_room_debt;
    }

    public void setUnit_room_debt(String unit_room_debt) {
        this.unit_room_debt = unit_room_debt;
    }

    public BillingDetailsInfo() {
    }

    public BillingDetailsInfo(String room_debt, String cater_debt, String shopping_debt, String quantity, String price, String caption, String create_time, String other_debt) {
        this.room_debt = room_debt;
        this.cater_debt = cater_debt;
        this.shopping_debt = shopping_debt;
        this.quantity = quantity;
        this.price = price;
        this.caption = caption;
        this.create_time = create_time;
        this.other_debt = other_debt;
    }

    public String getOther_debt() {
        return other_debt;
    }

    public void setOther_debt(String other_debt) {
        this.other_debt = other_debt;
    }

    public String getRoom_debt() {
        return room_debt;
    }

    public void setRoom_debt(String room_debt) {
        this.room_debt = room_debt;
    }

    public String getCater_debt() {
        return cater_debt;
    }

    public void setCater_debt(String cater_debt) {
        this.cater_debt = cater_debt;
    }

    public String getShopping_debt() {
        return shopping_debt;
    }

    public void setShopping_debt(String shopping_debt) {
        this.shopping_debt = shopping_debt;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
