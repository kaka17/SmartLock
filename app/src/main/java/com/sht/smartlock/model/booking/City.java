package com.sht.smartlock.model.booking;

/**
 * Created by Administrator on 2015/9/17.
 */
public class City {
    private String id;
    private String caption;
    private int rank;

    public City(String id,String caption,int rank){
        this.id=id;
        this.caption=caption;
        this.rank=rank;

    }



    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }


}
