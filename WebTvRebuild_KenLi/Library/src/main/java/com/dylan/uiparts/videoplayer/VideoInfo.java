package com.dylan.uiparts.videoplayer;

import java.util.ArrayList;

import android.net.Uri;

public class VideoInfo {
	private String title;
	private String fileName;
	private String path;
	private ArrayList<Uri> uriList = new ArrayList<Uri>();
	private ArrayList<String> formatTypeList = new ArrayList<String>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ArrayList<Uri> getUriList() {
		return uriList;
	}

	public void setUriList(ArrayList<Uri> uriList) {
		this.uriList = uriList;
	}

	public ArrayList<String> getFormatTypeList() {
		return formatTypeList;
	}

	public void setFormatTypeList(ArrayList<String> formatTypeList) {
		this.formatTypeList = formatTypeList;
	}
}
