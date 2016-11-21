package com.sobey.cloud.webtv.obj;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewHolderLiveNewsDetailGuide {
	private TextView startTime;
	private TextView state;
	private ImageView controlBtn;
	
	public RelativeLayout program_list_item_container;

	public TextView getStartTime() {
		return startTime;
	}

	public void setStartTime(TextView startTime) {
		this.startTime = startTime;
	}

	public TextView getState() {
		return state;
	}

	public void setState(TextView state) {
		this.state = state;
	}

	public ImageView getControlBtn() {
		return controlBtn;
	}

	public void setControlBtn(ImageView controlBtn) {
		this.controlBtn = controlBtn;
	}
}
