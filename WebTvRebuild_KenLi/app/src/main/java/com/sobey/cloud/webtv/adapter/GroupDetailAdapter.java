package com.sobey.cloud.webtv.adapter;

import java.util.ArrayList;
import java.util.List;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.FriendCenterActivity;
import com.sobey.cloud.webtv.bean.GroupModel;
import com.sobey.cloud.webtv.bean.GroupRequestMananger;
import com.sobey.cloud.webtv.bean.GroupRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.bean.GroupUserModel;
import com.sobey.cloud.webtv.bean.SobeyBaseResult;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.utils.FaceUtil;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.ToastUtil;
import com.sobey.cloud.webtv.views.group.GroupPersonalInfoActivity;
import com.sobey.cloud.webtv.views.group.GroupSubjectActivity;
import com.sobey.cloud.webtv.views.group.PhotoDetailViewPagerActivity;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class GroupDetailAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private GroupModel mGroupModel;
	private GroupModel baseGroupModel;
	private Context mContext;
	private List<GroupSubjectModel> groupSubjectModels;
	private final String TAG = this.getClass().getName();
	private DisplayImageOptions imageOptions;
	private DisplayImageOptions imageHeadOptions;

	public GroupDetailAdapter(Context ctx) {
		this.mInflater = LayoutInflater.from(ctx);
		this.mContext = ctx;
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.showImageForEmptyUri(R.drawable.default_thumbnail_banner)
				.showImageOnLoading(R.drawable.default_thumbnail_banner);
		imageOptions = builder.build();
		DisplayImageOptions.Builder builder2 = new DisplayImageOptions.Builder();
		builder2.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.showImageForEmptyUri(R.drawable.default_thumbnail_banner).showImageOnLoading(R.drawable.default_head);
		imageHeadOptions = builder2.build();

	}

	public void setData(List<GroupSubjectModel> groupSubjectModels) {
		this.groupSubjectModels = groupSubjectModels;
	}

	public void setData(GroupModel groupModel, GroupModel baseGroupModel) {
		this.mGroupModel = groupModel;
		this.baseGroupModel = baseGroupModel;
		groupSubjectModels = mGroupModel.subjectList;
		// Collections.reverse(groupSubjectModels);
		GroupSubjectModel subjectModel = new GroupSubjectModel();
		subjectModel.type = 0;
		groupSubjectModels.add(0, subjectModel);
	}

	// 请求分页数据后添加数据
	public void addData(GroupModel groupModel) {
		if (null != groupModel) {
			groupSubjectModels.addAll(groupModel.subjectList);
			notifyDataSetChanged();
		}
	}

	public void clearData() {
		if (null != groupSubjectModels) {
			groupSubjectModels.clear();
		}

	}

	@Override
	public int getCount() {
		return groupSubjectModels.size();
	}

	@Override
	public GroupSubjectModel getItem(int position) {
		return groupSubjectModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Log.i("GroupDetailAdapter",
		// "childcount:"+parent.getChildCount()+",position:"+position);
		GroupSubjectModel subjectModel = (GroupSubjectModel) getItem(position);
		if (subjectModel.type == 0) {
			View v = buildTopSujectViews();
			return v;
		}
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_group_detail_listview_subject, null);
			holder.nameTv = (TextView) convertView.findViewById(R.id.item_subject_name_tv);
			holder.headIv = (AdvancedImageView) convertView.findViewById(R.id.item_subject_iv);
			holder.desTv = (TextView) convertView.findViewById(R.id.item_subject_des_tv);
			holder.replyNumTv = (TextView) convertView.findViewById(R.id.item_subject_reply);
			holder.likeNumTv = (TextView) convertView.findViewById(R.id.item_subject_like);
			holder.titleTv = (TextView) convertView.findViewById(R.id.item_subject_title_tv);
			holder.contentTv = (TextView) convertView.findViewById(R.id.item_subject_content_tv);
			holder.img1 = (ImageView) convertView.findViewById(R.id.item_subject_content_img1);
			holder.img2 = (ImageView) convertView.findViewById(R.id.item_subject_content_img2);
			holder.img3 = (ImageView) convertView.findViewById(R.id.item_subject_content_img3);
			holder.imgContentLayout = convertView.findViewById(R.id.item_subject_content_img_layout);
			holder.jblayout = (LinearLayout) convertView.findViewById(R.id.item_subject_jblayout);
			holder.essenceTv = (TextView) convertView.findViewById(R.id.essence_tv);
			holder.topTv = (TextView) convertView.findViewById(R.id.top_tv);
			convertView.setTag(holder);
		} else {
			// 由于type=0的时候也会产生convertView 此时并未设置holder
			holder = (ViewHolder) convertView.getTag();
			if (holder == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_group_detail_listview_subject, null);
				holder.nameTv = (TextView) convertView.findViewById(R.id.item_subject_name_tv);
				holder.headIv = (AdvancedImageView) convertView.findViewById(R.id.item_subject_iv);
				holder.desTv = (TextView) convertView.findViewById(R.id.item_subject_des_tv);
				holder.replyNumTv = (TextView) convertView.findViewById(R.id.item_subject_reply);
				holder.likeNumTv = (TextView) convertView.findViewById(R.id.item_subject_like);
				holder.titleTv = (TextView) convertView.findViewById(R.id.item_subject_title_tv);
				holder.contentTv = (TextView) convertView.findViewById(R.id.item_subject_content_tv);
				holder.img1 = (ImageView) convertView.findViewById(R.id.item_subject_content_img1);
				holder.img2 = (ImageView) convertView.findViewById(R.id.item_subject_content_img2);
				holder.img3 = (ImageView) convertView.findViewById(R.id.item_subject_content_img3);
				holder.imgContentLayout = convertView.findViewById(R.id.item_subject_content_img_layout);
				holder.jblayout = (LinearLayout) convertView.findViewById(R.id.item_subject_jblayout);
				holder.essenceTv = (TextView) convertView.findViewById(R.id.essence_tv);
				holder.topTv = (TextView) convertView.findViewById(R.id.top_tv);
			}
		}
		int replyCount = parseString2Integer(subjectModel.subjectReplyCount);
		int likeCount = parseString2Integer(subjectModel.subjectLikeCount);

		String[] urls = subjectModel.subjectPicUrls;

		buildContentPicViews(urls, holder);

		subjectModel.publishUserHeadUrl = subjectModel.publishUserHeadUrl.replace("http://qz.sobeycache.com", "");
		ImageLoader.getInstance().displayImage(subjectModel.publishUserHeadUrl, holder.headIv, imageHeadOptions);
		holder.nameTv.setText(subjectModel.publishUserName);
		holder.titleTv.setText(subjectModel.subjectTitle);
		holder.desTv.setText(subjectModel.publishTime);
		// if(subjectModel.publishUserId.equals(PreferencesUtil.getLoggedUserId())){
		// holder.nameTv.setText(PreferencesUtil.getLoggedUserName());
		// }else{
		// }

		String content = subjectModel.subjectContent;

		if ("qz_ccsobey_placeholder".equals(content)) {
			holder.contentTv.setText("");
		} else {
			if (!TextUtils.isEmpty(content) && content.contains("\n")) {
				content = content.replace("\n", "<br/>");
			}

			try {
				holder.contentTv.setText(Html.fromHtml(content, new MyImgGetter(), null));
			} catch (Exception e) {
				holder.contentTv.setText(content);
			}
		}

		// Html.fromHtml(content, new MyImgGetter(), null)
		MyViewOnClickListener clickListener = new MyViewOnClickListener();

		holder.nameTv.setOnClickListener(clickListener);
		holder.headIv.setOnClickListener(clickListener);
		holder.jblayout.setOnClickListener(clickListener);
		convertView.setOnClickListener(clickListener);
		clickListener.setSubjectModel(subjectModel);

		if (replyCount > 0) {
			holder.replyNumTv.setText("" + replyCount);
		} else {
			holder.replyNumTv.setText("0");
		}

		if (likeCount > 0) {
			holder.likeNumTv.setText("" + likeCount);
		} else {
			holder.likeNumTv.setText("0");
		}

		holder.topTv.setVisibility(subjectModel.type == 2 ? View.VISIBLE : View.GONE);
		holder.essenceTv.setVisibility(subjectModel.digest == 1 ? View.VISIBLE : View.GONE);

		return convertView;
	}

	@SuppressLint("InflateParams")
	public View buildTopSujectViews() {
		List<GroupSubjectModel> topicSubjectList = mGroupModel.topicSubjectList;

		View view = mInflater.inflate(R.layout.item_group_detail_listview_1, null);
		ImageView headImg = (ImageView) view.findViewById(R.id.item_group_detail_listview_1_iv);
		TextView nameTv = (TextView) view.findViewById(R.id.item_group_detail_listview_1_group_name_tv);
		TextView desTv = (TextView) view.findViewById(R.id.item_group_detail_listview_1_group_des_tv);
		TextView followTv = (TextView) view.findViewById(R.id.item_group_detail_listview_1_follow_tv);
		TextView fatieNumTV = (TextView) view.findViewById(R.id.item_group_detail_listview_1_group_fatienum_tv);
		LinearLayout containerView = (LinearLayout) view
				.findViewById(R.id.item_group_detail_top_subject_list_container);

		ImageLoader.getInstance().displayImage(baseGroupModel.headUrl, headImg, imageOptions);
		nameTv.setText(baseGroupModel.groupName);
		desTv.setText(baseGroupModel.groupInfo);
		if (TextUtils.isEmpty(baseGroupModel.totalSubjectCount)) {
			baseGroupModel.totalSubjectCount = "0";
		}
		fatieNumTV.setText("发帖:" + baseGroupModel.totalSubjectCount);
		if (baseGroupModel.isFollowed == 0) {
			followTv.setText("关注");
		} else {
			followTv.setText("取消关注");
		}

		containerView.removeAllViews();

		for (int i = 0; i < topicSubjectList.size(); i++) {
			final GroupSubjectModel groupSubjectModel = topicSubjectList.get(i);
			LinearLayout itemView = (LinearLayout) mInflater.inflate(R.layout.item_group_detail_listview_2, null);
			TextView tvTitle = (TextView) itemView.findViewById(R.id.item_top_subject_title);
			tvTitle.setText(groupSubjectModel.subjectTitle);
			itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					jump2SubjectActivity(groupSubjectModel);
				}
			});
			containerView.addView(itemView, i);
		}
		containerView.invalidate();

		followTv.setOnClickListener(new MyViewOnClickListener());

		return view;
	}

	private void buildContentPicViews(String[] urls, ViewHolder holder) {
		if (null != urls && urls.length > 0) {
			holder.imgContentLayout.setVisibility(View.VISIBLE);
			if (urls.length < 2) {
				ImageLoader.getInstance().displayImage(urls[0], holder.img1, imageOptions);
				holder.img1.setVisibility(View.VISIBLE);
				holder.img2.setVisibility(View.INVISIBLE);
				holder.img3.setVisibility(View.INVISIBLE);
			} else if (urls.length < 3) {
				ImageLoader.getInstance().displayImage(urls[0], holder.img1, imageOptions);
				ImageLoader.getInstance().displayImage(urls[1], holder.img2, imageOptions);
				holder.img1.setVisibility(View.VISIBLE);
				holder.img2.setVisibility(View.VISIBLE);
				holder.img3.setVisibility(View.INVISIBLE);
			} else {
				ImageLoader.getInstance().displayImage(urls[0], holder.img1, imageOptions);
				ImageLoader.getInstance().displayImage(urls[1], holder.img2, imageOptions);
				ImageLoader.getInstance().displayImage(urls[2], holder.img3, imageOptions);
				holder.img1.setVisibility(View.VISIBLE);
				holder.img2.setVisibility(View.VISIBLE);
				holder.img3.setVisibility(View.VISIBLE);
			}
			holder.img1.setOnClickListener(new MyViewOnClickListener(0, urls));
			holder.img2.setOnClickListener(new MyViewOnClickListener(1, urls));
			holder.img3.setOnClickListener(new MyViewOnClickListener(2, urls));
		} else {
			holder.imgContentLayout.setVisibility(View.GONE);
		}
	}

	private int parseString2Integer(String count) {
		try {
			return Integer.parseInt(count);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private void jump2SubjectActivity(GroupSubjectModel subjectModel) {
		if (null == subjectModel) {
			return;
		}
		Intent intent = new Intent(mContext, GroupSubjectActivity.class);
		Bundle bundle = new Bundle();
		subjectModel.groupId = baseGroupModel.groupId;
		subjectModel.groupHeadUrl = baseGroupModel.headUrl;
		subjectModel.groupInfo = baseGroupModel.groupInfo;
		subjectModel.groupName = baseGroupModel.groupName;
		bundle.putString("title", baseGroupModel.groupName);
		bundle.putParcelable("mSubjectModel", subjectModel);
		intent.putExtras(bundle);
		mContext.startActivity(intent);
	}

	private void jump2LoginActivity() {
		Intent intent = new Intent(mContext, LoginActivity.class);
		mContext.startActivity(intent);
	}

	private class ViewHolder {
		AdvancedImageView headIv;
		TextView nameTv;
		// TextView nameTv2;
		// TextView followTv;
		TextView desTv;
		// TextView countTv;
		TextView likeNumTv;
		TextView replyNumTv;
		TextView titleTv;
		TextView contentTv;
		ImageView img1;
		ImageView img2;
		ImageView img3;
		View imgContentLayout;
		LinearLayout jblayout;
		TextView essenceTv;
		TextView topTv;

	}

	private class MyViewOnClickListener implements View.OnClickListener {

		private GroupSubjectModel mSubjectModel;
		private int currentItem;
		private String[] imageUrls;

		private void setSubjectModel(GroupSubjectModel subjectModel) {
			this.mSubjectModel = subjectModel;
		}

		public MyViewOnClickListener() {

		}

		public MyViewOnClickListener(int currentItem, String[] imageUrls) {
			this.currentItem = currentItem;
			this.imageUrls = imageUrls;
		}

		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.item_group_detail_listview_1_follow_tv:
				// 点击关注按钮表示关注状态发生变化 回到圈子列表页面需要刷新列表
				// 未登录跳转到登录页面
				if (TextUtils.isEmpty(PreferencesUtil.getLoggedUserId())) {
					Intent intent = new Intent(mContext, LoginActivity.class);
					mContext.startActivity(intent);
					return;
				}
				PreferencesUtil.putBoolean(PreferencesUtil.KEY_FOLLOW_STATE_CHANGED, true);
				if (baseGroupModel.isFollowed == 0) {
					baseGroupModel.isFollowed = 1;
					// ((TextView) v).setText("取消关注");
					GroupRequestMananger.getInstance().followGroup(baseGroupModel.isFollowed, baseGroupModel.groupId,
							mContext, new RequestResultListner() {

								@Override
								public void onFinish(SobeyType result) {
									if (result instanceof SobeyBaseResult) {
										SobeyBaseResult baseResult = (SobeyBaseResult) result;
										if (baseResult.returnCode != SobeyBaseResult.OK) {
											Log.i(TAG, "取消关注失败 returnCode:" + baseResult.returnCode);
											baseGroupModel.isFollowed = 0;
											((TextView) v).setText("关注");
										} else {
											baseGroupModel.isFollowed = 1;
											((TextView) v).setText("取消关注");
										}
									} else {
										Log.i(TAG, "取消关注失败 xxx");
										baseGroupModel.isFollowed = 0;
										((TextView) v).setText("关注");
									}
								}
							});
				} else if (baseGroupModel.isFollowed == 1) {
					baseGroupModel.isFollowed = 0;
					// ((TextView) v).setText("关注");
					GroupRequestMananger.getInstance().followGroup(baseGroupModel.isFollowed, baseGroupModel.groupId,
							mContext, new RequestResultListner() {

								@Override
								public void onFinish(SobeyType result) {
									if (result instanceof SobeyBaseResult) {
										SobeyBaseResult baseResult = (SobeyBaseResult) result;
										if (baseResult.returnCode != SobeyBaseResult.OK) {
											Log.i(TAG, "关注失败 returnCode:" + baseResult.returnCode);
											baseGroupModel.isFollowed = 1;
											((TextView) v).setText("取消关注");
										} else {
											baseGroupModel.isFollowed = 0;
											((TextView) v).setText("关注");
										}
									} else {
										Log.i(TAG, "关注失败 xxx");
										baseGroupModel.isFollowed = 1;
										((TextView) v).setText("取消关注");
									}
								}
							});
				}
				break;
			case R.id.item_subject_iv:
			case R.id.item_subject_name_tv:
				jump2PersonalInfoActivity();
				break;
			case R.id.item_subject_layout:
				jump2SubjectActivity(mSubjectModel);
				break;
			case R.id.item_subject_content_img1:
			case R.id.item_subject_content_img2:
			case R.id.item_subject_content_img3:
				jump2PhotoDetailActivity(currentItem, imageUrls);
				break;
			case R.id.item_subject_jblayout:
				showReportDialog();
				break;
			}
		}

		private void showReportDialog() {
			AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示").setMessage("是否举报？")
					.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							ToastUtil.showToast(mContext, "已举报");
						}
					}).create();
			alertDialog.show();
		}

		private void jump2PhotoDetailActivity(int currentItem, String[] imageUrls) {
			Intent intent = new Intent(mContext, PhotoDetailViewPagerActivity.class);
			intent.putExtra("currentItem", currentItem);
			intent.putExtra("imageUrls", imageUrls);
			mContext.startActivity(intent);
		}

		private void jump2PersonalInfoActivity() {

			if (null == mSubjectModel) {
				return;
			}

			if (mSubjectModel.type == 0) {
				return;
			} else {
				Intent intent = new Intent(mContext, FriendCenterActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("mUserName", mSubjectModel.publishUserName);
				bundle.putString("mUid", mSubjectModel.publishUserId);
				bundle.putString("mHeadUrl", mSubjectModel.publishUserHeadUrl);
				intent.putExtra("userInfos", bundle);
				mContext.startActivity(intent);
			}
		}

		private void jump2GroupSubjectActivity(GroupSubjectModel subjectModel) {
			Intent intent = new Intent(mContext, GroupSubjectActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("title", mGroupModel.groupName);
			bundle.putParcelable("mSubjectModel", subjectModel);
			intent.putExtras(bundle);
			mContext.startActivity(intent);
		}

	}

	private class MyImgGetter implements Html.ImageGetter {

		@Override
		public Drawable getDrawable(String source) {
			// static/image/smiley/default/call.gif
			Drawable drawable = null;
			String face = source.substring(source.lastIndexOf("/") + 1, source.lastIndexOf("."));
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

}
