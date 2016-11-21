package com.sobey.cloud.webtv.broke;

import java.util.ArrayList;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.animation.AnimationController;
import com.dylan.uiparts.circularseekbar.CircularSeekBar;
import com.dylan.uiparts.listview.DragListView;
import com.dylan.uiparts.listview.DragListView.IDragListViewListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.broke.util.BrokeFooterControl;
import com.sobey.cloud.webtv.broke.util.BrokeTaskUploadControl;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.obj.CacheDataBrokeTask;
import com.sobey.cloud.webtv.obj.CatalogObj;
import com.sobey.cloud.webtv.obj.CatalogType;
import com.sobey.cloud.webtv.obj.ViewHolderBrokeTask;
import com.sobey.cloud.webtv.senum.BrokeTaskStatus;
import com.sobey.cloud.webtv.utils.CatalogControl;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 爆料管理界面
 * 
 * @author lgx
 *
 */
public class BrokeTaskHomeActivity extends BaseActivity implements IDragListViewListener {

	private static final int ItemType_AdBanner = 0;
	private static final int ItemType_Normal = 2;

	private BaseAdapter mAdapter;
	private ArrayList<CacheDataBrokeTask> mCacheDatas = new ArrayList<CacheDataBrokeTask>();
	private CatalogObj mCatalogObj;
	private int mPageIndex = 0;
	@SuppressWarnings("unused")
	private int mPageSize = 10;
	private boolean mHasAdBanner = false;
	private LayoutInflater inflater;
	private int mScreenWidth;
	private long mStartTime = 0;
	private ProgressReceiver mProgressReceiver;
	private String mUserName;
	private boolean initFlag = false;
	private int mCatalogId = 0;

	@GinInjectView(id = R.id.mListView)
	DragListView mListView;

	@GinInjectView(id = R.id.mTabHost)
	TabHost mTabHost;

	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_broke_task_home;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		init();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus && initFlag) {
			SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(BrokeTaskHomeActivity.this, BrokeNewsHomeActivity.class));
				finish();
			}
		}
		if (hasFocus) {
			if (mCacheDatas != null) {
				loadMore(mPageIndex);
			}
		}
		super.onWindowFocusChanged(hasFocus);
	}

	public void init() {
		SharedPreferences settings = getSharedPreferences("lauch_mode", 0);
		Editor editor = settings.edit();
		editor.putBoolean("broke_task_home", false);
		editor.commit();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				initFlag = true;
			}
		}, 500);
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			startActivity(new Intent(BrokeTaskHomeActivity.this, com.sobey.cloud.webtv.views.user.LoginActivity.class));
		}
		mUserName = userInfo.getString("id", "");

		try {
			if (MConfig.CatalogList.size() < 1) {
				CatalogControl.setCatalogList(this, null);
			}
			int def = 0;
			for (int i = 0; i < MConfig.CatalogList.size(); i++) {
				if (MConfig.CatalogList.get(i).type == CatalogType.Broke) {
					def = i;
				}
			}
			mCatalogObj = MConfig.CatalogList.get(getIntent().getIntExtra("index", def));
		} catch (Exception e) {
			finish();
		}
		initSliding(false);
		setTitle(mCatalogObj.name);
		setModuleMenuSelectedItem(mCatalogObj.index);
		BrokeFooterControl.setBrokeFooterSelectedItem(this, "任务", mCatalogObj.index);

		inflater = LayoutInflater.from(this);
		mScreenWidth = getResources().getDisplayMetrics().widthPixels;

		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setListener(this);
		mListView.setHeaderColor(0xffffffff);
		mListView.setHeaderDividersEnabled(false);
		mListView.setFooterBackgroundColor(0xffffffff);
		mListView.setBackgroundColor(0xffffffff);

		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderBrokeTask viewHolderBrokeTask = null;
				int type = getItemViewType(position);

				if (convertView == null) {
					convertView = inflater.inflate(R.layout.listitem_broketask, null);
					viewHolderBrokeTask = new ViewHolderBrokeTask();
					viewHolderBrokeTask.setImage((AdvancedImageView) convertView.findViewById(R.id.image));
					viewHolderBrokeTask.setTime((TextView) convertView.findViewById(R.id.time_text));
					viewHolderBrokeTask.setTitle((TextView) convertView.findViewById(R.id.title));
					viewHolderBrokeTask.setVideocount((TextView) convertView.findViewById(R.id.videocount_text));
					viewHolderBrokeTask.setVideocountImage((ImageView) convertView.findViewById(R.id.videocount_image));
					viewHolderBrokeTask.setPicturecount((TextView) convertView.findViewById(R.id.picturecount_text));
					viewHolderBrokeTask
							.setPicturecountImage((ImageView) convertView.findViewById(R.id.picturecount_image));
					viewHolderBrokeTask
							.setWaitingLayout((RelativeLayout) convertView.findViewById(R.id.waiting_layout));
					viewHolderBrokeTask
							.setUploadingLayout((RelativeLayout) convertView.findViewById(R.id.uploading_layout));
					viewHolderBrokeTask.setUploadingCircularSeekbar(
							(CircularSeekBar) convertView.findViewById(R.id.uploading_circularseekbar));
					viewHolderBrokeTask.setContentLayout((LinearLayout) convertView.findViewById(R.id.content_layout));
					viewHolderBrokeTask.setActionBtn((Button) convertView.findViewById(R.id.action_btn));
					viewHolderBrokeTask.setHorizontalScrollView(
							(HorizontalScrollView) convertView.findViewById(R.id.horizontal_scrollview));
					viewHolderBrokeTask.getContentLayout().getLayoutParams().width = mScreenWidth;
					ActionBtnClass actionBtnClass = new ActionBtnClass();
					actionBtnClass.postion = position;
					actionBtnClass.horizontalScrollView = viewHolderBrokeTask.getHorizontalScrollView();
					viewHolderBrokeTask.getActionBtn().setTag(actionBtnClass);
					viewHolderBrokeTask.getWaitingLayout().setTag(actionBtnClass);
					viewHolderBrokeTask.getUploadingLayout().setTag(actionBtnClass);
					convertView.setTag(viewHolderBrokeTask);
					loadViewHolder(position, convertView, type);
				} else {
					loadViewHolder(position, convertView, type);
				}
				return convertView;
			}

			@Override
			public int getItemViewType(int position) {
				if (position == 0 && mHasAdBanner)
					return ItemType_AdBanner;
				else
					return ItemType_Normal;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public int getCount() {
				int count = 0;
				if (mHasAdBanner) {
					count++;
				}
				if (mCacheDatas == null || mCacheDatas.size() < 1)
					return count;
				count += mCacheDatas.size();
				return count;
			}
		};
		mListView.setAdapter(mAdapter);
		mListView.setAsOuter();
		setupTabBar();

		mProgressReceiver = new ProgressReceiver();
		IntentFilter filter = new IntentFilter(BrokeTaskProgressNotificationService.ServiceAction);
		registerReceiver(mProgressReceiver, filter);
	}

	private void loadViewHolder(int position, View convertView, int type) {
		final ViewHolderBrokeTask viewHolderBrokeTask = (ViewHolderBrokeTask) convertView.getTag();
		int pos = position;
		if (mHasAdBanner) {
			pos--;
		}
		final CacheDataBrokeTask data = mCacheDatas.get(pos);
		viewHolderBrokeTask.getImage().setLocalImage(data.getLogo());
		viewHolderBrokeTask.getTime().setText(data.getTime());
		viewHolderBrokeTask.getTitle().setText(data.getTitle());
		if (data.getVideocount() < 1) {
			viewHolderBrokeTask.getVideocountImage().setVisibility(View.GONE);
			viewHolderBrokeTask.getVideocount().setVisibility(View.GONE);
		} else {
			viewHolderBrokeTask.getVideocount().setText(data.getVideocount() + "个");
		}
		if (data.getImagecount() < 1) {
			viewHolderBrokeTask.getPicturecountImage().setVisibility(View.GONE);
			viewHolderBrokeTask.getPicturecount().setVisibility(View.GONE);
		} else {
			viewHolderBrokeTask.getPicturecount().setText(data.getImagecount() + "张");
		}
		switch (data.getStatus()) {
		case WAITING:
			viewHolderBrokeTask.getWaitingLayout().setVisibility(View.VISIBLE);
			viewHolderBrokeTask.getUploadingLayout().setVisibility(View.GONE);
			break;
		case UPLOADING:
			viewHolderBrokeTask.getWaitingLayout().setVisibility(View.GONE);
			viewHolderBrokeTask.getUploadingLayout().setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.uploading_pause_image).setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.uploading_start_image).setVisibility(View.GONE);
			viewHolderBrokeTask.getUploadingCircularSeekbar().setMaxProgress(100);
			viewHolderBrokeTask.getUploadingCircularSeekbar().setProgress(data.getProgress());
			viewHolderBrokeTask.getUploadingCircularSeekbar().setProgressTouchable(false);
			viewHolderBrokeTask.getUploadingCircularSeekbar().invalidate();
			break;
		case PAUSE:
			viewHolderBrokeTask.getWaitingLayout().setVisibility(View.GONE);
			viewHolderBrokeTask.getUploadingLayout().setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.uploading_pause_image).setVisibility(View.GONE);
			convertView.findViewById(R.id.uploading_start_image).setVisibility(View.VISIBLE);
			viewHolderBrokeTask.getUploadingCircularSeekbar().setMaxProgress(100);
			viewHolderBrokeTask.getUploadingCircularSeekbar().setProgress(data.getProgress());
			viewHolderBrokeTask.getUploadingCircularSeekbar().setProgressTouchable(false);
			viewHolderBrokeTask.getUploadingCircularSeekbar().invalidate();
			break;
		case FINISHED:
			viewHolderBrokeTask.getWaitingLayout().setVisibility(View.GONE);
			viewHolderBrokeTask.getUploadingLayout().setVisibility(View.GONE);
			break;
		default:
			viewHolderBrokeTask.getWaitingLayout().setVisibility(View.GONE);
			viewHolderBrokeTask.getUploadingLayout().setVisibility(View.GONE);
		}
		if (mCatalogId != 0) {
			viewHolderBrokeTask.getWaitingLayout().setVisibility(View.GONE);
			viewHolderBrokeTask.getUploadingLayout().setVisibility(View.GONE);
		}

		convertView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ViewHolderBrokeTask viewHolderBrokeTask = (ViewHolderBrokeTask) v.getTag();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					v.setPressed(true);
					if (viewHolderBrokeTask.getHorizontalScrollView().getScrollX() > 0) {
						viewHolderBrokeTask.getHorizontalScrollView().smoothScrollTo(0, 0);
						mStartTime = 0;
					} else {
						mStartTime = System.currentTimeMillis();
					}
					return true;
				case MotionEvent.ACTION_UP:
					v.setPressed(false);
					int scrollX = viewHolderBrokeTask.getHorizontalScrollView().getScrollX();
					int actionW = viewHolderBrokeTask.getActionBtn().getWidth();
					if (scrollX > 0 && scrollX < actionW * 0.8) {
						viewHolderBrokeTask.getHorizontalScrollView().smoothScrollTo(0, 0);
					} else if (scrollX <= 0) {
						if (mStartTime > 0) {
							long difTime = System.currentTimeMillis() - mStartTime;
							if (difTime > 0 && difTime < 500) {
								Log.i("dzy", "click");
							}
						}
					} else {
						viewHolderBrokeTask.getHorizontalScrollView().smoothScrollTo(actionW, 0);
					}
					mStartTime = 0;
					return true;
				case MotionEvent.ACTION_MOVE:
					if (viewHolderBrokeTask.getHorizontalScrollView().getScrollX() <= 0 && mStartTime > 0) {
						long difTime = System.currentTimeMillis() - mStartTime;
						if (difTime >= 500 && difTime < 2000) {
							viewHolderBrokeTask.getHorizontalScrollView()
									.smoothScrollTo(viewHolderBrokeTask.getActionBtn().getWidth(), 0);
							mStartTime = 0;
							return true;
						}
					}
					break;
				}
				return false;
			}
		});

		viewHolderBrokeTask.getActionBtn().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActionBtnClass actionBtnClass = (ActionBtnClass) v.getTag();
				actionBtnClass.horizontalScrollView.smoothScrollTo(0, 0);
				deleteItem(actionBtnClass.postion);
			}
		});

		viewHolderBrokeTask.getWaitingLayout().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActionBtnClass actionBtnClass = (ActionBtnClass) v.getTag();
				if (BrokeTaskUploadControl.startUploadService(BrokeTaskHomeActivity.this,
						mCacheDatas.get(actionBtnClass.postion))) {
					Toast.makeText(BrokeTaskHomeActivity.this, "开始上传...", Toast.LENGTH_SHORT).show();
					mCacheDatas.get(actionBtnClass.postion).setStatus(BrokeTaskStatus.UPLOADING);
					mAdapter.notifyDataSetChanged();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							mAdapter.notifyDataSetChanged();
						}
					}, 200);
				}
			}
		});

		viewHolderBrokeTask.getUploadingLayout().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActionBtnClass actionBtnClass = (ActionBtnClass) v.getTag();
				ImageView pauseImageView = (ImageView) v.findViewById(R.id.uploading_pause_image);
				if (pauseImageView.getVisibility() == View.VISIBLE) {
					if (BrokeTaskUploadControl.stopUploadService(BrokeTaskHomeActivity.this,
							mCacheDatas.get(actionBtnClass.postion).getId())) {
						Toast.makeText(BrokeTaskHomeActivity.this, "暂停上传", Toast.LENGTH_SHORT).show();
						mCacheDatas.get(actionBtnClass.postion).setStatus(BrokeTaskStatus.PAUSE);
						pauseImageView.setVisibility(View.GONE);
						v.findViewById(R.id.uploading_start_image).setVisibility(View.VISIBLE);
					}
				} else {
					if (BrokeTaskUploadControl.startUploadService(BrokeTaskHomeActivity.this,
							mCacheDatas.get(actionBtnClass.postion))) {
						Toast.makeText(BrokeTaskHomeActivity.this, "继续上传", Toast.LENGTH_SHORT).show();
						mCacheDatas.get(actionBtnClass.postion).setStatus(BrokeTaskStatus.UPLOADING);
						pauseImageView.setVisibility(View.VISIBLE);
						v.findViewById(R.id.uploading_start_image).setVisibility(View.GONE);
					}
				}
			}
		});
	}

	private void setupTabBar() {
		mTabHost.setup();
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
					View view = (View) mTabHost.getTabWidget().getChildAt(i);
					if (mTabHost.getCurrentTab() == i) {
						((TextView) view.findViewById(R.id.text))
								.setTextColor(getResources().getColor(R.color.home_tab_text_focus));
						view.setBackgroundResource(R.drawable.home_tab_background_selected);
						mCatalogId = i;
					} else {
						((TextView) view.findViewById(R.id.text))
								.setTextColor(getResources().getColor(R.color.home_tab_text_normal));
						view.setBackgroundResource(R.drawable.trans);
					}
				}
				onTabChange();
			}
		});
		loadCatalog();
	}

	private void loadCatalog() {
		int width = getResources().getDisplayMetrics().widthPixels;
		LayoutParams params = new LayoutParams(width / 4, LayoutParams.MATCH_PARENT);
		params.setMargins(width / 8, 0, width / 8, 0);
		params.gravity = Gravity.CENTER;
		View view = inflater.inflate(R.layout.layout_tabitem_text, null);
		((TextView) view.findViewById(R.id.text)).setText("正在上传");
		view.setLayoutParams(params);
		((TextView) view.findViewById(R.id.text)).setGravity(Gravity.CENTER);
		mTabHost.addTab(mTabHost.newTabSpec("正在上传").setIndicator(view).setContent(R.id.mListView));
		View view1 = inflater.inflate(R.layout.layout_tabitem_text, null);
		((TextView) view1.findViewById(R.id.text)).setText("上传完成");
		view1.setLayoutParams(params);
		((TextView) view1.findViewById(R.id.text)).setGravity(Gravity.CENTER);
		mTabHost.addTab(mTabHost.newTabSpec("上传完成").setIndicator(view1).setContent(R.id.mListView));
		mTabHost.setCurrentTab(1);
		mTabHost.setCurrentTab(0);
	}

	public void onTabChange() {
		mCacheDatas.clear();
		mAdapter.notifyDataSetChanged();
		loadMore(mPageIndex);
	}

	private void deleteItem(int position) {
		BrokeTaskUploadControl.stopUploadService(this, mCacheDatas.get(position).getId());
		BrokeTaskUploadControl.deleteUploadTask(this, mCacheDatas.get(position).getId(), mCatalogId);
		mCacheDatas.remove(position);
		mAdapter.notifyDataSetChanged();
	}

	private void loadMore(int pageIndex) {
		mCacheDatas.clear();
		mCacheDatas = BrokeTaskUploadControl.getUploadTaskList(this, mUserName, mCatalogId);
		if (mCacheDatas == null) {
			mCacheDatas = new ArrayList<CacheDataBrokeTask>();
		}
		mAdapter.notifyDataSetChanged();
		mCloseLoadingIcon();
	}

	@Override
	public void onRefresh() {
	}

	@Override
	public void onLoadMore() {
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mProgressReceiver);
		super.onDestroy();
	}

	@SuppressWarnings("unused")
	private void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.hide(mListView);
			animationController.show(mLoadingIconLayout);
		}
	}

	private void mCloseLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mListView);
			animationController.fadeOut(mLoadingIconLayout, 1000, 0);
		}
	}

	private class ProgressReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int progress = intent.getIntExtra("progress", 0);
			String id = intent.getStringExtra("id");
			for (int i = 0; i < mCacheDatas.size(); i++) {
				if (id.equalsIgnoreCase(mCacheDatas.get(i).getId())) {
					if (progress < 0) {
						mCacheDatas.get(i).setStatus(BrokeTaskStatus.WAITING);
					} else if (progress > 100) {
						mCacheDatas.get(i).setStatus(BrokeTaskStatus.UPLOADING);
						mCacheDatas.get(i).setProgress(100);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								if (mCacheDatas != null) {
									loadMore(mPageIndex);
								}
							}
						}, 500);
					} else {
						mCacheDatas.get(i).setStatus(BrokeTaskStatus.UPLOADING);
						mCacheDatas.get(i).setProgress(progress);
					}
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	private class ActionBtnClass {
		public int postion;
		public HorizontalScrollView horizontalScrollView;
	}
}
