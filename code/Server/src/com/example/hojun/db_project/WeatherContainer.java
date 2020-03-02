package com.example.hojun.db_project;

import java.io.Serializable;
import java.sql.Timestamp;

public class WeatherContainer implements Serializable {
	private static final long serialVersionUID = 9216371960110601623L;
	public String city;
	public String province;
	public Double latitude;
	public Double longitude;
	public Timestamp date;
	public String wf;
	public int tmn;
	public int tmx;
	public String reliability;

	public WeatherContainer() {

	}

	public WeatherContainer(String province, Double latitude, Double longitude, String city, Timestamp date, String wf, int tmn, int tmx, String reliability) {
		this.city = city;
		this.province = province;
		this.latitude = latitude;
		this.longitude = longitude;
		this.date = date;
		this.wf = wf;
		this.tmn = tmn;
		this.tmx = tmx;
		this.reliability = reliability;
	}

	public String getCity() {
		return this.city;
	}

	public String getProvince() {
		return this.province;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public Double getLongitude() {
		return this.longitude;
	}

	public Timestamp getDate() {
		return this.date;
	}

	public String getWf() {
		return this.wf;
	}

	public int getTmn() {
		return this.tmn;
	}

	public int getTmx() {
		return this.tmx;
	}

	public String getReliability() {
		return this.reliability;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return province + " " + city + " " + date + " " + reliability;
	}

}
