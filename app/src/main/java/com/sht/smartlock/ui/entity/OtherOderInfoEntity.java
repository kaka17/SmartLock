package com.sht.smartlock.ui.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/5/11.
 */
public class OtherOderInfoEntity {
    private String ID;
    private String caption;
    private double price;
    private String discount;
    private int quantity;
    private String code;

    public void pareJson(JSONObject object){
        try {
            this.ID=object.getString("ID");
            this.caption=object.getString("caption");
            this.price=object.getDouble("price");
            this.discount=object.getString("discount");
            this.quantity=object.getInt("quantity");
            this.code=object.getString("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
