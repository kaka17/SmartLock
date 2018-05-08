package com.sht.smartlock.ui.entity;

/**
 * Created by Administrator on 2015/12/25.
 */
public class OpenDoorEntity {
    private String code;
    private String msg;
    private String return_value;
    private String power;
    private int num;//第几位用户开锁

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReturn_value() {
        return return_value;
    }

    public void setReturn_value(String return_value) {
        this.return_value = return_value;
    }
}
