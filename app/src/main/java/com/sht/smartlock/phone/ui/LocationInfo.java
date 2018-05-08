package com.sht.smartlock.phone.ui;

import java.io.Serializable;


public class LocationInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5339411496152105157L;
	private double lat;
	private double lon;
	private String address;
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	

}
