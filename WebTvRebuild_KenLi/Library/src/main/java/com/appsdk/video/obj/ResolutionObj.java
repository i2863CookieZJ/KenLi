package com.appsdk.video.obj;

public class ResolutionObj {
	private String title;
	private String mediaPath;

	public ResolutionObj(String title, String mediaPath) {
		this.title = title;
		this.mediaPath = mediaPath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMediaPath() {
		return mediaPath;
	}

	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}
}
