package com.sht.smartlock.ui.entity;

/**
 * Created by Administrator on 2016/9/27.
 */
public class UserInfoEntity {
    private String ID;
    private String user_name;
    private String passwd;
    private String sessionid;
    private String set_unlock_pwd;
    private String emid;
    private String em_passwd;
    private String voip_account;
    private String voip_pwd;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getSet_unlock_pwd() {
        return set_unlock_pwd;
    }

    public void setSet_unlock_pwd(String set_unlock_pwd) {
        this.set_unlock_pwd = set_unlock_pwd;
    }

    public String getEmid() {
        return emid;
    }

    public void setEmid(String emid) {
        this.emid = emid;
    }

    public String getEm_passwd() {
        return em_passwd;
    }

    public void setEm_passwd(String em_passwd) {
        this.em_passwd = em_passwd;
    }

    public String getVoip_account() {
        return voip_account;
    }

    public void setVoip_account(String voip_account) {
        this.voip_account = voip_account;
    }

    public String getVoip_pwd() {
        return voip_pwd;
    }

    public void setVoip_pwd(String voip_pwd) {
        this.voip_pwd = voip_pwd;
    }
}
