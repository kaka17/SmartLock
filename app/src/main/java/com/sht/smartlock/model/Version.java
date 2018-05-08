package com.sht.smartlock.model;

import android.util.Log;

/**
 * Created by Jerry on 2015/6/28 0028.
 */
public class Version extends BaseModel {
    private static final int REQUIRED_FORCED = 1;

    private boolean isLasted;
    private int required;

    private String major;
    private String minor;
    private String revision;
    private String build;
    private String label;
    private String link;
    private String change_log;

    public String getChange_log() {
        return change_log;
    }

    public void setChange_log(String change_log) {
        this.change_log = change_log;
    }

    public boolean isForcedUpdate(){
        return required == REQUIRED_FORCED;
    }

    public boolean isUpadte(String currentVersionNum){
        String[] split = currentVersionNum.split("\\.");
        Log.e("VERS","-------->currentVersionNum"+currentVersionNum+"---=="+major+"."+minor+"."+revision+"split"+split.length);
        if (major.equals("null")||minor.equals("null")||revision.equals("null")||major==null){
            return false;
        }
        Log.e("TAAF","--"+Integer.parseInt(split[0]+split[1]+split[2])+"-------"+Integer.parseInt(major+minor+revision));
        if (Integer.parseInt(split[0]+split[1]+split[2])<Integer.parseInt(major+minor+revision)){
            return true;
        }else {
            return false;
        }

//        if(Integer.parseInt(split[0])<Integer.parseInt(major)){
//            return true;
//        }else if(Integer.parseInt(split[0])==Integer.parseInt(major)&&Integer.parseInt(split[1])<Integer.parseInt(minor)){
//            return true;
//        }else if(Integer.parseInt(split[0])==Integer.parseInt(major)&&Integer.parseInt(split[1])==Integer.parseInt(minor)&&Integer.parseInt(split[2])<Integer.parseInt(revision)){
//            return true;
//        }else {
//            return false;
//        }
    }

    public void setIsLasted(boolean isLasted) {
        this.isLasted = isLasted;
    }
    public boolean isIsLasted() {
        return isLasted;
    }

    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }

    public static int getRequiredForced() {
        return REQUIRED_FORCED;
    }

    public boolean isLasted() {
        return isLasted;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getVision(){
        return "/"+major+minor+revision+".apk";
    }
}
