package com.sobey.cloud.webtv.obj;

import com.appsdk.advancedimageview.AdvancedImageView;

import android.widget.TextView;

public class ViewHolderTopicNews {
	private TextView title;
	private TextView summary;
	private TextView displayNum;
	private AdvancedImageView image;

	public TextView getTitle() {
		return title;
	}

	public void setTitle(TextView title) {
		this.title = title;
	}

	public TextView getSummary() {
		return summary;
	}

	public void setSummary(TextView summary) {
		this.summary = summary;
	}

	public TextView getDisplayNum() {
		return displayNum;
	}

	public void setDisplayNum(TextView displayNum) {
		this.displayNum = displayNum;
	}

	public AdvancedImageView getImage() {
		return image;
	}

	public void setImage(AdvancedImageView image) {
		this.image = image;
	}
}
