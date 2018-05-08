package com.sht.smartlock.model.booking;

import android.widget.ImageView;

/**
 * Created by Administrator on 2015/9/28.
 */
public class PayChannel {
    private String id;
    private String name;
    private String introduction;
    private int picture;
    private boolean isSelected;

    public PayChannel(String id, String name, String introduction, int ivPicture, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.introduction = introduction;
        this.picture = ivPicture;
        this.isSelected = isSelected;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getIvPicture() {
        return picture;
    }

    public void setIvPicture(int ivPicture) {
        this.picture = ivPicture;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
