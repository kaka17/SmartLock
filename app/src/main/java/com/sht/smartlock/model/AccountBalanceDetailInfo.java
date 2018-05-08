package com.sht.smartlock.model;

/**
 * Created by Administrator on 2016/1/26.
 */
public class AccountBalanceDetailInfo {
     private String record_id;
     private String caption;
     private String balance;
     private String record_balance;
     private String create_time;

    public AccountBalanceDetailInfo(String record_id, String caption, String balance, String record_balance, String create_time) {
        this.record_id = record_id;
        this.caption = caption;
        this.balance = balance;
        this.record_balance = record_balance;
        this.create_time = create_time;
    }

    public AccountBalanceDetailInfo() {
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getRecord_balance() {
        return record_balance;
    }

    public void setRecord_balance(String record_balance) {
        this.record_balance = record_balance;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
