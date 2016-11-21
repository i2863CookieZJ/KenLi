package com.sobey.cloud.webtv.views.group;

import com.dylan.common.animation.AnimationController;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.GroupPersonalInfoAdapter;
import com.sobey.cloud.webtv.bean.GroupRequestMananger;
import com.sobey.cloud.webtv.bean.GroupRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.bean.GroupUserModel;
import com.sobey.cloud.webtv.bean.SobeyBaseResult;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.personal.PostPrivtateLetterActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GroupPersonalInfoActivity extends BaseActivity4Group {

	@GinInjectView(id = R.id.personal_info_head__listview)
	ListView mListView;
	@GinInjectView(id = R.id.personal_info_head_iv)
	ImageView headIv;
	@GinInjectView(id = R.id.personal_name_tv)
	TextView nameTv;
	@GinInjectView(id = R.id.title_layout)
	LinearLayout titleLayout;
	@GinInjectView(id = R.id.title_right)
	Button titleRight;
	@GinInjectView(id = R.id.personal_send_private_letter_btn)
	Button sendPrivateLetterBtn;
	@GinInjectView(id = R.id.personal_follow_user_cb)
	CheckBox followCb;

	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	GroupPersonalInfoAdapter mAdapter;
	private String mUserName;
	private String mUid;
	private String mHeadUrl;

	@Override
	public int getContentView() {
		return R.layout.activity_group_personal_info_layout;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		Bundle bundle = this.getIntent().getBundleExtra("userInfos");
		if (null != savedInstanceState) {
			bundle = savedInstanceState.getBundle("userInfos");
		}
		if (null != bundle) {
			mUserName = bundle.getString("mUserName");
			mUid = bundle.getString("mUid");
			mHeadUrl = bundle.getString("mHeadUrl");
		}
		setUpData();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public void setUpData() {
		mOpenLoadingIcon();

		ImageLoader.getInstance().displayImage(mHeadUrl, headIv);
		nameTv.setText(mUserName);
		titleLayout.setVisibility(View.GONE);
		titleRight.setVisibility(View.GONE);
		sendPrivateLetterBtn.setOnClickListener(this);
		loadData();

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GroupSubjectModel groupSubjectModel = (GroupSubjectModel) parent.getAdapter().getItem(position);
				Intent intent = new Intent(mContext, GroupSubjectActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("mSubjectModel", groupSubjectModel);
				bundle.putString("title", groupSubjectModel.groupName);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
			}

		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.title_left:
			this.finish();
			break;
		case R.id.personal_send_private_letter_btn:

			jump2PostPrivtateLetterActivity();

			break;
		default:
			break;
		}
	}

	private void jump2PostPrivtateLetterActivity() {
		GroupUserModel groupUserModel = new GroupUserModel();
		groupUserModel.uid = mUid;
		groupUserModel.userName = mUserName;
		groupUserModel.userHeadUrl = mHeadUrl;
		Intent intent = new Intent(mContext, PostPrivtateLetterActivity.class);
		intent.putExtra("groupUserModel", groupUserModel);
		startActivity(intent);

	}

	private void loadData() {
		GroupRequestMananger.getInstance().getUserInfo(mUid, this, new RequestResultListner() {

			@Override
			public void onFinish(SobeyType result) {
				// 界面已经被销毁 线程才返回结果 此时什么都不处理
				if (isFinishing()) {
					return;
				}

				if (result instanceof GroupUserModel) {
					GroupUserModel userModel = (GroupUserModel) result;
					if (userModel.isFriend == 1) {
						// 好友关系
						followCb.setChecked(true);
					} else {
						followCb.setChecked(false);
					}
					followCb.setOnCheckedChangeListener(new MyCheckChangeListener());
					if (null == mAdapter) {
						mAdapter = new GroupPersonalInfoAdapter(mContext);
					}
					mAdapter.setData(userModel.postedSubjectList);
					// ImageLoader.getInstance().displayImage(userModel.userHeadUrl,
					// headIv);
				}
				mListView.setAdapter(mAdapter);
				mCloseLoadingIcon();
			}
		});
	}

	public void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.hide(mListView);
			animationController.show(mLoadingIconLayout);
		}
	}

	public void mCloseLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mListView);
			animationController.fadeOut(mLoadingIconLayout, 1000, 0);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 界面被回收时 存储数据
		Bundle bundle = new Bundle();
		if (TextUtils.isEmpty(mUserName)) {
			bundle.putString("mUserName", mUserName);
		}
		if (TextUtils.isEmpty(mUid)) {
			bundle.putString("mUid", mUid);
		}
		if (TextUtils.isEmpty(mHeadUrl)) {
			bundle.putString("mHeadUrl", mHeadUrl);
		}
		outState.putBundle("userInfos", bundle);
	}

	private class MyCheckChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			int follow = 0;
			if (isChecked) {
				follow = 1;
				buttonView.setText(R.string.cancel_follow);
			} else {
				buttonView.setText(R.string.follow);
				follow = 0;
			}
			GroupRequestMananger.getInstance().followUser(mUid, follow, mContext, new RequestResultListner() {

				@Override
				public void onFinish(SobeyType result) {
					if (result instanceof SobeyBaseResult) {

					}
				}
			});
		}

	}
}
