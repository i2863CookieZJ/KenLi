package com.sobey.cloud.webtv.adapter;

import java.util.ArrayList;
import java.util.List;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.GetGroupListResult;
import com.sobey.cloud.webtv.bean.GroupModel;
import com.sobey.cloud.webtv.bean.GroupRequestMananger;
import com.sobey.cloud.webtv.bean.GroupRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.bean.SobeyBaseResult;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.utils.BaseUtil;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<GroupModel> mGroups;
	private List<GroupModel> followedGroupList;
	private List<GroupModel> otherGroupList;
	private LayoutParams followedGroupIvParams;
	private LayoutParams unFollowedGroupIvParams;
	private Context mContext;
	private int followedGroupSize;
	private DisplayImageOptions imageOptions;
	private final String TAG = this.getClass().getName();

	public GroupAdapter(Context ctx) {
		this.mInflater = LayoutInflater.from(ctx);
		followedGroupIvParams = new LayoutParams(BaseUtil.Dp2Px(ctx, 40), BaseUtil.Dp2Px(ctx, 40));
		followedGroupIvParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		unFollowedGroupIvParams = new LayoutParams(BaseUtil.Dp2Px(ctx, 55), BaseUtil.Dp2Px(ctx, 55));
		unFollowedGroupIvParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		this.mContext = ctx;
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.showImageForEmptyUri(R.drawable.default_thumbnail_banner)
				.showImageOnLoading(R.drawable.default_thumbnail_banner);
		imageOptions = builder.build();
	}

	public void setData(GetGroupListResult groupList) {
		buildData(groupList);
	}

	public void setData(List<GroupModel> groupModels) {
		this.mGroups = groupModels;
	}

	@Override
	public int getCount() {
		return mGroups.size();
	}

	@Override
	public GroupModel getItem(int position) {
		return mGroups.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		GroupModel groupModel = getItem(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_group_listview, null);
			GinInjector.manualInjectView(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 如果是已关注列表的最后项 显示底部阴影
		if (followedGroupSize != 0 && followedGroupSize == position + 1) {
			holder.shadowIv.setVisibility(View.VISIBLE);
			if (position == getCount() - 1) {// 如果全部关注了就不显示底部“推荐关注”
				holder.shadowIv.setVisibility(View.GONE);
			}
		} else {
			holder.shadowIv.setVisibility(View.GONE);
		}
		if (groupModel.isFollowed == 1) {
			holder.headIv.setRondRadius(90);
			holder.headIv.setLayoutParams(followedGroupIvParams);
			holder.nameTv2.setVisibility(View.VISIBLE);
			holder.nameTv.setVisibility(View.VISIBLE);
			holder.desTv.setVisibility(View.VISIBLE);
			holder.followTv.setVisibility(View.GONE);
			holder.countTv.setVisibility(View.VISIBLE);
		} else {
			holder.headIv.setRondRadius(6);
			holder.headIv.setLayoutParams(unFollowedGroupIvParams);
			holder.desTv.setVisibility(View.VISIBLE);
			holder.nameTv.setVisibility(View.VISIBLE);
			holder.nameTv2.setVisibility(View.VISIBLE);
			holder.followTv.setVisibility(View.VISIBLE);
			holder.countTv.setVisibility(View.GONE);
			if (groupModel.isFollowed == -1) {
				holder.followTv.setVisibility(View.GONE);
			}
		}
		holder.nameTv.setText(groupModel.groupName);
		holder.nameTv2.setText(groupModel.groupInfo);
		if (TextUtils.isEmpty(groupModel.totalSubjectCount)) {
			groupModel.totalSubjectCount = "0";
		}
		holder.desTv.setText("发帖:" + groupModel.totalSubjectCount);

		holder.countTv.setVisibility(View.GONE);

		// int newSubjectCount = groupModel.newSubjectCount;
		// if(newSubjectCount < 1){
		// holder.countTv.setVisibility(View.GONE);
		// }else{
		// if(newSubjectCount > 9){
		// holder.countTv.setText("n");
		// }
		// if(groupModel.isFollowed == 1){
		// holder.countTv.setVisibility(View.VISIBLE);
		// }
		// }
		holder.countTv.setText(String.valueOf(groupModel.newSubjectCount));

		ImageLoader.getInstance().displayImage(groupModel.headUrl, holder.headIv, imageOptions);

		MyClickListener clickListener = new MyClickListener(groupModel);
		holder.followTv.setOnClickListener(clickListener);

		return convertView;
	}

	private void buildData(GetGroupListResult groupList) {
		mGroups = new ArrayList<GroupModel>();
		followedGroupList = groupList.followedGroupList;
		otherGroupList = groupList.otherGroupList;

		if (null != followedGroupList && !followedGroupList.isEmpty()) {
			mGroups.addAll(followedGroupList);
		}
		followedGroupSize = followedGroupList.size();
		// else{
		// followedGroupList = new ArrayList<GroupModel>();
		// for(int i=0;i<5;i++){
		// GroupModel groupModel = new GroupModel();
		// groupModel.isFollowed = 1;
		// groupModel.groupName = "已关注_"+i;
		// followedGroupList.add(groupModel);
		// }
		// followedGroupSize = followedGroupList.size();
		// mGroups.addAll(followedGroupList);
		// }
		if (null != otherGroupList && !otherGroupList.isEmpty())
			mGroups.addAll(otherGroupList);
	}

	private class ViewHolder {
		@GinInjectView(id = R.id.item_group_listview_iv)
		AdvancedImageView headIv;
		@GinInjectView(id = R.id.item_group_listview_name_tv)
		TextView nameTv;
		@GinInjectView(id = R.id.item_group_listview_name_tv2)
		TextView nameTv2;
		@GinInjectView(id = R.id.item_group_listview_follow_tv)
		TextView followTv;
		@GinInjectView(id = R.id.item_group_listview_des_tv)
		TextView desTv;
		@GinInjectView(id = R.id.item_group_listview_new_subject_count_tv)
		TextView countTv;
		@GinInjectView(id = R.id.item_group_listview_bottom_shadow)
		LinearLayout shadowIv;
	}

	private class MyClickListener implements View.OnClickListener {
		private GroupModel mGroupModel;

		MyClickListener(GroupModel groupModel) {
			this.mGroupModel = groupModel;
		}

		@Override
		public void onClick(View v) {

			String uid = PreferencesUtil.getLoggedUserId();
			if (TextUtils.isEmpty(uid)) {
				jump2LoginActivity();
				return;
			}

			mGroupModel.isFollowed = 1;
			GroupRequestMananger.getInstance().followGroup(mGroupModel.isFollowed, mGroupModel.groupId, mContext,
					new RequestResultListner() {

						@Override
						public void onFinish(SobeyType result) {
							if (result instanceof SobeyBaseResult) {
								SobeyBaseResult baseResult = (SobeyBaseResult) result;
								int code = baseResult.returnCode;
								Log.i(TAG, "关注圈子 code=" + code);
								if (code == SobeyBaseResult.OK) {
									mGroupModel.isFollowed = 1;
									followedGroupList.add(mGroupModel);
									followedGroupSize = followedGroupList.size();
									if (otherGroupList.contains(mGroupModel)) {
										otherGroupList.remove(mGroupModel);
									}
									if (null != mGroups) {
										mGroups.clear();
										mGroups.addAll(followedGroupList);
										mGroups.addAll(otherGroupList);
										notifyDataSetChanged();
									}
								} else {
									mGroupModel.isFollowed = 0;
								}
							}
						}
					});
		}
	}

	private void jump2LoginActivity() {
		Intent intent = new Intent(mContext, LoginActivity.class);
		mContext.startActivity(intent);
	}
}
