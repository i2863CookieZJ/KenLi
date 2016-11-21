package com.sobey.cloud.webtv.views.group;

import com.dylan.common.animation.AnimationController;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.GroupDetailAdapter;
import com.sobey.cloud.webtv.bean.GroupModel;
import com.sobey.cloud.webtv.bean.GroupRequestMananger;
import com.sobey.cloud.webtv.bean.GroupRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.broadcast.LogStateChangeReceiver;
import com.sobey.cloud.webtv.broadcast.PostSubjectSuccessReciever;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.SobeyConstants;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GroupDetailActivity extends BaseActivity4Group implements View.OnClickListener {
	@GinInjectView(id = R.id.group_detail_activity_listView)
	DragListView mListView;
	@GinInjectView(id = R.id.title_tv)
	TextView mTitleTv;
	@GinInjectView(id = R.id.title_layout)
	LinearLayout titleLayout;
	@GinInjectView(id = R.id.group_detail_activity_titlemore_layout)
	LinearLayout titlemore_layout;
	@GinInjectView(id = R.id.group_detail_activity_titlemore_container_layout)
	LinearLayout titlemore_container_layout;
	@GinInjectView(id = R.id.group_detail_activity_sort_radiogroup)
	RadioGroup sortRadioGroup;
	@GinInjectView(id = R.id.group_detail_activity_filter_radiogroup)
	RadioGroup filterRadioGroup;
	@GinInjectView(id = R.id.group_detail_activity_order_in_reply_cb)
	RadioButton replyBtn;
	@GinInjectView(id = R.id.group_detail_activity_order_in_publish_cb)
	RadioButton publishBtn;
	@GinInjectView(id = R.id.group_detail_activity_order_in_checkall_cb)
	RadioButton checkallBtn;
	@GinInjectView(id = R.id.group_detail_activity_order_in_checkessence_cb)
	RadioButton checkssenceBtn;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	// @GinInjectView(id = R.id.title_left)
	// Button btnLeft;
	// @GinInjectView(id = R.id.title_right)
	// Button btnRight;

	private GroupModel mGroupModel;
	private Animation anim_in;
	private Animation anim_out;
	private GroupDetailAdapter groupDetailAdapter;
	private PostSubjectSuccessReciever postSubjectSuccessReceiver;
	private LogStateChangeReceiver logStateReceiver;
	private int mCurrentPage = 1;
	private int totalPage;
	private int pageSize = 20;
	private String sort = SortEnum.reply.toString();
	private String filter = "";

	@Override
	public int getContentView() {
		return R.layout.activity_group_deail_layout;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		mGroupModel = this.getIntent().getParcelableExtra("mGroupModel");
		mGroupModel = this.getIntent().getParcelableExtra("mGroupModel");
		if (null != savedInstanceState) {
			mGroupModel = savedInstanceState.getParcelable("mGroupModel");
		}

		initAnim();
		setUpDatas();
	}

	public void setUpDatas() {

		mOpenLoadingIcon();

		if (null != mGroupModel) {
			mTitleTv.setText(mGroupModel.groupName);
		}

		titleLayout.performClick();
		mListView.setPullLoadEnable(true);

		loadData(false, false);
		setListeners();
		RegisterReciever();

	}

	@Override
	public void onBackPressed() {
		if (titlemore_layout.getVisibility() == View.VISIBLE) {
			titlemore_layout.startAnimation(anim_out);
			return;
		}
		super.onBackPressed();
	}

	/**
	 * 加载数据
	 * 
	 * @param isManualRefresh
	 *            是否自动刷新
	 */
	private void loadData(final boolean isManualRefresh, final boolean isShowLoadMore) {
		Log.i(TAG, "loadData--> currentPage:" + mCurrentPage);
		if (isManualRefresh) {
			mListView.startManualRefresh();
		}
		GroupRequestMananger.getInstance().getGroupInfo(mGroupModel.groupId, mCurrentPage, sort, filter, mContext,
				new RequestResultListner() {
					@Override
					public void onFinish(SobeyType result) {

						if (isFinishing()) {
							Log.i(TAG, TAG + " is finishing.");
							return;
						}

						if (result instanceof GroupModel) {
							if (groupDetailAdapter == null) {
								groupDetailAdapter = new GroupDetailAdapter(mContext);
							}
							String subjectCount = ((GroupModel) result).subjectCount;
							int count = 1;
							try {
								count = Integer.parseInt(subjectCount);
								if (count == 0) {
									Toast.makeText(mContext, "无内容", Toast.LENGTH_SHORT).show();
								}
							} catch (NumberFormatException e) {

							}
							if (count <= pageSize) {
								totalPage = 1;
							} else if (count % pageSize == 0) {
								totalPage = count / pageSize;
							} else {
								totalPage = count / pageSize + 1;
							}
							if (mCurrentPage == 1) {
								groupDetailAdapter.clearData();
								mGroupModel.isFollowed = ((GroupModel) result).isFollowed;
								groupDetailAdapter.setData((GroupModel) result, mGroupModel);
								if (totalPage == 1) {
									mListView.setPullLoadEnable(false);
								}
							} else {
								groupDetailAdapter.addData((GroupModel) result);
							}
							mListView.setAdapter(groupDetailAdapter);
							mListView.setSelection((mCurrentPage - 1) * pageSize - 1);
							groupDetailAdapter.notifyDataSetChanged();
						}
						if (isManualRefresh) {
							mListView.stopMannualRefresh();
						} else if (isShowLoadMore) {
							mListView.stopLoadMore();
						} else {
							mListView.stopRefresh();
							mCloseLoadingIcon();
						}
					}
				});
	}

	private void setListeners() {

		titlemore_container_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "titlemore_container_layout is clicked!!");
				doTitleTextClickAction();
			}
		});

		titleLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doTitleTextClickAction();
			}
		});
		mListView.setListener(new IDragListViewListener() {

			@Override
			public void onRefresh() {
				mCurrentPage = 1;
				loadData(false, false);
			}

			@Override
			public void onLoadMore() {
				Log.i(TAG, "onLoadMore-->");
				// 此时表示点击了查看更多
				mCurrentPage += 1;
				if (mCurrentPage >= totalPage) {
					mCurrentPage = totalPage;
					mListView.setPullLoadEnable(false);
				}
				loadData(false, true);
			}
		});
		sortRadioGroup.setOnCheckedChangeListener(new MyCheckChangeListener());
		filterRadioGroup.setOnCheckedChangeListener(new MyCheckChangeListener());
	}

	private void RegisterReciever() {
		postSubjectSuccessReceiver = new PostSubjectSuccessReciever(handler);
		IntentFilter filter = new IntentFilter(SobeyConstants.ACTION_POST_SUBJECT);
		registerReceiver(postSubjectSuccessReceiver, filter);

		logStateReceiver = new LogStateChangeReceiver(handler);
		filter = new IntentFilter(SobeyConstants.ACTION_LOG_STATE_CHANGE);
		registerReceiver(logStateReceiver, filter);

	}

	private void unRegisterReciever() {
		if (null != postSubjectSuccessReceiver) {
			unregisterReceiver(postSubjectSuccessReceiver);
		}
		if (null != logStateReceiver) {
			unregisterReceiver(logStateReceiver);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_rl:
			finishActivity();
			break;
		case R.id.operator_rl:
			// 未登录跳转到登录页面
			if (TextUtils.isEmpty(PreferencesUtil.getLoggedUserId())) {
				Intent intent = new Intent(mContext, LoginActivity.class);
				startActivity(intent);
				return;
			}

			Intent intent = new Intent(mContext, GroupPostSubjectActivity.class);
			intent.putExtra("mGroupModel", mGroupModel);
			startActivity(intent);

			break;

		default:
			break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (null != mGroupModel) {
			outState.putParcelable("mGroupModel", mGroupModel);
		}
	}

	@Override
	protected void handleMessage(Message msg) {
		super.handleMessage(msg);
		if (msg.what == 0) {
			mCurrentPage = 1;
			loadData(true, false);
		}
		if (msg.what == SobeyConstants.CODE_FOR_LOG_STATE_CHANGE) {
			mCurrentPage = 1;
			loadData(true, false);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterReciever();
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

	private void doTitleTextClickAction() {
		Log.i(TAG, "title_layuout clicked!!");
		if (titlemore_layout.getVisibility() == View.VISIBLE) {

			titlemore_layout.startAnimation(anim_out);

		} else {
			titlemore_container_layout.setVisibility(View.VISIBLE);
			titlemore_layout.startAnimation(anim_in);
			titlemore_layout.setVisibility(View.VISIBLE);

		}
	}

	private void initAnim() {
		anim_in = AnimationUtils.loadAnimation(mContext, R.anim.top_in);
		anim_out = AnimationUtils.loadAnimation(mContext, R.anim.top_out);
		anim_out.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				titlemore_layout.setVisibility(View.GONE);
				titlemore_container_layout.setVisibility(View.GONE);
			}
		});
	}

	private class MyCheckChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (group.getId()) {
			case R.id.group_detail_activity_sort_radiogroup:
				switch (checkedId) {
				case R.id.group_detail_activity_order_in_reply_cb:
					if (sort.equals(SortEnum.reply.toString())) {
						return;
					} else {
						sort = SortEnum.reply.toString();
						loadData(true, false);
						doTitleTextClickAction();
					}
					break;
				case R.id.group_detail_activity_order_in_publish_cb:
					if (sort.equals(SortEnum.publish.toString())) {
						return;
					} else {
						sort = SortEnum.publish.toString();
						loadData(true, false);
						doTitleTextClickAction();
					}
					break;

				default:
					break;
				}
				break;
			case R.id.group_detail_activity_filter_radiogroup:

				switch (checkedId) {
				case R.id.group_detail_activity_order_in_checkall_cb:
					if (TextUtils.isEmpty(filter)) {
						return;
					} else {
						filter = "";
						loadData(true, false);
						doTitleTextClickAction();
					}
					break;
				case R.id.group_detail_activity_order_in_checkessence_cb:
					if (filter.equals(FilterEnum.digest.toString())) {
						return;
					} else {
						filter = FilterEnum.digest.toString();
						loadData(true, false);
						doTitleTextClickAction();
					}
					break;
				// case R.id.group_detail_activity_order_in_checkhot_cb:
				// if(filter.equals(FilterEnum.heats.toString())){
				// return;
				// }else{
				// filter = FilterEnum.heats.toString();
				// loadData(true, false);
				// doTitleTextClickAction();
				// }
				// break;

				default:
					break;
				}
				break;

			default:
				break;
			}
		}

		private void doCheckAction(int id) {

		}
	}

	public enum SortEnum {
		publish, // 最新发布
		reply// 最新回复
	}

	public enum FilterEnum {
		digest, // 精华
		heats// 热点
	}
}
