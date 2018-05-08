package com.sht.smartlock.ui.ordering.entity;

import java.util.List;
import java.util.UUID;

/**
 * ProductType entity. @author MyEclipse Persistence Tools
 */

public class ProductType implements java.io.Serializable {

	// Fields

	private String id;
	private Integer shopID;
	private String typeName;
	private List<Product> productList;
	private String ID;
	private String caption;//分类名称
	private String items;
	private int ShoppingNum;

	public int getShoppingNum() {
		return ShoppingNum;
	}

	public void setShoppingNum(int shoppingNum) {
		ShoppingNum = shoppingNum;
	}

	private boolean ischenk;

	public boolean ischenk() {
		return ischenk;
	}

	public void setIschenk(boolean ischenk) {
		this.ischenk = ischenk;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}


	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		this.typeName=caption;
	}

	public String getID() {

		return ID;
	}

	public void setID(String ID) {
		setId(UUID.randomUUID().toString().replace("-", ""));
		this.ID = ID;

	}
	// Constructors

	/** default constructor */
	public ProductType() {
	}

	/** minimal constructor */
	public ProductType(Integer shopID) {
		this.shopID = shopID;
	}

	/** full constructor */
	public ProductType(Integer shopID, String typeName) {
		this.shopID = shopID;
		this.typeName = typeName;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public Integer getShopID() {
		return shopID;
	}

	public void setShopID(Integer shopID) {
		this.shopID = shopID;
	}

	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	@Override
	public String toString() {
		return "ProductType{" +
				"id='" + id + '\'' +
				", shopID=" + shopID +
				", typeName='" + typeName + '\'' +
				", productList=" + productList +
				", ID='" + ID + '\'' +
				", caption='" + caption + '\'' +
				'}';
	}
}