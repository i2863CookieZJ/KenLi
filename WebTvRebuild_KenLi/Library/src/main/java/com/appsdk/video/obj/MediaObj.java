package com.appsdk.video.obj;

import android.text.TextUtils;

public class MediaObj {
	private String title;
	private ResolutionList resolutionList;
	private String mediaPath;
	private boolean showTitle;
	private boolean resolutionMode;
	private boolean showControler;

	public MediaObj(String title, ResolutionList resolutionList, boolean showControler) throws Exception {
		if (resolutionList == null || resolutionList.getResolutions() == null || resolutionList.getResolutions().size() <= 0) {
			throw new Exception("resolutionObj should be initialized");
		}
		this.resolutionList = resolutionList;
		this.resolutionMode = true;
		if (TextUtils.isEmpty(title)) {
			showTitle = false;
		} else {
			showTitle = true;
			this.title = title;
		}
		this.showControler = showControler;
	}

	public MediaObj(String title, String mediaPath, boolean showControler) throws Exception {
		if (TextUtils.isEmpty(mediaPath)) {
			throw new Exception("mediaPath can not be null");
		}
		this.mediaPath = mediaPath;
		this.resolutionMode = false;
		if (TextUtils.isEmpty(title)) {
			showTitle = false;
		} else {
			showTitle = true;
			this.title = title;
		}
		this.showControler = showControler;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ResolutionList getResolutionList() {
		return resolutionList;
	}

	public void setResolutionList(ResolutionList resolutionList) {
		this.resolutionList = resolutionList;
	}

	public String getMediaPath() {
		return mediaPath;
	}

	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}

	public boolean isShowTitle() {
		return showTitle;
	}

	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

	public boolean isResolutionMode() {
		return resolutionMode;
	}

	public void setResolutionMode(boolean resolutionMode) {
		this.resolutionMode = resolutionMode;
	}

	public boolean isShowControler() {
		return showControler;
	}

	public void setShowControler(boolean showControler) {
		this.showControler = showControler;
	}
}
