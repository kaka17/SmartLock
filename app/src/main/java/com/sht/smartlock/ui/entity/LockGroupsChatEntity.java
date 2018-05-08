package com.sht.smartlock.ui.entity;

/**
 * Created by Administrator on 2015/9/15.
 */
public class LockGroupsChatEntity {

//    private String groupId;
//    private String lockName;
//    private String num;

    //解析数据
    private String ID;
    private String caption;
    private String address;
    private String picture;
    private String on_num;
    private String emid;

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
//        this.lockName=caption;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getOn_num() {
        return on_num;
    }

    public void setOn_num(String on_num) {
        this.on_num = on_num;
    }

    public String getEmid() {
        return emid;
    }

    public void setEmid(String emid) {
        this.emid = emid;
//        this.groupId=emid;
    }

//    public String getNum() {
//        return num;
//    }
//
//    public void setNum(String num) {
//        this.num = num;
//    }
//
//    public String getGroupId() {
//        return groupId;
//    }
//
//    public void setGroupId(String groupId) {
//        this.groupId = groupId;
//    }
//
//    public String getLockName() {
//        return lockName;
//    }
//
//    public void setLockName(String lockName) {
//        this.lockName = lockName;
//    }


    @Override
    public String toString() {
        return "LockGroupsChatEntity{" +
                "ID='" + ID + '\'' +
                ", caption='" + caption + '\'' +
                ", address='" + address + '\'' +
                ", picture='" + picture + '\'' +
                ", on_num='" + on_num + '\'' +
                ", emid='" + emid + '\'' +
                '}';
    }
}
