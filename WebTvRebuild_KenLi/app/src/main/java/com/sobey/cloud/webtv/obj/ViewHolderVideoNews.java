package com.sobey.cloud.webtv.obj;

import com.appsdk.advancedimageview.AdvancedImageView;

import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHolderVideoNews {
	private LinearLayout normalLayout;
	private TextView title;
	private TextView comments;
	private TextView playcount;
	private TextView duration;
	private AdvancedImageView thumbnail;

	public TextView getDuration() {
		return duration;
	}

	public void setDuration(TextView duration) {
		this.duration = duration;
	}

	public LinearLayout getNormalLayout() {
		return normalLayout;
	}

	public void setNormalLayout(LinearLayout normalLayout) {
		this.normalLayout = normalLayout;
	}

	public TextView getTitle() {
		return title;
	}

	public void setTitle(TextView title) {
		this.title = title;
	}

	public TextView getComments() {
		return comments;
	}

	public void setComments(TextView comments) {
		this.comments = comments;
	}

	public TextView getPlaycount() {
		return playcount;
	}

	public void setPlaycount(TextView playcount) {
		this.playcount = playcount;
	}

	public AdvancedImageView getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(AdvancedImageView thumbnail) {
		this.thumbnail = thumbnail;
	}

}
