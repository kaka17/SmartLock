package com.sht.smartlock.ui.entity;

/**
 * Created by Administrator on 2015/10/12.
 */
public class OpenDoorRecordEntity {
    private String source;
    private String ID;
    private String success;
    private String unlock_time;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getUnlock_time() {
        return unlock_time;
    }

    public void setUnlock_time(String unlock_time) {
        this.unlock_time = unlock_time;
    }
}
