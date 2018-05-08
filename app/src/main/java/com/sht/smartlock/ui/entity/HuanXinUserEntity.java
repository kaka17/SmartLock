package com.sht.smartlock.ui.entity;

/**
 * Created by Administrator on 2015/9/21.
 */
public class HuanXinUserEntity {
    private String uuid;
    private String type;
    private String created;
    private String username;
    private String activated;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getActivated() {
        return activated;
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }

    @Override
    public String toString() {
        return "HuanXinUserEntity{" +
                "uuid='" + uuid + '\'' +
                ", type='" + type + '\'' +
                ", created='" + created + '\'' +
                ", username='" + username + '\'' +
                ", activated='" + activated + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
