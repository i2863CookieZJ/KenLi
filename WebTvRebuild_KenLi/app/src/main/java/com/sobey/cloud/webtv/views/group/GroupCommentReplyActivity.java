package com.sobey.cloud.webtv.views.group;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.GroupCommentModel;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupCommentReplyActivity extends BaseActivity4Group {

	@GinInjectView(id = R.id.title)
	TextView title;
	@GinInjectView(id = R.id.title_left)
	Button titleLeft;
	@GinInjectView(id = R.id.title_right)
	Button titleRight;
	@GinInjectView(id = R.id.item_subject_detail_iv)
	AdvancedImageView advancedImageView;
	@GinInjectView(id = R.id.item_subject_detail_name_tv)
	TextView nameTv;
	@GinInjectView(id = R.id.item_subject_detail_des_tv)
	TextView desTv;
	@GinInjectView(id = R.id.item_subject_detail_title_tv)
	TextView titleTv;
	@GinInjectView(id = R.id.item_subject_detail_content_tv)
	TextView contentTv;
	@GinInjectView(id = R.id.item_subject_detail_reply_num_tv)
	TextView replyNumTv;
	@GinInjectView(id = R.id.item_subject_detail_reply_icon_tv)
	CheckBox replyIconCb;
	@GinInjectView(id = R.id.item_subject_detail_image_container)
	LinearLayout imgContainer;
	@GinInjectView(id = R.id.post_comment_bottom_content_layout)
	LinearLayout contentLayout;
	// @GinInjectView(id = R.id.post_comment_bottom_more_layout)
	// LinearLayout contentLayout;
	// @GinInjectView(id = R.id.post_comment_bottom_pic_layout)
	// LinearLayout contentLayout;
	// @GinInjectView(id = R.id.post_comment_bottom_more_layout)
	// LinearLayout contentLayout;

	private int mFloor;
	private GroupCommentModel mGroupCommentModel;

	@Override
	public int getContentView() {
		return R.layout.activity_group_comement_reply;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		if (null != bundle) {
			mGroupCommentModel = bundle.getParcelable("groupCommentModel");
			mFloor = bundle.getInt("floor");
		}
		setUpData();
	}

	public void setUpData() {
		StringBuilder sb = new StringBuilder();
		sb.append("第").append(mFloor).append("楼");
		title.setText(sb);
		titleRight.setVisibility(View.GONE);
	}
}
