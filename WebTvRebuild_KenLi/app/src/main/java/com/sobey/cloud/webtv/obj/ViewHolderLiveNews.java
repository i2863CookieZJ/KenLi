package com.sobey.cloud.webtv.obj;

import com.appsdk.advancedimageview.AdvancedImageView;

import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolderLiveNews {
	private TextView title;
	private TextView startTime;
	private TextView endTime;
	private AdvancedImageView thumbnail;
	private ImageView controlBtn;

	public TextView getTitle() {
		return title;
	}

	public void setTitle(TextView title) {
		this.title = title;
	}

	public TextView getStartTime() {
		return startTime;
	}

	public void setStartTime(TextView startTime) {
		this.startTime = startTime;
	}

	public TextView getEndTime() {
		return endTime;
	}

	public void setEndTime(TextView endTime) {
		this.endTime = endTime;
	}

	public AdvancedImageView getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(AdvancedImageView thumbnail) {
		this.thumbnail = thumbnail;
	}

	public ImageView getControlBtn() {
		return controlBtn;
	}

	public void setControlBtn(ImageView controlBtn) {
		this.controlBtn = controlBtn;
	}
}
