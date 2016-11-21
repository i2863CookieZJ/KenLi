package com.sobey.cloud.webtv.widgets.webview;

public class ImageUrlInfo {
	private String url;
	private String imageId;
	private String localFilePath;
	private boolean isDownload = false;

	public ImageUrlInfo() {
	}

	public ImageUrlInfo(String url, String imageId, String localFilePath) {
		this.url = url;
		this.imageId = imageId;
		this.localFilePath = localFilePath;
		this.isDownload = false;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getLocalFilePath() {
		return localFilePath;
	}

	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}
}
