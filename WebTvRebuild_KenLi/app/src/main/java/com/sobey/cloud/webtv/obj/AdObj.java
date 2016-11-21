package com.sobey.cloud.webtv.obj;

import org.json.JSONObject;

public class AdObj {
	private String id;
	private String name;
	private String picUrl;
	private String linkUrl;
	private String picPath;

	public AdObj() {
	}

	public AdObj(String id, String name, String picUrl, String linkUrl) {
		this.id = id;
		this.name = name;
		this.picUrl = picUrl;
		this.linkUrl = linkUrl;
	}

	public static String AdObjToString(AdObj obj) {
		try {
			if (obj == null) {
				return null;
			}
			JSONObject object = new JSONObject();
			object.put("id", obj.getId());
			object.put("name", obj.getName());
			object.put("pic_url", obj.getPicUrl());
			object.put("link_url", obj.getLinkUrl());
			object.put("pic_path", obj.getPicPath());
			return object.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static AdObj StringToAdObj(String str) {
		try {
			JSONObject object = new JSONObject(str);
			AdObj obj = new AdObj(object.optString("id"), object.optString("name"), object.optString("pic_url"), object.optString("link_url"));
			obj.setPicPath(object.optString("pic_path"));
			return obj;
		} catch (Exception e) {
			return null;
		}
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

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
}
