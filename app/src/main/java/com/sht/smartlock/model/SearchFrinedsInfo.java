package com.sht.smartlock.model;

/**
 * Created by Administrator on 2016/2/25.
 */
public class SearchFrinedsInfo {
    private String id_image;
    private String emid;
    private String name;

    public SearchFrinedsInfo() {
    }

    public SearchFrinedsInfo(String id_image, String emid, String name) {
        this.id_image = id_image;
        this.emid = emid;
        this.name = name;
    }

    public String getId_image() {
        return id_image;
    }

    public void setId_image(String id_image) {
        this.id_image = id_image;
    }

    public String getEmid() {
        return emid;
    }

    public void setEmid(String emid) {
        this.emid = emid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
