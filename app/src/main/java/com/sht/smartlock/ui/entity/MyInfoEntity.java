package com.sht.smartlock.ui.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/13.
 */
public class MyInfoEntity implements Serializable{
    private String  name;//姓名
    private String id_no ;//身份证号
    private String  phone_no;//电话号码
    private String  id_image;//身份证影印件
    private String  unlock_pwd;//开门密码
    private String  invoice;//	发票抬头

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId_no() {
        return id_no;
    }

    public void setId_no(String id_no) {
        this.id_no = id_no;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getId_image() {
        return id_image;
    }

    public void setId_image(String id_image) {
        this.id_image = id_image;
    }

    public String getUnlock_pwd() {
        return unlock_pwd;
    }

    public void setUnlock_pwd(String unlock_pwd) {
        this.unlock_pwd = unlock_pwd;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }
}
