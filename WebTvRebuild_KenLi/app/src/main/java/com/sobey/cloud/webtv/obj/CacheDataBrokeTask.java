package com.sobey.cloud.webtv.obj;

import java.util.ArrayList;

import org.json.JSONObject;

import com.sobey.cloud.webtv.senum.BrokeTaskStatus;

public class CacheDataBrokeTask {
	private String username;
	private String id;
	private String catalogId;
	private String logo;
	private String time;
	private String title;
	private String phone;
	private String location;
	private String index;
	private int videocount;
	private int imagecount;
	private BrokeTaskStatus status;
	private int progress;
	private ArrayList<JSONObject> filePathList;

	public CacheDataBrokeTask() {
	}

	public CacheDataBrokeTask(String username, String index, String id, String catalogId, String logo, String time, String title, String phone, String location, int videocount, int imagecount, BrokeTaskStatus status, int progress, ArrayList<JSONObject> filePathList) {
		this.username = username;
		this.index = index;
		this.id = id;
		this.catalogId = catalogId;
		this.logo = logo;
		this.time = time;
		this.title = title;
		this.phone = phone;
		this.location = location;
		this.videocount = videocount;
		this.imagecount = imagecount;
		this.status = status;
		this.progress = progress;
		this.filePathList = filePathList;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getVideocount() {
		return videocount;
	}

	public void setVideocount(int videocount) {
		this.videocount = videocount;
	}

	public int getImagecount() {
		return imagecount;
	}

	public void setImagecount(int imagecount) {
		this.imagecount = imagecount;
	}

	public BrokeTaskStatus getStatus() {
		return status;
	}

	public void setStatus(BrokeTaskStatus status) {
		this.status = status;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<JSONObject> getFilePathList() {
		return filePathList;
	}

	public void setFilePathList(ArrayList<JSONObject> filePathList) {
		this.filePathList = filePathList;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
}
