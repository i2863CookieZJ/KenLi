package com.sobey.cloud.webtv.adapter;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sobey.cloud.webtv.FriendCenterActivity;
import com.sobey.cloud.webtv.bean.GroupCommentModel;
import com.sobey.cloud.webtv.bean.GroupModel;
import com.sobey.cloud.webtv.bean.GroupRequestMananger;
import com.sobey.cloud.webtv.bean.GroupRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.bean.GroupUserModel;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.BaseUtil;
import com.sobey.cloud.webtv.utils.Display;
import com.sobey.cloud.webtv.utils.FaceUtil;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.SobeyConstants;
import com.sobey.cloud.webtv.views.group.GroupDetailActivity;
import com.sobey.cloud.webtv.views.group.GroupSubjectActivity;
import com.sobey.cloud.webtv.views.group.PhotoDetailViewPagerActivity;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class GroupSubjectDetailAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	// 从主题详情页传入 携带当前主题相关信息
	private GroupSubjectModel baseSubjectModel;
	// 当前页面查询的主题详情，携带评论列表相关信息
	private GroupSubjectModel mGroupSubjectModel;
	private List<GroupCommentModel> mGroupCommentModels;
	private List<GroupUserModel> mLikedUserList;
	private final String TAG = this.getClass().getName();
	private Handler mHandler;
	private int mCurrentPage;
	private int mPageSize;
	private DisplayImageOptions imageOptions;

	public GroupSubjectDetailAdapter(Context ctx, Handler handler) {
		this.mInflater = LayoutInflater.from(ctx);
		this.mContext = ctx;
		this.mHandler = handler;
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.showImageForEmptyUri(R.drawable.logined_default_head)
				.showImageOnLoading(R.drawable.logined_default_head);
		imageOptions = builder.build();
	}

	public void setPageInfo(int currentPage, int pageSize) {
		this.mCurrentPage = currentPage;
		this.mPageSize = pageSize;
	}

	public void setData(GroupSubjectModel gropGroupSubjectModel, GroupSubjectModel baseSubjectModel) {
		this.mGroupSubjectModel = gropGroupSubjectModel;
		this.baseSubjectModel = baseSubjectModel;
		this.mGroupCommentModels = mGroupSubjectModel.commentList;
		this.mLikedUserList = mGroupSubjectModel.likedUserList;
		// 添加评论数据 此条目主要用于区别于主题item和评论item listview第一项为主题item 添加到列表首位置
		GroupCommentModel commentModel = new GroupCommentModel();
		commentModel.type = 0;
		mGroupCommentModels.add(0, commentModel);
	}

	public void addData(GroupSubjectModel gropGroupSubjectModel) {
		this.mGroupCommentModels.addAll(gropGroupSubjectModel.commentList);
		notifyDataSetChanged();
	}

	public void addLikedUser(String uid, String userName, String userHeadUrl) {
		GroupUserModel groupUserModel = new GroupUserModel();
		groupUserModel.uid = uid;
		groupUserModel.userName = userName;
		groupUserModel.userHeadUrl = userHeadUrl;
		mLikedUserList.add(0, groupUserModel);
		notifyDataSetChanged();
	}

	public void delLikedUser(String uid, String userName, String userHeadUrl) {
		GroupUserModel exsitUserModel = null;
		for (GroupUserModel userModel : mLikedUserList) {
			if (uid.equals(userModel.uid)) {
				exsitUserModel = userModel;
				break;
			}
		}
		if (null != exsitUserModel) {
			mLikedUserList.remove(exsitUserModel);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mGroupCommentModels.size();
	}

	@Override
	public GroupCommentModel getItem(int position) {
		return mGroupCommentModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("GroupDetailAdapter", "childcount:" + parent.getChildCount() + ",position:" + position);
		GroupCommentModel groupCommentModel = getItem(position);
		// 第一项 构建主题相关视图
		if (groupCommentModel.type == 0) {
			View v = buildSujectViews();
			return v;
		}
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_group_subject_detail_listview_2, null);
			holder.nameTv = (TextView) convertView.findViewById(R.id.item_subject_detail_name_tv);
			holder.desTv = (TextView) convertView.findViewById(R.id.item_subject_detail_des_tv);
			holder.contentTv = (TextView) convertView.findViewById(R.id.item_subject_detail_content_tv);
			holder.replyTv = (TextView) convertView.findViewById(R.id.item_subject_detail_reply_num_tv);
			holder.headIv = (AdvancedImageView) convertView.findViewById(R.id.item_subject_detail_iv);
			holder.likeTv = (TextView) convertView.findViewById(R.id.item_subject_detail_reply_icon_tv);
			holder.likeIv = (ImageView) convertView.findViewById(R.id.like_iv);
			holder.CommentreplyContentTv = (TextView) convertView
					.findViewById(R.id.item_subject_detail_reply_content_tv);
			holder.commentImgContainer = (LinearLayout) convertView
					.findViewById(R.id.item_subject_detail_image_container);
			holder.commentReplyLayout = (LinearLayout) convertView
					.findViewById(R.id.item_subject_detail_comment_reply_layout);
			holder.commentReplyContainer = (LinearLayout) convertView
					.findViewById(R.id.item_subject_detail_comment_reply_container);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			if (holder == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_group_subject_detail_listview_2, null);
				holder.nameTv = (TextView) convertView.findViewById(R.id.item_subject_detail_name_tv);
				holder.desTv = (TextView) convertView.findViewById(R.id.item_subject_detail_des_tv);
				holder.contentTv = (TextView) convertView.findViewById(R.id.item_subject_detail_content_tv);
				holder.replyTv = (TextView) convertView.findViewById(R.id.item_subject_detail_reply_num_tv);
				holder.headIv = (AdvancedImageView) convertView.findViewById(R.id.item_subject_detail_iv);
				holder.likeTv = (TextView) convertView.findViewById(R.id.item_subject_detail_reply_icon_tv);
				holder.likeIv = (ImageView) convertView.findViewById(R.id.like_iv);
				holder.CommentreplyContentTv = (TextView) convertView
						.findViewById(R.id.item_subject_detail_reply_content_tv);
				holder.commentImgContainer = (LinearLayout) convertView
						.findViewById(R.id.item_subject_detail_image_container);
				holder.commentReplyLayout = (LinearLayout) convertView
						.findViewById(R.id.item_subject_detail_comment_reply_layout);
				holder.commentReplyContainer = (LinearLayout) convertView
						.findViewById(R.id.item_subject_detail_comment_reply_container);
			}
		}

		// buildCommentReplyViews(groupCommentModel,holder);
		// if(groupCommentModel.commentUserId.equals(PreferencesUtil.getLoggedUserId())){
		// holder.nameTv.setText(PreferencesUtil.getLoggedUserName());
		// }else{
		// }
		groupCommentModel.CommentUserHeadUrl = groupCommentModel.CommentUserHeadUrl
				.replace("http://qz.sobeycache.com/uc_server/", "");
		holder.nameTv.setText(groupCommentModel.commentUserName);
		holder.desTv.setText(getDes(position, groupCommentModel.commentPostedTime));

		MyClickListener clickListener = new MyClickListener(groupCommentModel, position);

		holder.replyTv.setOnClickListener(clickListener);
		holder.headIv.setOnClickListener(clickListener);
		holder.nameTv.setOnClickListener(clickListener);// 点名字不在跳转个人页面

		if (!TextUtils.isEmpty(groupCommentModel.commentReplyContent)) {
			holder.CommentreplyContentTv.setVisibility(View.VISIBLE);
			holder.CommentreplyContentTv.setText(Html.fromHtml(groupCommentModel.commentReplyContent));
		} else {
			holder.CommentreplyContentTv.setVisibility(View.GONE);
		}

		boolean commentIsLiked = PreferencesUtil
				.getBoolean(PreferencesUtil.KEY_COMMENT_IS_LIKED + groupCommentModel.commentId);
		int likeCount = PreferencesUtil.getInt(PreferencesUtil.KEY_COMMENT_LIKE_COUNT + groupCommentModel.commentId);
		if (commentIsLiked) {
			holder.likeIv.setImageResource(R.drawable.icon_group_like_press);
		} else {
			holder.likeIv.setImageResource(R.drawable.icon_group_like_nor);
		}

		holder.likeTv.setText(String.valueOf(likeCount));
		holder.likeTv.setOnClickListener(clickListener);

		ImageLoader.getInstance().displayImage(groupCommentModel.CommentUserHeadUrl, holder.headIv, imageOptions);
		String comment = groupCommentModel.commentContent;

		try {
			if (!TextUtils.isEmpty(comment) && comment.contains("\n")) {
				comment = comment.replace("\n", "<br/>");
			}
			holder.contentTv.setText(Html.fromHtml(comment, new MyImgGetter(), null));
		} catch (Exception e) {
			holder.contentTv.setText(comment);
		}

		buildSujectImgViews(holder.commentImgContainer, groupCommentModel.commentPicUrls);

		return convertView;

	}

	/**
	 * 构建评论的回复视图
	 * 
	 * @param groupCommentModel
	 * @param holder
	 */
	public void buildCommentReplyViews(GroupCommentModel groupCommentModel, ViewHolder holder) {
		if (null != groupCommentModel.commentReplyList && groupCommentModel.commentReplyList.size() > 0) {
			holder.commentReplyLayout.setVisibility(View.VISIBLE);
			holder.commentReplyContainer.removeAllViews();
			LinearLayout itemLayout = new LinearLayout(mContext);
			itemLayout.setOrientation(LinearLayout.HORIZONTAL);
			final String name = "泷泽萝拉";
			StringBuilder builder = new StringBuilder();
			builder.append(name).append(":").append("其实我们可以一起进入小屋子~").append(" ").append("2015-4-24");
			SpannableString spannableString = new SpannableString(builder);
			SpannableStringBuilder ssb = new SpannableStringBuilder(spannableString);
			int start = 0;
			int end = name.length();
			ssb.setSpan(new ClickableSpan() {

				@Override
				public void onClick(View widget) {
					Toast.makeText(mContext, name, Toast.LENGTH_LONG).show();
				}

				@Override
				public void updateDrawState(TextPaint ds) {
					super.updateDrawState(ds);
					ds.setColor(Color.BLUE);
					ds.setUnderlineText(false);
				}
			}, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.setSpan(new ClickableSpan() {

				@Override
				public void onClick(View widget) {
					Log.i(TAG, "the rest1 is clicked!!");
				}

				@Override
				public void updateDrawState(TextPaint ds) {
					super.updateDrawState(ds);
					ds.setColor(Color.BLACK);
					ds.setUnderlineText(false);
				}
			}, name.length(), builder.lastIndexOf(" "), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.setSpan(new ClickableSpan() {

				@Override
				public void onClick(View widget) {
					Log.i(TAG, "the rest2 is clicked!!");
				}

				@Override
				public void updateDrawState(TextPaint ds) {
					super.updateDrawState(ds);
					ds.setColor(Color.GRAY);
					ds.setUnderlineText(false);
				}
			}, builder.lastIndexOf(" "), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			TextView textView1 = new TextView(mContext);
			LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			textView1.setClickable(true);
			textView1.setMovementMethod(LinkMovementMethod.getInstance());
			textView1.setFocusable(true);
			textView1.setLongClickable(true);
			textView1.setText(ssb);
			itemLayout.addView(textView1);

			holder.commentReplyContainer.addView(itemLayout, itemLayoutParams);
		} else {
			holder.commentReplyLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 构建主题视图
	 * 
	 * @return
	 */
	public View buildSujectViews() {
		String[] urls = baseSubjectModel.subjectPicUrls;
		View view = mInflater.inflate(R.layout.item_group_subject_detail_listview_1, null);

		LinearLayout likeContainer = (LinearLayout) view.findViewById(R.id.item_subject_detail_like_container);
		LinearLayout iamgeContainer = (LinearLayout) view.findViewById(R.id.item_subject_detail_image_container);

		AdvancedImageView headImg = (AdvancedImageView) view.findViewById(R.id.item_subject_detail_iv);
		TextView nameTv = (TextView) view.findViewById(R.id.item_subject_detail_name_tv);
		TextView desTv = (TextView) view.findViewById(R.id.item_subject_detail_des_tv);
		TextView titleTv = (TextView) view.findViewById(R.id.item_subject_detail_title_tv);
		TextView contentTv = (TextView) view.findViewById(R.id.item_subject_detail_content_tv);
		Button goGroupBtn = (Button) view.findViewById(R.id.item_subject_detail_go_group_btn);

		goGroupBtn.setText(baseSubjectModel.groupName + " >");
		goGroupBtn.setOnClickListener(new MyClickListener());
		headImg.setOnClickListener(new MyClickListener());
		headImg.setTag("louzhu");
		iamgeContainer.removeAllViews();

		// 设置头像
		ImageLoader.getInstance().displayImage(baseSubjectModel.publishUserHeadUrl, headImg);
		// 设置名称
		// if(baseSubjectModel.publishUserId.equals(PreferencesUtil.getLoggedUserId())){
		// nameTv.setText(PreferencesUtil.getLoggedUserName());
		// }else{
		// }
		nameTv.setText(baseSubjectModel.publishUserName);
		// 设置时间
		desTv.setText(baseSubjectModel.publishTime);
		// 设置主题标题
		titleTv.setText(baseSubjectModel.subjectTitle);
		// 设置主题文本内容
		// contentTv.setText(baseSubjectModel.subjectContent);
		String content = baseSubjectModel.subjectContent;

		if ("qz_ccsobey_placeholder".equals(content)) {
			contentTv.setText("");
		} else {
			if (!TextUtils.isEmpty(content) && content.contains("\n")) {
				content = content.replace("\n", "<br/>");
			}
			try {
				contentTv.setText(Html.fromHtml(content, new MyImgGetter(), null));
			} catch (Exception e) {
				contentTv.setText(content);
			}
		}
		// 设置主题点赞相关
		buildLikeViews(likeContainer, mLikedUserList);
		// 设置主题图片内容
		buildSujectImgViews(iamgeContainer, urls);

		return view;
	}

	// 向图片容器中添加图片
	private void buildSujectImgViews(LinearLayout iamgeContainer, final String[] urls) {
		iamgeContainer.removeAllViews();
		if (null != urls && urls.length > 0) {
			for (int i = 0; i < urls.length; i++) {
				View itemView = mInflater.inflate(R.layout.item_group_subject_detail_listview_1_image, null);
				ImageView image = (ImageView) itemView.findViewById(R.id.item_group_subject_detail_img);
				Display.setLayoutMargin(image, 0, 0, 0, BaseUtil.Dp2Px(mContext, 10));
				MyClickListener clickListener = new MyClickListener();
				clickListener.setDetailPhotoViewData(i, urls);
				image.setOnClickListener(clickListener);
				ImageLoader.getInstance().displayImage(urls[i], image, imageOptions, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

					}

					@Override
					public void onLoadingComplete(String arg0, View view, Bitmap bitmap) {
						int height = bitmap.getHeight();
						int width = bitmap.getWidth();
						int sWidth = Display.ScreenWidth - BaseUtil.Dp2Px(mContext, 20);
						double rat = ((double) sWidth) / width;
						int sHeight = (int) (height * rat);
						if (width < 200) {
							Display.setLayoutParams(view, sWidth / 2, sHeight / 2);
						} else {
							Display.setLayoutParams(view, sWidth, sHeight);
						}
						Log.i(TAG, "onLoadingComplete--> bitmap heigth:" + height + ",width:" + width);
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {

					}
				});
				iamgeContainer.addView(itemView, i);
			}
			iamgeContainer.invalidate();
		}

	}

	private void jump2PhotoDetailActivity(int currentItem, String[] imageUrls) {
		Intent intent = new Intent(mContext, PhotoDetailViewPagerActivity.class);
		intent.putExtra("currentItem", currentItem);
		intent.putExtra("imageUrls", imageUrls);
		mContext.startActivity(intent);

	}

	// 构建点赞相关视图
	private void buildLikeViews(LinearLayout likeContainer, List<GroupUserModel> likedUserList) {

		final List<GroupUserModel> userModels = likedUserList;
		StringBuilder sb = new StringBuilder();

		TextView tv1 = new TextView(mContext);
		TextView tv2 = new TextView(mContext);
		TextView tv3 = new TextView(mContext);

		tv1.setTextSize(12);
		tv2.setTextSize(12);
		tv3.setTextSize(12);

		tv1.setTextColor(mContext.getResources().getColor(R.color.blue_for_group_like_username));
		tv2.setTextColor(mContext.getResources().getColor(R.color.blue_for_group_like_username));
		tv3.setTextColor(mContext.getResources().getColor(R.color.grey_group_for_des));

		if (userModels.size() > 0) {

			likeContainer.setVisibility(View.VISIBLE);

			if (userModels.size() >= 2) {

				sb.append("  等").append(baseSubjectModel.subjectLikeCount).append("人赞过");
				tv3.setText(sb.toString());

				tv1.setText(userModels.get(0).userName);
				tv2.setText("、" + userModels.get(1).userName);

				likeContainer.addView(tv1);
				likeContainer.addView(tv2);
				likeContainer.addView(tv3);

			} else {
				sb.append("  赞过");
				tv3.setText(sb.toString());

				if (userModels.size() == 1) {
					tv1.setText(userModels.get(0).userName);
				}

				likeContainer.addView(tv1);
				likeContainer.addView(tv3);

			}
		} else {
			likeContainer.setVisibility(View.GONE);
		}

		likeContainer.invalidate();

		tv1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump2PersonalInfoActivity(userModels.get(0));// 点名字不在跳转个人页面
			}
		});
		tv2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump2PersonalInfoActivity(userModels.get(1));// 点名字不在跳转个人页面
			}
		});

	}

	// 第x楼 6-1 14:00
	private String getDes(int floor, String date) {
		int mFoolr = floor;
		if (mCurrentPage >= 1) {
			mFoolr = (mCurrentPage - 1) * mPageSize + floor;
		}
		String pattern1 = "yyyy-MM-dd HH:mm:ss";
		String pattern2 = "MM-dd HH:mm";
		String convertDate = "";
		SimpleDateFormat dateFormat1 = new SimpleDateFormat(pattern1);
		SimpleDateFormat dateFormat2 = new SimpleDateFormat(pattern2);
		Date d = null;
		try {
			d = dateFormat1.parse(date);
		} catch (ParseException e) {
			convertDate = date;
		}
		if (null != d)
			convertDate = dateFormat2.format(d);
		StringBuilder sb = new StringBuilder();
		sb.append("第").append(mFoolr).append("楼").append(" ").append(convertDate);
		return sb.toString();
	}

	private void jump2PersonalInfoActivity(GroupUserModel userModel) {
		Intent intent = new Intent(mContext, FriendCenterActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("mUserName", userModel.userName);
		bundle.putString("mUid", userModel.uid);
		bundle.putString("mHeadUrl", userModel.userHeadUrl);
		intent.putExtra("userInfos", bundle);
		mContext.startActivity(intent);
	}

	private class ViewHolder {
		AdvancedImageView headIv;
		TextView nameTv;
		TextView replyTv;
		TextView contentTv;
		TextView likeTv;
		TextView desTv;
		ImageView likeIv;
		TextView CommentreplyContentTv;
		// TextView countTv;
		// 评论的回复 如果没有回复 此视图不可以见
		LinearLayout commentReplyLayout;
		// 评论回复 条目layout 用于add每个条目
		LinearLayout commentReplyContainer;
		// 图片容器
		LinearLayout commentImgContainer;

	}

	private class MyClickListener implements View.OnClickListener {
		GroupCommentModel mGroupCommentModel;
		private int mFloor;
		private int currentItem;
		private String[] imageUrls;

		public MyClickListener() {

		}

		public MyClickListener(GroupCommentModel groupCommentModel, int floor) {
			this.mGroupCommentModel = groupCommentModel;
			this.mFloor = floor;
		}

		public void setDetailPhotoViewData(int currentItem, String[] imageUrls) {
			this.currentItem = currentItem;
			this.imageUrls = imageUrls;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.item_subject_detail_reply_num_tv:

				// 登录校验
				if (TextUtils.isEmpty(PreferencesUtil.getLoggedUserId())) {
					Intent intent = new Intent(mContext, LoginActivity.class);
					mContext.startActivity(intent);
					return;
				}

				mGroupCommentModel.floor = mFloor;
				Message message = new Message();
				message.what = SobeyConstants.CODE_FOR_COMMENT_REPLY_CLICKED;
				message.obj = mGroupCommentModel;
				mHandler.sendMessage(message);

				break;
			case R.id.item_subject_detail_reply_icon_tv:

				// 登录校验
				if (TextUtils.isEmpty(PreferencesUtil.getLoggedUserId())) {
					Intent intent = new Intent(mContext, LoginActivity.class);
					mContext.startActivity(intent);
					return;
				}

				boolean isLiked = PreferencesUtil
						.getBoolean(PreferencesUtil.KEY_COMMENT_IS_LIKED + mGroupCommentModel.commentId);
				int count = PreferencesUtil
						.getInt(PreferencesUtil.KEY_COMMENT_LIKE_COUNT + mGroupCommentModel.commentId);
				int like = 0;
				// TextView tv = (TextView) v;
				if (isLiked) {
					// 已点赞 置换为未点赞的状态
					count--;
					if (count < 1)
						count = 0;

					like = 0;
					((GroupSubjectActivity) mContext).praiseAnimAction("-1");
				} else {
					count++;
					like = 1;
					((GroupSubjectActivity) mContext).praiseAnimAction("+1");
				}
				//
				PreferencesUtil.putBoolean(PreferencesUtil.KEY_COMMENT_IS_LIKED + mGroupCommentModel.commentId,
						!isLiked);
				PreferencesUtil.putInt(PreferencesUtil.KEY_COMMENT_LIKE_COUNT + mGroupCommentModel.commentId, count);
				notifyDataSetChanged();

				likeComment(like, mGroupCommentModel);

				break;
			case R.id.item_subject_detail_iv:
			case R.id.item_subject_detail_name_tv:
				GroupUserModel userModel = new GroupUserModel();
				if (v.getTag() != null && "louzhu".equals(v.getTag().toString())) {
					userModel.uid = baseSubjectModel.publishUserId;
					userModel.userName = baseSubjectModel.publishUserName;
					userModel.userHeadUrl = baseSubjectModel.publishUserHeadUrl;
				} else {
					userModel.uid = mGroupCommentModel.commentUserId;
					userModel.userName = mGroupCommentModel.commentUserName;
					userModel.userHeadUrl = mGroupCommentModel.CommentUserHeadUrl;

				}
				jump2PersonalInfoActivity(userModel);
				break;
			case R.id.item_group_subject_detail_img:
				jump2PhotoDetailActivity(currentItem, imageUrls);
				break;
			case R.id.item_subject_detail_go_group_btn:
				// baseSubjectModel.groupId;
				GroupModel groupModel = new GroupModel();
				groupModel.groupId = baseSubjectModel.groupId;
				groupModel.groupName = baseSubjectModel.groupName;
				groupModel.groupInfo = baseSubjectModel.groupInfo;
				groupModel.headUrl = baseSubjectModel.groupHeadUrl;
				Intent intent = new Intent(mContext, GroupDetailActivity.class);
				intent.putExtra("mGroupModel", groupModel);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				mContext.startActivity(intent);
				((GroupSubjectActivity) mContext).finishActivity();
				break;

			default:
				break;
			}
		}

		private void likeComment(final int like, final GroupCommentModel mCommentModel) {

			GroupRequestMananger.getInstance().likeComment(mCommentModel.commentId, like, mContext,
					new RequestResultListner() {

						@Override
						public void onFinish(SobeyType result) {

							// boolean islike = false;
							// int count = PreferencesUtil
							// .getInt(PreferencesUtil.KEY_COMMENT_LIKE_COUNT+mGroupCommentModel.commentId);
							// //FIXME 请求成功后在share中放入值
							// //请求成功后在share中放入值
							//
							// if(result instanceof SobeyBaseResult){
							// SobeyBaseResult baseResult = (SobeyBaseResult)
							// result;
							// if(baseResult.returnCode == SobeyBaseResult.OK){
							// if(like == 1){
							// islike = true;
							// count += 1;
							// }else{
							// count -= 1;
							// if(count <=0 ){
							// count = 0;
							// }
							// }
							// //请求成功后在share中放入值
							// PreferencesUtil
							// .putBoolean(PreferencesUtil.KEY_COMMENT_IS_LIKED+mCommentModel.commentId,
							// islike);
							// PreferencesUtil
							// .putInt(PreferencesUtil.KEY_COMMENT_LIKE_COUNT+mCommentModel.commentId,
							// count);
							// notifyDataSetChanged();
							// }else{
							// Toast.makeText(mContext,
							// R.string.post_like_comment_fail,
							// Toast.LENGTH_SHORT).show();
							// }
							// }else{
							// Toast.makeText(mContext,
							// R.string.post_like_comment_fail,
							// Toast.LENGTH_SHORT).show();
							// }
						}
					});
		}

	}

	private class MyImgGetter implements Html.ImageGetter {

		@Override
		public Drawable getDrawable(String source) {
			// static/image/smiley/default/call.gif
			Drawable drawable = null;
			String face = source.substring(source.lastIndexOf("/") + 1, source.lastIndexOf("."));
			if (source.startsWith("http")) {
				String fileName = BaseUtil.getFileName(source);
				String fullPath = BaseUtil.getFilePath() + fileName;
				File file = new File(fullPath);
				if (BaseUtil.isFileExist(fullPath)) {
					// Toast.makeText(this, "该图片已下载!",
					// Toast.LENGTH_SHORT).show();
					drawable = Drawable.createFromPath(file.getAbsolutePath());
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
					return drawable;
				} else {
					Log.i(TAG, "下载图片..source.." + source);
					toSavePhoto(source, fileName);
					return mContext.getResources().getDrawable(R.drawable.default_thumbnail_banner);
				}
			}
			Log.i(TAG, "face:" + face);
			if (source.contains("default")) {
				drawable = mContext.getResources().getDrawable(FaceUtil.defaultFaces.get(face));
			} else if (source.contains("coolmonkey")) {
				drawable = mContext.getResources().getDrawable(FaceUtil.coolmonkeyFaces.get(face));
			} else if (source.contains("grapeman")) {
				drawable = mContext.getResources().getDrawable(FaceUtil.grapemanFaces.get(face));
			}
			if (null != drawable) {
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			}
			return drawable;
		}

	}

	private void toSavePhoto(final String path, final String fileName) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					InputStream inputStream = BaseUtil.getImageStream(path);
					File file = BaseUtil.createSavePhotoFile(fileName);
					Message msg = new Message();
					msg.what = 0;
					msg.obj = BaseUtil.downLoadFile(inputStream, file);
					myHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 下载图片完成后刷新界面
			if (msg.what == 0) {
				notifyDataSetChanged();
			}
		};
	};
	// private void generateData(){
	// for(int i=0 ;i<20;i++){
	// Myobj obj = new Myobj();
	// obj.name = "axx"+i;
	// if(i==0){
	// obj.type = 0;
	// }else{
	// obj.type = 1;
	// }
	// if(i==3 || i==5 ||i==9){
	// obj.hasComment = true;
	// }
	// lists.add(obj);
	// }
	// }
	// private class Myobj{
	// String name;
	// int type;
	// boolean hasComment;
	// }

}
