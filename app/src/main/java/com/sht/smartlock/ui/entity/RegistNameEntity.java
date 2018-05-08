package com.sht.smartlock.ui.entity;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/14.
 */
public class RegistNameEntity {
    private String emid;
    private String passwd;
    private String user_name;
    private String voip_account;
    private String em_passwd;
    private String phone_no;
    private String name;
    private String ID;
    private String sessionid;
    private String voip_pwd;
    private String set_unlock_pwd;
    private String msg;
    private int code;

    private String result;

    public void paseJson(String str){
        Log.e("json","----->"+str);
        try {
            JSONObject jsonObject=new JSONObject(str);
            this.emid=jsonObject.getString("emid");
            this.passwd=jsonObject.getString("passwd");
            this.user_name=jsonObject.getString("user_name");
            this.voip_account=jsonObject.getString("voip_account");
            this.em_passwd=jsonObject.getString("em_passwd");
            this.phone_no=jsonObject.getString("phone_no");
            this.name=jsonObject.getString("name");
            this.ID=jsonObject.getString("ID");
            this.sessionid=jsonObject.getString("sessionid");
            this.voip_pwd=jsonObject.getString("voip_pwd");
            this.set_unlock_pwd=jsonObject.getString("set_unlock_pwd");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        return result;
    }


    public void setResult(String result) {
        this.result = result;
    }

    public String getEmid() {
        return emid;
    }

    public void setEmid(String emid) {
        this.emid = emid;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getVoip_account() {
        return voip_account;
    }

    public void setVoip_account(String voip_account) {
        this.voip_account = voip_account;
    }

    public String getEm_passwd() {
        return em_passwd;
    }

    public void setEm_passwd(String em_passwd) {
        this.em_passwd = em_passwd;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getVoip_pwd() {
        return voip_pwd;
    }

    public void setVoip_pwd(String voip_pwd) {
        this.voip_pwd = voip_pwd;
    }

    public String getSet_unlock_pwd() {
        return set_unlock_pwd;
    }

    public void setSet_unlock_pwd(String set_unlock_pwd) {
        this.set_unlock_pwd = set_unlock_pwd;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;

    }
}
