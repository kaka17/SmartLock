package com.sht.smartlock.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/13.
 */
public class UserInfo implements Serializable {
    private String name;
    private String id_no;
    private String phone_no;
    private String id_image;
    private String unlock_pwd;
    private String invoice;

    public UserInfo() {
    }

    public UserInfo(String id_image, String id_no, String invoice, String name, String phone_no, String unlock_pwd) {
        this.id_image = id_image;
        this.id_no = id_no;
        this.invoice = invoice;
        this.name = name;
        this.phone_no = phone_no;
        this.unlock_pwd = unlock_pwd;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getId_image() {
        return id_image;
    }

    public void setId_image(String id_image) {
        this.id_image = id_image;
    }

    public String getId_no() {
        return id_no;
    }

    public void setId_no(String id_no) {
        this.id_no = id_no;
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

    public String getUnlock_pwd() {
        return unlock_pwd;
    }

    public void setUnlock_pwd(String unlock_pwd) {
        this.unlock_pwd = unlock_pwd;
    }
}
