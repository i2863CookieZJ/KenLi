package com.sobey.cloud.webtv.obj;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.uiparts.circularseekbar.CircularSeekBar;

import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewHolderBrokeTask {
	private TextView title;
	private AdvancedImageView image;
	private TextView time;
	private ImageView videocountImage;
	private TextView videocount;
	private ImageView picturecountImage;
	private TextView picturecount;
	private RelativeLayout waitingLayout;
	private RelativeLayout uploadingLayout;
	private CircularSeekBar uploadingCircularSeekbar;
	private Button actionBtn;
	private LinearLayout contentLayout;
	private HorizontalScrollView horizontalScrollView;
	
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

	public TextView getTime() {
		return time;
	}

	public void setTime(TextView time) {
		this.time = time;
	}

	public ImageView getVideocountImage() {
		return videocountImage;
	}

	public void setVideocountImage(ImageView videocountImage) {
		this.videocountImage = videocountImage;
	}

	public TextView getVideocount() {
		return videocount;
	}

	public void setVideocount(TextView videocount) {
		this.videocount = videocount;
	}

	public ImageView getPicturecountImage() {
		return picturecountImage;
	}

	public void setPicturecountImage(ImageView picturecountImage) {
		this.picturecountImage = picturecountImage;
	}

	public TextView getPicturecount() {
		return picturecount;
	}

	public void setPicturecount(TextView picturecount) {
		this.picturecount = picturecount;
	}

	public RelativeLayout getWaitingLayout() {
		return waitingLayout;
	}

	public void setWaitingLayout(RelativeLayout waitingLayout) {
		this.waitingLayout = waitingLayout;
	}

	public RelativeLayout getUploadingLayout() {
		return uploadingLayout;
	}

	public void setUploadingLayout(RelativeLayout uploadingLayout) {
		this.uploadingLayout = uploadingLayout;
	}

	public CircularSeekBar getUploadingCircularSeekbar() {
		return uploadingCircularSeekbar;
	}

	public void setUploadingCircularSeekbar(CircularSeekBar uploadingCircularSeekbar) {
		this.uploadingCircularSeekbar = uploadingCircularSeekbar;
	}

	public Button getActionBtn() {
		return actionBtn;
	}

	public void setActionBtn(Button actionBtn) {
		this.actionBtn = actionBtn;
	}

	public HorizontalScrollView getHorizontalScrollView() {
		return horizontalScrollView;
	}

	public void setHorizontalScrollView(HorizontalScrollView horizontalScrollView) {
		this.horizontalScrollView = horizontalScrollView;
	}

	public LinearLayout getContentLayout() {
		return contentLayout;
	}

	public void setContentLayout(LinearLayout contentLayout) {
		this.contentLayout = contentLayout;
	}
}
