package com.sobey.cloud.webtv.obj;

import com.appsdk.advancedimageview.AdvancedImageView;

import android.widget.TextView;

public class ViewHolderVideoNewsDetailComments {
	private TextView user;
	private TextView comments;
	private TextView date;
	private AdvancedImageView thumbnail;

	public TextView getUser() {
		return user;
	}

	public void setUser(TextView user) {
		this.user = user;
	}

	public TextView getComments() {
		return comments;
	}

	public void setComments(TextView comments) {
		this.comments = comments;
	}

	public TextView getDate() {
		return date;
	}

	public void setDate(TextView date) {
		this.date = date;
	}

	public AdvancedImageView getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(AdvancedImageView thumbnail) {
		this.thumbnail = thumbnail;
	}
}
