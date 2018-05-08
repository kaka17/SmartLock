package com.sht.smartlock.ui.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/21.
 */
public class ServicerActionEntity implements Serializable{
    private String action;
    private String action_time;
    private String attendant_name;
    private long comparableTime;

    public long getComparableTime() {
        return comparableTime;
    }

    public void setComparableTime(long comparableTime) {
        this.comparableTime = comparableTime;
    }

    public String getAttendant_name() {
        return attendant_name;
    }

    public void setAttendant_name(String attendant_name) {
        this.attendant_name = attendant_name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction_time() {
        return action_time;
    }

    public void setAction_time(String action_time) {
        this.action_time = action_time;
    }
}
