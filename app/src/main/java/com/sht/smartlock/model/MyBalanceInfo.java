package com.sht.smartlock.model;

/**
 * Created by Administrator on 2016/1/22.
 */
public class MyBalanceInfo {
    private String recharge_account_user_id;
    private String recharge_account_id;
    private String hotel_caption;
    private String balance;
    private String give_flag;
    private String chain_flag;
    private String comment;
    private String items;


    public MyBalanceInfo(String recharge_account_user_id, String recharge_account_id, String hotel_caption, String balance, String give_flag, String chain_flag, String comment, String items) {
        this.recharge_account_user_id = recharge_account_user_id;
        this.recharge_account_id = recharge_account_id;
        this.hotel_caption = hotel_caption;
        this.balance = balance;
        this.give_flag = give_flag;
        this.chain_flag = chain_flag;
        this.comment = comment;
        this.items = items;
    }

    public MyBalanceInfo() {
    }

    public String getRecharge_account_user_id() {
        return recharge_account_user_id;
    }

    public void setRecharge_account_user_id(String recharge_account_user_id) {
        this.recharge_account_user_id = recharge_account_user_id;
    }

    public String getRecharge_account_id() {
        return recharge_account_id;
    }

    public void setRecharge_account_id(String recharge_account_id) {
        this.recharge_account_id = recharge_account_id;
    }

    public String getHotel_caption() {
        return hotel_caption;
    }

    public void setHotel_caption(String hotel_caption) {
        this.hotel_caption = hotel_caption;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getGive_flag() {
        return give_flag;
    }

    public void setGive_flag(String give_flag) {
        this.give_flag = give_flag;
    }

    public String getChain_flag() {
        return chain_flag;
    }

    public void setChain_flag(String chain_flag) {
        this.chain_flag = chain_flag;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

}
