package com.sht.smartlock.model;

/**
 * Created by Administrator on 2015/9/24.
 */
public class UserLoginInfo {
    private String name;
    private String nick_name;
    private String ID;
    private String sessionid;
    private static  String user_id ="";
    private static final String loginID ="";
    private boolean set_unlock_pwd=false;
    private String emid;
    private String em_passwd;
    private String voip_account;
    private String voip_pwd;
    private String user_name;
    private String phone_no;

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
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

    public String getVoip_pwd() {
        return voip_pwd;
    }

    public void setVoip_pwd(String voip_pwd) {
        this.voip_pwd = voip_pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEm_passwd() {
        return em_passwd;
    }

    public void setEm_passwd(String em_passwd) {
        this.em_passwd = em_passwd;
    }

    public String getEmid() {
        return emid;
    }

    public void setEmid(String emid) {
        this.emid = emid;
    }

    public boolean isSet_unlock_pwd() {
        return set_unlock_pwd;
    }


    public void setSet_unlock_pwd(boolean set_unlock_pwd) {
        this.set_unlock_pwd = set_unlock_pwd;
    }

    public static void setUser_id(String user_id) {
        UserLoginInfo.user_id = user_id;
    }

    public static String getUser_id() {
        return user_id;
    }

    public static String getLoginID() {
        return loginID;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }



    public UserLoginInfo(String name, String nick_name, String ID, String sessionid, boolean set_unlock_pwd, String emid, String em_passwd) {
        this.name = name;
        this.nick_name = nick_name;
        this.ID = ID;
        this.sessionid = sessionid;
        this.set_unlock_pwd = set_unlock_pwd;
        this.emid = emid;
        this.em_passwd = em_passwd;
    }

    public UserLoginInfo() {
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
