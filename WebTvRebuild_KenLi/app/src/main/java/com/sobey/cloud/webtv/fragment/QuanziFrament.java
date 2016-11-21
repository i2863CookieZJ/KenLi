package com.sobey.cloud.webtv.fragment;

import com.dylan.common.animation.AnimationController;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;

import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.GroupAdapter;
import com.sobey.cloud.webtv.bean.GetGroupListResult;
import com.sobey.cloud.webtv.bean.GroupModel;
import com.sobey.cloud.webtv.bean.GroupRequestMananger;
import com.sobey.cloud.webtv.bean.GroupRequestMananger.RequestResultListner;
import com.sobey.cloud.webtv.bean.SobeyType;
import com.sobey.cloud.webtv.broadcast.LogStateChangeReceiver;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.utils.SobeyConstants;
import com.sobey.cloud.webtv.views.group.GroupDetailActivity;
import com.sobey.cloud.webtv.views.group.GroupSearchActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author bin
 *
 */
public class QuanziFrament extends BaseFragment {

	@GinInjectView(id = R.id.search_ll)
	private LinearLayout search_ll;

	@GinInjectView(id = R.id.group_frame_listView)
	DragListView mListView;
	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;
	@GinInjectView(id = R.id.mLoadingFailedLayout)
	View failedLayout;
	@GinInjectView(id = R.id.loading_failed_tips_tv)
	TextView failedTv;
	private GroupAdapter mAdatper;
	private final String TAG = this.getClass().getName();
	private boolean hasLoadSuccessed = false;

	/**
	 * 初始化布局Inflater
	 */
	private LayoutInflater mInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("TAG", "onCreate-->");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("TAG", "onCreateView-->");
		mInflater = LayoutInflater.from(getActivity());
		View v = getCacheView(mInflater, R.layout.new_group_frame2);
		// View v = inflater.inflate(R.layout.new_group_frame2,
		// container, false);
		//
		// ViewUtils.inject(this, v);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (isUseCache()) {
			return;
		}
		setupActivity();
	}

	private void setupActivity() {
		mListView.setPullLoadEnable(false);
		// mListView.setPullRefreshEnable(false);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("GroupNewFrament", "item is clicked position:" + position);
				GroupModel groupModel = (GroupModel) parent.getAdapter().getItem(position);
				// FIXME 如果该项的条目数不为0 进入详情后回到页面需要刷新数据 先这样处理
				if (groupModel.newSubjectCount != 0) {
					PreferencesUtil.putBoolean(PreferencesUtil.KEY_FOLLOW_STATE_CHANGED, true);
				}
				jump2GroupDetailActivity(groupModel);
			}
		});
		mListView.setListener(new IDragListViewListener() {

			@Override
			public void onRefresh() {
				loadData(false, false);
			}

			@Override
			public void onLoadMore() {

			}
		});
		failedTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadData(true, false);
			}
		});
		registerLogStateChangeReciecer();
		loadData(true, false);

		search_ll.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
		case R.id.search_ll:
			Intent intent = new Intent(this.getActivity(), GroupSearchActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		Log.i("TAG", "onAttach-->");
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		Log.i("TAG", "onDetach-->");
		super.onDetach();
	}

	@Override
	public void onPause() {
		Log.i("TAG", "onPause-->");
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.i("TAG", "onResume-->");
		boolean isFollowStateChanged = PreferencesUtil.getBoolean(PreferencesUtil.KEY_FOLLOW_STATE_CHANGED);
		if (isFollowStateChanged) {
			Log.i(TAG, "onResume--> 关注状态发生变化..刷新列表！");
			loadData(false, true);
		}
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unRegisterLogStateChangeReciecer();
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		Log.i("TAG", "onViewStateRestored-->");
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.i("TAG", "onSaveInstanceState-->");
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroyView() {
		Log.i("TAG", "onDestroyView-->");
		super.onDestroyView();
	}

	/**
	 * @param showLoadingIcon
	 *            这个界面显示正在加载
	 * @param isManualRefresh
	 *            主动发起刷新请求
	 */
	private void loadData(final boolean showLoadingIcon, final boolean isManualRefresh) {
		if (showLoadingIcon) {
			mOpenLoadingIcon();
		}
		if (isManualRefresh) {
			mListView.startManualRefresh();
		}
		GroupRequestMananger.getInstance().getGroupList(this.getActivity(), new RequestResultListner() {

			@Override
			public void onFinish(SobeyType result) {
				mCloseLoadingIcon();
				if (result instanceof GetGroupListResult) {
					hasLoadSuccessed = true;
					failedLayout.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
					if (null == mAdatper) {
						mAdatper = new GroupAdapter(getActivity());
					}
					mAdatper.setData((GetGroupListResult) result);
					mListView.setAdapter(mAdatper);
					mAdatper.notifyDataSetChanged();
				} else {
					if (!hasLoadSuccessed) {
						failedLayout.setVisibility(View.VISIBLE);
						mListView.setVisibility(View.GONE);
					} else {
						// Toast.makeText(getActivity(),
						// R.string.request_data_failed,
						// Toast.LENGTH_SHORT).show();
					}
				}
				PreferencesUtil.putBoolean(PreferencesUtil.KEY_FOLLOW_STATE_CHANGED, false);
				if (isManualRefresh) {
					mListView.stopMannualRefresh();
				} else {
					mListView.stopRefresh();
				}
			}
		});
	}

	private void jump2GroupDetailActivity(GroupModel groupModel) {
		Intent intent = new Intent(this.getActivity(), GroupDetailActivity.class);
		intent.putExtra("mGroupModel", groupModel);
		startActivity(intent);
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

	private void registerLogStateChangeReciecer() {
		logStateReceiver = new LogStateChangeReceiver(handler);
		IntentFilter filter = new IntentFilter(SobeyConstants.ACTION_LOG_STATE_CHANGE);
		getActivity().registerReceiver(logStateReceiver, filter);
	}

	private void unRegisterLogStateChangeReciecer() {
		if (null != logStateReceiver) {
			getActivity().unregisterReceiver(logStateReceiver);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == SobeyConstants.CODE_FOR_LOG_STATE_CHANGE) {
				loadData(false, true);
			}
		};
	};
	private LogStateChangeReceiver logStateReceiver;
}
