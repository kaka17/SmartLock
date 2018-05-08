package com.sht.smartlock.ui.entity;

import com.sht.smartlock.model.BaseModel;
import com.sht.smartlock.ui.ordering.entity.Product;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/9/23.
 */
public class OrderEntity extends BaseModel {
    private List<Product> myShopList;

    public List<Product> getMyShopList() {
        return myShopList;
    }

    public void setMyShopList(List<Product> myShopList) {
        this.myShopList = myShopList;
    }
}
