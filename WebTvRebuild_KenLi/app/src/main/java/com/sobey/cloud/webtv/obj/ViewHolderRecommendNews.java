package com.sobey.cloud.webtv.obj;

import com.appsdk.advancedimageview.AdvancedImageView;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewHolderRecommendNews {
	private TextView largePicTitle;
	private TextView smallPicTitle;
	private TextView singlePicTitle;
	private TextView time;
	private AdvancedImageView largePicImage;
	private AdvancedImageView smallPicImage;
	private AdvancedImageView singlePicImage;
	private RelativeLayout largePicLayout;
	private LinearLayout smallPicLayout;
	private LinearLayout singlePicLayout;
	private LinearLayout divider;

	public TextView getLargePicTitle() {
		return largePicTitle;
	}

	public void setLargePicTitle(TextView largePicTitle) {
		this.largePicTitle = largePicTitle;
	}

	public TextView getSmallPicTitle() {
		return smallPicTitle;
	}

	public void setSmallPicTitle(TextView smallPicTitle) {
		this.smallPicTitle = smallPicTitle;
	}

	public TextView getSinglePicTitle() {
		return singlePicTitle;
	}

	public void setSinglePicTitle(TextView singlePicTitle) {
		this.singlePicTitle = singlePicTitle;
	}

	public TextView getTime() {
		return time;
	}

	public void setTime(TextView time) {
		this.time = time;
	}

	public AdvancedImageView getLargePicImage() {
		return largePicImage;
	}

	public void setLargePicImage(AdvancedImageView largePicImage) {
		this.largePicImage = largePicImage;
	}

	public AdvancedImageView getSmallPicImage() {
		return smallPicImage;
	}

	public void setSmallPicImage(AdvancedImageView smallPicImage) {
		this.smallPicImage = smallPicImage;
	}

	public AdvancedImageView getSinglePicImage() {
		return singlePicImage;
	}

	public void setSinglePicImage(AdvancedImageView singlePicImage) {
		this.singlePicImage = singlePicImage;
	}

	public RelativeLayout getLargePicLayout() {
		return largePicLayout;
	}

	public void setLargePicLayout(RelativeLayout largePicLayout) {
		this.largePicLayout = largePicLayout;
	}

	public LinearLayout getSmallPicLayout() {
		return smallPicLayout;
	}

	public void setSmallPicLayout(LinearLayout smallPicLayout) {
		this.smallPicLayout = smallPicLayout;
	}

	public LinearLayout getSinglePicLayout() {
		return singlePicLayout;
	}

	public void setSinglePicLayout(LinearLayout singlePicLayout) {
		this.singlePicLayout = singlePicLayout;
	}

	public LinearLayout getDivider() {
		return divider;
	}

	public void setDivider(LinearLayout divider) {
		this.divider = divider;
	}
}
