package com.sobey.cloud.webtv.obj;

import com.appsdk.advancedimageview.AdvancedImageView;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHolderGeneralNews {
	private LinearLayout normalLayout;
	private LinearLayout pictureLayout;
	private TextView title;
	private TextView summary;
	private TextView comments;
	private AdvancedImageView thumbnail;
	private ImageView type;
	private TextView picture_title;
	private AdvancedImageView picture_image1;
	private AdvancedImageView picture_image2;
	private AdvancedImageView picture_image3;
	private TextView picture_comments;
	private TextView picture_count;
	private TextView picture_refername;

	public LinearLayout getPictureLayout() {
		return pictureLayout;
	}

	public void setPictureLayout(LinearLayout pictureLayout) {
		this.pictureLayout = pictureLayout;
	}

	public TextView getPicture_title() {
		return picture_title;
	}

	public void setPicture_title(TextView picture_title) {
		this.picture_title = picture_title;
	}

	public AdvancedImageView getPicture_image1() {
		return picture_image1;
	}

	public void setPicture_image1(AdvancedImageView picture_image1) {
		this.picture_image1 = picture_image1;
	}

	public AdvancedImageView getPicture_image2() {
		return picture_image2;
	}

	public void setPicture_image2(AdvancedImageView picture_image2) {
		this.picture_image2 = picture_image2;
	}

	public AdvancedImageView getPicture_image3() {
		return picture_image3;
	}

	public void setPicture_image3(AdvancedImageView picture_image3) {
		this.picture_image3 = picture_image3;
	}

	public TextView getPicture_comments() {
		return picture_comments;
	}

	public void setPicture_comments(TextView picture_comments) {
		this.picture_comments = picture_comments;
	}

	public TextView getPicture_count() {
		return picture_count;
	}

	public void setPicture_count(TextView picture_count) {
		this.picture_count = picture_count;
	}

	public TextView getPicture_refername() {
		return picture_refername;
	}

	public void setPicture_refername(TextView picture_refername) {
		this.picture_refername = picture_refername;
	}

	public ImageView getType() {
		return type;
	}

	public void setType(ImageView type) {
		this.type = type;
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

	public TextView getSummary() {
		return summary;
	}

	public void setSummary(TextView summary) {
		this.summary = summary;
	}

	public TextView getComments() {
		return comments;
	}

	public void setComments(TextView comments) {
		this.comments = comments;
	}

	public AdvancedImageView getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(AdvancedImageView thumbnail) {
		this.thumbnail = thumbnail;
	}
}
