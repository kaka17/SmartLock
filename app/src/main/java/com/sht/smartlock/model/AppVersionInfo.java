package com.sht.smartlock.model;

/**
 * Created by Administrator on 2015/11/12.
 */
public class AppVersionInfo {
    private String app;
    private String change_log;
    private String revision;
    private String os;
    private String minor;
    private String state;
    private String create_time;
    private String ID;
    private String label;
    private String build;
    private String major;

    public AppVersionInfo() {
    }

    public AppVersionInfo(String app, String change_log, String revision, String os, String minor, String state, String create_time, String ID, String label, String build, String major) {
        this.app = app;
        this.change_log = change_log;
        this.revision = revision;
        this.os = os;
        this.minor = minor;
        this.state = state;
        this.create_time = create_time;
        this.ID = ID;
        this.label = label;
        this.build = build;
        this.major = major;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getChange_log() {
        return change_log;
    }

    public void setChange_log(String change_log) {
        this.change_log = change_log;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
