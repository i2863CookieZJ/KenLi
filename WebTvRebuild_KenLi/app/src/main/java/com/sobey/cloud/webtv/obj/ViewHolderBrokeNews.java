package com.sobey.cloud.webtv.obj;

import com.appsdk.advancedimageview.AdvancedImageView;

import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolderBrokeNews {
	private TextView title;
	private AdvancedImageView image;
	private AdvancedImageView headicon;
	private TextView time;
	private TextView commentcount;
	private TextView username;
	private ImageView videocountImage;
	private TextView videocount;
	private ImageView picturecountImage;
	private TextView picturecount;

	public ImageView getVideocountImage() {
		return videocountImage;
	}

	public void setVideocountImage(ImageView videocountImage) {
		this.videocountImage = videocountImage;
	}

	public ImageView getPicturecountImage() {
		return picturecountImage;
	}

	public void setPicturecountImage(ImageView picturecountImage) {
		this.picturecountImage = picturecountImage;
	}

	public TextView getTitle() {
		return title;
	}

	public void setTitle(TextView title) {
		this.title = title;
	}

	public AdvancedImageView getImage() {
		return image;
	}

	public void setImage(AdvancedImageView image) {
		this.image = image;
	}

	public AdvancedImageView getHeadicon() {
		return headicon;
	}

	public void setHeadicon(AdvancedImageView headicon) {
		this.headicon = headicon;
	}

	public TextView getTime() {
		return time;
	}

	public void setTime(TextView time) {
		this.time = time;
	}

	public TextView getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(TextView commentcount) {
		this.commentcount = commentcount;
	}

	public TextView getUsername() {
		return username;
	}

	public void setUsername(TextView username) {
		this.username = username;
	}

	public TextView getVideocount() {
		return videocount;
	}

	public void setVideocount(TextView videocount) {
		this.videocount = videocount;
	}

	public TextView getPicturecount() {
		return picturecount;
	}

	public void setPicturecount(TextView picturecount) {
		this.picturecount = picturecount;
	}
}
