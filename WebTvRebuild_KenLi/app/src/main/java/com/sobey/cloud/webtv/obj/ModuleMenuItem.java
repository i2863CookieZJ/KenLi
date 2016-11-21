package com.sobey.cloud.webtv.obj;

import android.graphics.drawable.Drawable;

public class ModuleMenuItem {
	private String title;
	private String url;
	private Drawable icon;

	public ModuleMenuItem(String title, String url, Drawable icon) {
		this.title = title;
		this.url = url;
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
