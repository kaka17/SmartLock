package com.sht.smartlock.ui.ordering.entity;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * Product entity. @author MyEclipse Persistence Tools
 */

public class Product implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
//	private Shop shop;
	private String name;
	private Double prices;
	private Double discount;
	private Integer number;
	private Integer saleNumber;
	private String summary;
	private Integer state;
	private Integer isRecommend;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String  imgUrl;
	private String imgName;
	private ProductType type;	
	private Set<FileImage> fileImage = new HashSet<FileImage>(0);
	private File image;
	private String imageFileName; // 文件名称	
	private String imageContentType; // 文件类型
	private String fileListId;
	private int num=0;

	// 规范数据
	private String ID;//自增的用户唯一ID
	private String provider_id;//供应商id
	private String caption;//标题
	private String content;//服务内容描述路径
	private String price;//单价
	private String unit;//单位
	private String thumbnail;//缩略图路径
	private String brief;//简介
	private String start_time;//开始时间
	private String end_time;//截止时间
	private String hotel_price;//对酒店客户价格
	private String open_time;
	private String close_time;

	public String getOpen_time() {
		return open_time;
	}

	public void setOpen_time(String open_time) {
		this.open_time = open_time;
	}

	public String getClose_time() {
		return close_time;
	}

	public void setClose_time(String close_time) {
		this.close_time = close_time;
	}

	/** default constructor */
	public Product() {
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		setId(UUID.randomUUID().toString().replace("-", ""));
		this.ID = ID;
	}

	public String getProvider_id() {
		return provider_id;
	}

	public void setProvider_id(String provider_id) {
		this.provider_id = provider_id;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		this.name=caption;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;


	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getHotel_price() {
		return hotel_price;
	}

	public void setHotel_price(String hotel_price) {
		this.hotel_price = hotel_price;
		this.prices=Double.parseDouble(hotel_price);
	}


// Property accessors


	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ProductType getType() {
		return type;
	}

	public void setType(ProductType type) {
		this.type = type;
	}



	public String getName() {
		return this.name;
	}

	public String getShortName() {
		return this.name!=null && this.name.length() > 8 ? this.name.substring(0,8):this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrices() {
		return this.prices;
	}

	public void setPrices(Double price) {
		this.prices = price;
	}

	public Double getDiscount() {
		return this.discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Integer getNumber() {
		return this.number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getSaleNumber() {
		return this.saleNumber;
	}

	public void setSaleNumber(Integer saleNumber) {
		this.saleNumber = saleNumber;
	}

	public String getSummary() {
		return this.summary;
	}

	public String getSumma() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	

	public Integer getState() {
		return this.state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getIsRecommend() {
		return this.isRecommend;
	}

	public void setIsRecommend(Integer isRecommend) {
		this.isRecommend = isRecommend;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getImgUrl() {
		return imgUrl;
	}


	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}


	public File getImage() {
		return image;
	}

	
	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public String getImageContentType() {
		return imageContentType;
	}

	public void setImageContentType(String imageContentType) {
		this.imageContentType = imageContentType;
	}

	
	

	public String getTypeId() {
		return type.getId();
	}

	public void setTypeId(String typeId) {
		if(type==null){
			type = new ProductType();
		}
		this.type.setId(typeId);
	}

	public String getTypeName() {
		return this.type.getTypeName();
	}

	public void setTypeName(String typeName) {
		if(type==null){
			type = new ProductType();
		}
		this.type.setTypeName(typeName);
	}

	public String getFileListId() {
		return fileListId;
	}

	public void setFileListId(String fileListId) {
		this.fileListId = fileListId;
	}

	public Set<FileImage> getFileImage() {
		return fileImage;
	}

	public void setFileImage(Set<FileImage> fileImage) {
		this.fileImage = fileImage;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o != null && o instanceof Product){
			return ((Product)o).getId() == id;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Product{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", prices=" + prices +
				", discount=" + discount +
				", number=" + number +
				", saleNumber=" + saleNumber +
				", summary='" + summary + '\'' +
				", state=" + state +
				", isRecommend=" + isRecommend +
				", createTime=" + createTime +
				", updateTime=" + updateTime +
				", imgUrl='" + imgUrl + '\'' +
				", imgName='" + imgName + '\'' +
				", type=" + type +
				", fileImage=" + fileImage +
				", image=" + image +
				", imageFileName='" + imageFileName + '\'' +
				", imageContentType='" + imageContentType + '\'' +
				", fileListId='" + fileListId + '\'' +
				", num=" + num +
				", ID='" + ID + '\'' +
				", provider_id='" + provider_id + '\'' +
				", caption='" + caption + '\'' +
				", content='" + content + '\'' +
				", price='" + price + '\'' +
				", unit='" + unit + '\'' +
				", thumbnail='" + thumbnail + '\'' +
				", brief='" + brief + '\'' +
				", start_time='" + start_time + '\'' +
				", end_time='" + end_time + '\'' +
				", hotel_price='" + hotel_price + '\'' +
				'}';
	}
}