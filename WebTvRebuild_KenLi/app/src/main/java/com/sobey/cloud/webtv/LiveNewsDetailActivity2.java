package com.sobey.cloud.webtv;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.appsdk.video.obj.MediaObj;
import com.appsdk.video.obj.ResolutionList;
import com.appsdk.video.obj.ResolutionObj;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionWithParamListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.dylan.common.utils.CheckNetwork;
import com.dylan.common.utils.DateParse;
import com.dylan.uiparts.gifview.GifView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.obj.ViewHolderLiveNewsDetailGuide;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.MConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 音频界面，和视频界面有部分区别
 * 
 * @time 2016/1/20
 * @author Administrator
 *
 */
@SuppressLint("SetJavaScriptEnabled")
public class LiveNewsDetailActivity2 extends BaseActivity implements OnPreparedListener, OnCompletionListener,
		OnErrorListener, OnInfoListener, OnPlayingBufferCacheListener, OnCompletionWithParamListener {

	private enum GuideItemState {
		REPLAY, LIVE, COMING_SOON
	}

	protected boolean isDisposed;
	private JSONObject mInformation = null;
	private Map<String, JSONArray> guideMap = new HashMap<String, JSONArray>();
	private JSONArray guideList = new JSONArray();
	private ArrayList<ResolutionObj> resolutionObjs = new ArrayList<ResolutionObj>();
	private MediaObj mediaObj;
	private MediaObj replayObj;
	private boolean isShowVideoPlayer = true;
	@SuppressWarnings("unused")
	private boolean isInitVideoPlayer = false;
	private String[] tabName = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };
	private BaseAdapter mAdapter;
	// private SharePopWindow mSharePopWindow;
	private String mArticalId;
	private String mCatalogId;
	private View mGuideHeaderView;
	private LinearLayout mGuideHeaderViewLayout;
	private GifView mGuideHeaderLoadingIcon;
	private TextView mGuideHeaderNotice;
	private int mToday = 0;
	private String mUserName;

	@GinInjectView(id = R.id.titlebartop)
	View titlebartop;
	@GinInjectView(id = R.id.mLivedetailFooter)
	LinearLayout mLivedetailFooter;
	@GinInjectView(id = R.id.mLivedetailDivider)
	LinearLayout mLivedetailDivider;
	@GinInjectView(id = R.id.mLivedetailContentTab)
	TabHost mLivedetailContentTab;
	// TV Guide
	@GinInjectView(id = R.id.mGuide)
	ListView mGuide;
	// Footer
	@GinInjectView(id = R.id.mLivedetailBack)
	ImageButton mLivedetailBack;
	@GinInjectView(id = R.id.mLivedetailDownload)
	ImageButton mLivedetailDownload;
	@GinInjectView(id = R.id.mLivedetailShare)
	ImageButton mLivedetailShare;

	@GinInjectView(id = R.id.titlebar_name)
	protected TextView titlebar_name;
	@GinInjectView(id = R.id.back_rl)
	protected RelativeLayout top_back;
	private boolean mIsSeekFlag;
	/** 上次点击的列表项索引 **/
	private int mLastClickItem = -1;
	/** 当前选中的table索引 */
	private int mTableIndex;
	/** 标明是音频还是视频直播 */
	private String liveMark;
	/** 支持时移不 */
	protected boolean timeShift;
	/**
	 * 百度播放器相关
	 * 
	 * @time 2016-01-18
	 * 
	 */
	@GinInjectView(id = R.id.mplayerView)
	View playerView;

	private String mVideoSource = null;

	private BVideoView mVV = null;
	private RelativeLayout mViewHolder = null;
	private LinearLayout mControllerHolder = null;

	private ImageButton mPlaybtn = null;
	private ImageButton mFullScreenBtn = null;
	private SeekBar mProgress = null;
	private TextView mDuration = null;
	private TextView mCurrPostion = null;

	private EventHandler mEventHandler;
	private HandlerThread mHandlerThread;

	private final Object SYNC_Playing = new Object();

	private final int EVENT_PLAY = 0;
	private final int UI_EVENT_UPDATE_CURRPOSITION = 1;
	private WakeLock mWakeLock = null;
	private Toast toast;



	public PowerManager pm;
	private WakeLock wakeLock;

	/**
	 * 播放状态
	 */
	private enum PLAYER_STATUS {
		PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
	}

	private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;

	/**
	 * 记录播放位置
	 */
	private int mLastPos = 0;

	class EventHandler extends Handler {
		public EventHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_PLAY:
				/**
				 * 如果已经播放了，等待上一次播放结束
				 */
				if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
					synchronized (SYNC_Playing) {
						try {
							SYNC_Playing.wait();
							Log.v(TAG, "wait player status to idle");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				/**
				 * 设置播放url
				 */

				mVV.setVideoPath(mVideoSource);
				// mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
				/**
				 * 续播，如果需要如此
				 */
				if (mLastPos > 0) {

					mVV.seekTo(mLastPos);
					mLastPos = 0;
				}

				/**
				 * 显示或者隐藏缓冲提示
				 */
				mVV.showCacheInfo(true);

				/**
				 * 开始播放
				 */
				mVV.start();

				mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
				break;
			default:
				break;
			}
		}
	}

	Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/**
			 * 更新进度及时间
			 */
			case UI_EVENT_UPDATE_CURRPOSITION:
				int currPosition = mVV.getCurrentPosition();
				int duration = mVV.getDuration();
				updateTextViewWithTimeFormat(mCurrPostion, currPosition);
				updateTextViewWithTimeFormat(mDuration, duration);
				mProgress.setMax(duration);
				if (mVV.isPlaying()) {
					mProgress.setProgress(currPosition);
				}
				mUIHandler.sendEmptyMessageDelayed(UI_EVENT_UPDATE_CURRPOSITION, 200);
				break;
			default:
				break;
			}
		}
	};

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * 实现切换示例
	 */
	private OnClickListener mPreListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.v(TAG, "pre btn clicked");
			/**
			 * 如果已经开发播放，先停止播放
			 */
			if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
				mVV.stopPlayback();
			}

			/**
			 * 发起一次新的播放任务
			 */
			if (mEventHandler.hasMessages(EVENT_PLAY))
				mEventHandler.removeMessages(EVENT_PLAY);
			mEventHandler.sendEmptyMessage(EVENT_PLAY);
		}
	};

	private OnClickListener mNextListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.v(TAG, "next btn clicked");
		}
	};

	@Override
	public int getContentView() {
		return R.layout.activity_livenews_detail2;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);

		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(
				PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,
				"My Tag");
		wakeLock.acquire();
		wakeLock.setReferenceCounted(false);

		SharedPreferences userInfo = LiveNewsDetailActivity2.this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mUserName = "";
		} else {
			mUserName = userInfo.getString("id", "");
		}

		isShowVideoPlayer = true;
		String str = getIntent().getStringExtra("information");
		liveMark = getIntent().getStringExtra("liveMark");
		initTab();
		initFooter();
		if (isShowVideoPlayer) {
			initPlayer();
		}
		try {
			mInformation = new JSONObject(str);
			mArticalId = String.valueOf(mInformation.getInt("id"));
			mCatalogId = mInformation.optString("parentid");
			titlebar_name.setText(mInformation.getString("title"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		getRtspSource();
		initGuide();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			SharedPreferences userInfo = LiveNewsDetailActivity2.this.getSharedPreferences("user_info", 0);
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				mUserName = "";
			} else {
				mUserName = userInfo.getString("id", "");
			}
		}
	}

	private void initFooter() {
		View mActivityLayoutView = (RelativeLayout) findViewById(R.id.activity_livenews_detail_layout);
		// mSharePopWindow = new SharePopWindow(this, mActivityLayoutView);
		top_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LiveNewsDetailActivity2.this.finish();
			}
		});
		mLivedetailBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LiveNewsDetailActivity2.this.finish();
			}
		});
		mLivedetailDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		mLivedetailShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mSharePopWindow.showShareWindow();
			}
		});
	}

	private void initTab() {
		mToday = DateParse.getWeekToday() > 0 ? (DateParse.getWeekToday() - 1) : 0;
		tabName[mToday] = "今天";
		mLivedetailContentTab.setup();
		View view0 = LayoutInflater.from(this).inflate(R.layout.layout_tabitem_livenews, null);
		View view1 = LayoutInflater.from(this).inflate(R.layout.layout_tabitem_livenews, null);
		View view2 = LayoutInflater.from(this).inflate(R.layout.layout_tabitem_livenews, null);
		View view3 = LayoutInflater.from(this).inflate(R.layout.layout_tabitem_livenews, null);
		View view4 = LayoutInflater.from(this).inflate(R.layout.layout_tabitem_livenews, null);
		View view5 = LayoutInflater.from(this).inflate(R.layout.layout_tabitem_livenews, null);
		View view6 = LayoutInflater.from(this).inflate(R.layout.layout_tabitem_livenews, null);
		mLivedetailContentTab
				.addTab(mLivedetailContentTab.newTabSpec(tabName[0]).setIndicator(view0).setContent(R.id.mGuide));
		mLivedetailContentTab
				.addTab(mLivedetailContentTab.newTabSpec(tabName[1]).setIndicator(view1).setContent(R.id.mGuide));
		mLivedetailContentTab
				.addTab(mLivedetailContentTab.newTabSpec(tabName[2]).setIndicator(view2).setContent(R.id.mGuide));
		mLivedetailContentTab
				.addTab(mLivedetailContentTab.newTabSpec(tabName[3]).setIndicator(view3).setContent(R.id.mGuide));
		mLivedetailContentTab
				.addTab(mLivedetailContentTab.newTabSpec(tabName[4]).setIndicator(view4).setContent(R.id.mGuide));
		mLivedetailContentTab
				.addTab(mLivedetailContentTab.newTabSpec(tabName[5]).setIndicator(view5).setContent(R.id.mGuide));
		mLivedetailContentTab
				.addTab(mLivedetailContentTab.newTabSpec(tabName[6]).setIndicator(view6).setContent(R.id.mGuide));
		mLivedetailContentTab.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				onTabChange(mLivedetailContentTab.getCurrentTab());
			}
		});
		mLivedetailContentTab.setCurrentTab(1);
		mLivedetailContentTab.setCurrentTab(mToday);
	}

	private void onTabChange(int index) {
		for (int i = 0; i < tabName.length; i++) {
			View view = (View) mLivedetailContentTab.getTabWidget().getChildAt(i);
			((TextView) view.findViewById(R.id.text)).setText(tabName[i]);
			if (i == index) {
				((TextView) view.findViewById(R.id.text))
						.setTextColor(getResources().getColor(R.color.home_tab_text_focus));
				((ImageView) view.findViewById(R.id.image)).setBackgroundResource(R.drawable.livenews_tab_select);
			} else {
				((TextView) view.findViewById(R.id.text))
						.setTextColor(getResources().getColor(R.color.home_tab_text_normal));
				((ImageView) view.findViewById(R.id.image)).setBackgroundResource(R.drawable.livenews_tab_unselect);
			}
		}
		if (mAdapter != null && mGuide != null) {
			getTvGuide((index - mToday), new GuideListLoadListener() {
				@Override
				public void onPre() {
					if (isDisposed)
						return;
					guideList = null;
					mAdapter.notifyDataSetChanged();
					mGuideHeaderLoadingIcon.setVisibility(View.VISIBLE);
					mGuideHeaderNotice.setText("正在获取界面列表");
					mGuideHeaderViewLayout.setVisibility(View.VISIBLE);
				}

				@Override
				public void onOk() {
					if (isDisposed)
						return;
					mGuideHeaderViewLayout.setVisibility(View.GONE);
					mAdapter.notifyDataSetChanged();
				}

				@Override
				public void onFail(String reason) {
					if (isDisposed)
						return;
					guideList = null;
					mAdapter.notifyDataSetChanged();
					mGuideHeaderLoadingIcon.setVisibility(View.GONE);
					mGuideHeaderNotice.setVisibility(View.GONE);
					mGuideHeaderNotice.setText("暂时无法获取界面列表");
					mGuideHeaderViewLayout.setVisibility(View.VISIBLE);
					mGuide.setBackgroundResource(R.drawable.listener_online);
				}
			});
		}
	}

	// TV Guide
	private void initGuide() {
		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolderLiveNewsDetailGuide viewHolder = null;
				if (convertView == null) {
					convertView = LayoutInflater.from(LiveNewsDetailActivity2.this)
							.inflate(R.layout.listitem_livenewsdetailguide, null);
					viewHolder = new ViewHolderLiveNewsDetailGuide();
					viewHolder.program_list_item_container = (RelativeLayout) convertView
							.findViewById(R.id.program_list_item_container);
					viewHolder.setStartTime((TextView) convertView.findViewById(R.id.starttime));
					viewHolder.setState((TextView) convertView.findViewById(R.id.state));
					viewHolder.setControlBtn((ImageButton) convertView.findViewById(R.id.controlbtn));
					convertView.setTag(viewHolder);
					loadViewHolder(position, convertView, viewHolder);
				} else {
					loadViewHolder(position, convertView, viewHolder);
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				if (guideList == null) {
					return 0;
				}
				return guideList.length();
			}
		};
		mGuideHeaderView = getLayoutInflater().inflate(R.layout.layout_livenewsdetail_guideheader, null);
		mGuideHeaderViewLayout = (LinearLayout) mGuideHeaderView.findViewById(R.id.livenewsdetail_guideheader_layout);
		mGuideHeaderLoadingIcon = (GifView) mGuideHeaderView.findViewById(R.id.loading_icon);
		mGuideHeaderLoadingIcon.setGifImage(R.drawable.loading_icon);
		mGuideHeaderNotice = (TextView) mGuideHeaderView.findViewById(R.id.notice);
		mGuide.addHeaderView(mGuideHeaderView);
		mGuide.setAdapter(mAdapter);

		getTvGuide(0, new GuideListLoadListener() {
			@Override
			public void onPre() {
				if (isDisposed)
					return;
				guideList = null;
				mAdapter.notifyDataSetChanged();
				mGuideHeaderLoadingIcon.setVisibility(View.VISIBLE);
				mGuideHeaderNotice.setText("正在获取界面列表");
				mGuideHeaderViewLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onOk() {
				if (isDisposed)
					return;
				mGuideHeaderViewLayout.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
				if (guideList != null) {
					getOnlineVideoPath(0);
				}
			}

			@Override
			public void onFail(String reason) {
				if (isDisposed)
					return;
				guideList = null;
				mAdapter.notifyDataSetChanged();
				mGuideHeaderLoadingIcon.setVisibility(View.GONE);
				mGuideHeaderNotice.setVisibility(View.GONE);
				mGuideHeaderNotice.setText("暂时无法获取界面列表");
				mGuideHeaderViewLayout.setVisibility(View.VISIBLE);
				mGuide.setBackgroundResource(R.drawable.listener_online);
				getOnlineVideoPath(0);
			}
		});

		mGuide.setOnItemClickListener(new OnItemClickListener() {// 节目单的点击事件
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				JSONObject object;
				try {
					object = guideList.getJSONObject(arg2 - mGuide.getHeaderViewsCount());
					if (getGuideItemState(object) == GuideItemState.LIVE) {
						if (mIsSeekFlag) {
							getOnlineVideoPath(0);
							mIsSeekFlag = false;
							mLastClickItem = -1;
							mAdapter.notifyDataSetChanged();
						}
					} else if (getGuideItemState(object) == GuideItemState.REPLAY && timeShift
							&& (arg2 - mGuide.getHeaderViewsCount()) != mLastClickItem) // 回看
					{
						seek2RePlay(object);
						mIsSeekFlag = true;
						// textView.setText("回看");
						// if(mLastClickItem!=-1)
						// {
						// textView=(TextView)mGuide.getChildAt(mLastClickItem).findViewById(R.id.state);
						// textView.setText("已播");
						// }
						mTableIndex = mLivedetailContentTab.getCurrentTab();
						mLastClickItem = arg2 - mGuide.getHeaderViewsCount();
						mAdapter.notifyDataSetChanged();

					}
				} catch (Exception e) {
				}
			}
		});
	}

	/**
	 * 回看
	 */
	private void seek2RePlay(JSONObject object) {
		Date startTime;
		try {
			startTime = DateParse.parseDate(object.getString("starttime"), null);
			long seekTime = startTime.getTime();
			String urls = object.get("url").toString();
			urls = urls.substring(urls.indexOf("["));
			JSONArray jsonArray = new JSONArray(urls);
			List<JSONObject> arr = new ArrayList<JSONObject>();
			for (int i = 0; i < jsonArray.length(); i++) {
				arr.add(jsonArray.getJSONObject(i));
			}
			Collections.reverse(arr);
			ArrayList<ResolutionObj> replayResolutions = new ArrayList<ResolutionObj>();
			for (int j = 0; j < resolutionObjs.size(); j++) {
				// flv流的回看
				// ResolutionObj obj=new
				// ResolutionObj(resolutionObjs.get(j).getTitle(),arr.get(j).getString("url").concat("&shifttime=")+seekTime);
				// hls流的回看
				ResolutionObj obj = new ResolutionObj(resolutionObjs.get(j).getTitle(),
						resolutionObjs.get(j).getMediaPath().concat("&shifttime=") + seekTime);
				replayResolutions.add(obj);
				Log.d("zxd", "seekmediapath:" + obj.getMediaPath());
			}
			replayObj = new MediaObj(mediaObj.getTitle(), new ResolutionList(replayResolutions, 0), true);
		} catch (Exception e) {
			e.printStackTrace();
			replayObj = null;
		}
		replayItemVideo();
	}

	private void replayItemVideo() {
		CheckNetwork checkNetwork = new CheckNetwork(LiveNewsDetailActivity2.this);
		if (checkNetwork.check3GOnly(false, null) == CheckNetwork.MOBILE_ONLY) {
			AlertDialog.Builder builder = new AlertDialog.Builder(LiveNewsDetailActivity2.this);
			builder.setTitle("您现在使用的是3G网络，将耗费流量").setMessage("是否继续观看视频?");
			builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (replayObj != null) {
						if (replayObj.isResolutionMode()) {
							mVideoSource = replayObj.getResolutionList().getResolutions()
									.get(replayObj.getResolutionList().getResolutionIndex()).getMediaPath();
						} else {
							mVideoSource = replayObj.getMediaPath();
						}
						startPaly();
					}
				}
			}).setNegativeButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
		} else {
			// if (replayObj != null && mLivedetailVideoView != null) {
			// if (mLivedetailVideoView.isPlaying()) {
			// mLivedetailVideoView.stop();
			// // mLivedetailVideoView.pause();
			// }
			// mLivedetailVideoView.addMedia(replayObj, true, true);
			// // mLivedetailVideoView.play();
			// }
		}
	}

	private void setListPosition() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < guideList.length(); i++) {
						JSONObject object = guideList.getJSONObject(i);
						if (getGuideItemState(object) == GuideItemState.LIVE) {
							mGuide.setSelection(i);
							break;
						}
					}
				} catch (Exception e) {
				}
			}
		}, 1000);
	}

	@SuppressWarnings("deprecation")
	private void loadViewHolder(int position, View convertView, ViewHolderLiveNewsDetailGuide viewHolder) {
		viewHolder = (ViewHolderLiveNewsDetailGuide) convertView.getTag();
		try {
			JSONObject object = guideList.getJSONObject(position);
			String time = DateParse.getDate(0, 0, 0, 0, object.getString("starttime"), null, "HH:mm");
			viewHolder.getStartTime().setText(time + "  " + object.getString("Name"));
			Drawable drawable = getResources().getDrawable(R.drawable.radio_item_white_bg);
			viewHolder.program_list_item_container.setBackgroundDrawable(drawable);
			switch (getGuideItemState(object)) {
			case REPLAY:
				viewHolder.getState().setVisibility(View.VISIBLE);
				viewHolder.getControlBtn().setVisibility(View.GONE);
				viewHolder.getState().setTextColor(0xff000000);
				// if(!timeShift||mLastClickItem!=position||mTableIndex!=mLivedetailContentTab.getCurrentTab())
				if (!timeShift) {
					viewHolder.getState().setText("已播");
				} else {
					if (mLastClickItem != position || mTableIndex != mLivedetailContentTab.getCurrentTab()) {
						viewHolder.getState().setText("已播");
					} else {
						viewHolder.getState().setText("回看");
						drawable = getResources().getDrawable(R.drawable.huizhousarft_rectshape);
						viewHolder.program_list_item_container.setBackgroundDrawable(drawable);
						viewHolder.getState().setTextColor(getResources().getColor(R.color.white));
						viewHolder.getStartTime().setTextColor(getResources().getColor(R.color.white));
						viewHolder.getControlBtn().setImageResource(R.drawable.radio_play_white);
						viewHolder.getStartTime().setTextColor(getResources().getColor(R.color.white));
						break;
					}
				}
				viewHolder.getStartTime().setTextColor(getResources().getColor(R.color.home_tab_text_normal));
				break;
			case COMING_SOON:
				viewHolder.getState().setVisibility(View.VISIBLE);
				viewHolder.getControlBtn().setVisibility(View.GONE);
				viewHolder.getState().setTextColor(0xff000000);
				viewHolder.getState().setText("");
				viewHolder.getStartTime().setTextColor(0xff000000);
				// viewHolder.getState().setTextColor(0xffafafaf);
				// viewHolder.getState().setText("预告");
				// viewHolder.getStartTime().setTextColor(0xffafafaf);
				break;
			case LIVE:
				viewHolder.getState().setVisibility(View.GONE);
				viewHolder.getControlBtn().setVisibility(View.VISIBLE);
				if (!mIsSeekFlag) {
					drawable = getResources().getDrawable(R.drawable.huizhousarft_rectshape);
					viewHolder.program_list_item_container.setBackgroundDrawable(drawable);
					viewHolder.getState().setTextColor(0xFFFFFF);
					viewHolder.getStartTime().setTextColor(0xFFFFFF);
					viewHolder.getControlBtn().setImageResource(R.drawable.radio_play_white);
					viewHolder.getStartTime().setTextColor(getResources().getColor(R.color.white));
				} else {
					viewHolder.getControlBtn().setImageResource(R.drawable.livenewsdetail_guide_play_icon);
					viewHolder.getStartTime().setTextColor(getResources().getColor(R.color.home_tab_text_focus));
					viewHolder.getStartTime().setTextColor(getResources().getColor(R.color.home_tab_text_focus));
				}

				// viewHolder.getControlBtn().setImageResource(R.drawable.livenewsdetail_guide_play_icon);
				// viewHolder.getStartTime().setTextColor(getResources().getColor(R.color.home_tab_text_focus));
				break;
			}
		} catch (Exception e) {
			convertView = null;
		}
	}

	private void initPlayer() {
		mViewHolder = (RelativeLayout) playerView.findViewById(R.id.view_holder);
		mControllerHolder = (LinearLayout) playerView.findViewById(R.id.controller_holder);
		mProgress = (SeekBar) playerView.findViewById(R.id.media_progress);
		mProgress.setVisibility(View.INVISIBLE);
		mDuration = (TextView) playerView.findViewById(R.id.time_total);
		mDuration.setVisibility(View.INVISIBLE);
		mCurrPostion = (TextView) playerView.findViewById(R.id.time_current);
		mPlaybtn = (ImageButton) playerView.findViewById(R.id.play_btn);
		mFullScreenBtn = (ImageButton) playerView.findViewById(R.id.media_fullscreen);
		mFullScreenBtn.setVisibility(View.INVISIBLE);

		registerCallbackForControl();
		/**
		 * 设置ak
		 */
		BVideoView.setAK(MConfig.AK);

		/**
		 * 创建BVideoView和BMediaController
		 */
		mVV = new BVideoView(this);
		mViewHolder.addView(mVV);
		/**
		 * 注册listener
		 */
		mVV.setOnPreparedListener(this);
		mVV.setOnCompletionListener(this);
		mVV.setOnCompletionWithParamListener(this);
		mVV.setOnErrorListener(this);
		mVV.setOnInfoListener(this);
		/**
		 * 设置解码模式
		 */
		mVV.setDecodeMode(BVideoView.DECODE_SW);
		/**
		 * 开启后台事件处理线程
		 */
		mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
		mHandlerThread.start();
		mEventHandler = new EventHandler(mHandlerThread.getLooper());
	}

	/**
	 * 为控件注册回调处理函数
	 */
	private void registerCallbackForControl() {
		mPlaybtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (mVV.isPlaying()) {
					mPlaybtn.setImageResource(R.drawable.play);
					/**
					 * 暂停播放
					 */
					mVV.pause();
				} else {
					mPlaybtn.setImageResource(R.drawable.pause);
					/**
					 * 继续播放
					 */
					mVV.resume();
				}

			}
		});
		OnSeekBarChangeListener osbc1 = new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// Log.v(TAG, "progress: " + progress);
				updateTextViewWithTimeFormat(mCurrPostion, progress);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				/**
				 * SeekBar开始seek时停止更新
				 */
				mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				int iseekPos = seekBar.getProgress();
				/**
				 * SeekBark完成seek时执行seekTo操作并更新界面
				 *
				 */
				mVV.seekTo(iseekPos);
				Log.v(TAG, "seek to " + iseekPos);
				mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
			}
		};
		mProgress.setOnSeekBarChangeListener(osbc1);
	}

	private void updateTextViewWithTimeFormat(TextView view, int second) {
		int hh = second / 3600;
		int mm = second % 3600 / 60;
		int ss = second % 60;
		String strTemp = null;
		if (0 != hh) {
			strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
		} else {
			strTemp = String.format("%02d:%02d", mm, ss);
		}
		view.setText(strTemp);
	}

	@Override
	public void onPause() {
		super.onPause();
		/**
		 * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
		 */
		if (mVV.isPlaying() && (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
			mLastPos = (int) mVV.getCurrentPosition();
			mPlaybtn.setImageResource(R.drawable.play);
			mVV.pause();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v(TAG, "onStop");
		// 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
		if (mVV.isPlaying() && (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
			mLastPos = (int) mVV.getCurrentPosition();
			// don't stop pause
			// mVV.stopPlayback();
			mPlaybtn.setImageResource(R.drawable.play);
			mVV.pause();
		}
		if (toast != null) {
			toast.cancel();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != wakeLock) {
			wakeLock.release();
		}

		guideMap.clear();
		guideMap = null;
		mLivedetailContentTab.clearAllTabs();
		mLivedetailContentTab = null;
		isDisposed = true;
		if ((mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
			mLastPos = (int) mVV.getCurrentPosition();
			mVV.stopPlayback();
		}
		if (toast != null) {
			toast.cancel();
		}
		/**
		 * 结束后台事件处理线程
		 */
		mHandlerThread.quit();
		Log.v(TAG, "onDestroy");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v(TAG, "onResume");
		if (null != mWakeLock && (!mWakeLock.isHeld())) {
			mWakeLock.acquire();
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@SuppressWarnings("deprecation")
	private GuideItemState getGuideItemState(JSONObject object) {
		Date startTime;
		try {
			startTime = DateParse.parseDate(object.getString("starttime"), null);
			Date endTime = DateParse.parseDate(object.getString("endtime"), null);
			if (endTime.before(startTime)) // 解决那种节目单时间不对的问题 结束时间小于开始时间的
			{
				endTime.setDate(endTime.getDate() + 1);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
				String newEndTime = dateFormat.format(endTime);
				object.put("endtime", newEndTime);
				endTime = DateParse.parseDate(object.getString("endtime"), null);
			}
			Date nowTime = new Date(System.currentTimeMillis());
			if (nowTime.getTime() > endTime.getTime()) {
				return GuideItemState.REPLAY;
			} else if (nowTime.getTime() < startTime.getTime()) {
				return GuideItemState.COMING_SOON;
			} else {
				return GuideItemState.LIVE;
			}
		} catch (Exception e) {
			return null;
		}
	}

	private void getOnlineVideoPath(final int position) {
		CheckNetwork checkNetwork = new CheckNetwork(LiveNewsDetailActivity2.this);
		if (checkNetwork.check3GOnly(false, null) == CheckNetwork.MOBILE_ONLY) {
			AlertDialog.Builder builder = new AlertDialog.Builder(LiveNewsDetailActivity2.this);
			builder.setTitle("您现在使用的是3G网络，将耗费流量").setMessage("是否继续观看视频?");
			builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					playOnlineVideo(position);
				}
			}).setNegativeButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
		} else {
			playOnlineVideo(position);
		}
	}

	private void playOnlineVideo(int position) {
		if (null != mVideoSource && !"".equals(mVideoSource)) {
			startPaly();
		} else {
			getRtspSource();
		}
//		if (mediaObj != null) {
//			if (mediaObj.isResolutionMode()) {
//				mVideoSource = mediaObj.getResolutionList().getResolutions()
//						.get(mediaObj.getResolutionList().getResolutionIndex()).getMediaPath();
//			} else {
//				mVideoSource = mediaObj.getMediaPath();
//			}
//			// 发起一次播放任务,当然您不一定要在这发起
//			startPaly();
//		} else {
//			getRtspSource();
//		}
	}

	private void startPaly() {
		/**
		 * 如果已经开始播放，先停止播放
		 */
		if (mVV.isPlaying()) {
			mVV.stopPlayback();
		} else if (!mVV.isPlaying() && (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
			mVV.resume();
		} else {
			/**
			 * 发起一次新的播放任务
			 */
			if (mEventHandler.hasMessages(EVENT_PLAY)) {
				mEventHandler.removeMessages(EVENT_PLAY);
			}
			mEventHandler.sendEmptyMessage(EVENT_PLAY);
		}
	}

	private void getTvGuide(final int day, final GuideListLoadListener listener) {
		listener.onPre();
		if (guideMap.containsKey(DateParse.getDate(day, 0, 0, 0, null, null, "yyyyMMdd"))) {
			guideList = guideMap.get(DateParse.getDate(day, 0, 0, 0, null, null, "yyyyMMdd"));
			listener.onOk();
			setListPosition();
			return;
		}
		News.getArticleById(mArticalId, mCatalogId, mUserName, MConfig.TerminalType,
				DateParse.getDate(day, 0, 0, 0, null, null, "yyyyMMdd"), this, new OnJsonArrayResultListener() {
					@Override
					public void onOK(JSONArray result) {
						if (isDisposed)
							return;
						try {
							JSONArray guideArray = result.getJSONObject(0).getJSONArray("staticfilepaths");
							for (int i = 0; i < guideArray.length(); i++) {
								News.getTvGuide(
										((JSONObject) guideArray.get(i)).getString("pointpath")
												+ DateParse.getDate(day, 0, 0, 0, null, null, "yyyyMMdd") + ".json",
										LiveNewsDetailActivity2.this, new OnJsonArrayResultListener() {
											@Override
											public void onOK(JSONArray result) {
												if (isDisposed)
													return;
												guideMap.put(DateParse.getDate(day, 0, 0, 0, null, null, "yyyyMMdd"),
														result);
												guideList = result;
												listener.onOk();
												setListPosition();
											}

											@Override
											public void onNG(String reason) {
												listener.onFail(reason);
											}

											@Override
											public void onCancel() {
												listener.onFail("getTvGuide onCancel()");
												// TODO
											}
										});
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onNG(String reason) {
						listener.onFail(reason);
					}

					@Override
					public void onCancel() {
						listener.onFail("getArticleById onCancel()");
					}
				});
	}

	private void getRtspSource() {
		News.getArticleById(mArticalId, mCatalogId, mUserName, MConfig.TerminalType,
				DateParse.getDate(0, 0, 0, 0, null, null, "yyyyMMdd"), this, new OnJsonArrayResultListener() {
					@Override
					public void onOK(JSONArray result) {
						if (isDisposed)
							return;
						try {// 这是去取到一个台的json文件地址 然后再去请求这个json地址 再得到播放地址
							JSONArray guideArray = result.getJSONObject(0).getJSONArray("staticfilepaths");
							mVideoSource = ((JSONObject) guideArray.get(0)).getString("playerpath");
//							for (int i = 0; i < guideArray.length(); i++) {
//								News.getTvGuide(((JSONObject) guideArray.get(i)).getString("playerpath"),
//										LiveNewsDetailActivity2.this, new OnJsonArrayResultListener() {
//											@Override
//											public void onOK(JSONArray result) {// 这返回的数据是取播放地址的
//												if (isDisposed)
//													return;
//												try {
//													resolutionObjs.clear();
//													for (int i = 0; i < result.length(); i++) {
//														// m3u8的
//														JSONArray jsonArray = result.getJSONObject(i)
//																.getJSONArray("C_Address");
//														// flv的
//														// JSONArray
//														// jsonArray=result.getJSONObject(i).getJSONArray("C_ScreenShot");
//														JSONObject item = result.getJSONObject(i);
//														if (item.has("TimeShift")) // 判断有无支持时移标签
//														{
//															if ("0".equals(item.getString("TimeShift"))) {
//																timeShift = false;
//															} else {
//																timeShift = true;
//															}
//														} else {
//															timeShift = false;
//														}
//														// timeShift=true;//让他默认支持
//														// 彭先凯的代码没提交
//														for (int j = 0; j < jsonArray.length(); j++) {
//															resolutionObjs.add(new ResolutionObj(
//																	((JSONObject) jsonArray.optJSONObject(j))
//																			.optString("title"),
//																	((JSONObject) jsonArray.optJSONObject(j))
//																			.optString("url")));
//															// resolutionObjs.add(new
//															// ResolutionObj(((JSONObject)
//															// jsonArray.optJSONObject(j)).optString("title"),
//															// "rtmp://liveh2.sobeycache.com/live/e8e5530ca3e744f1867f208876a84c18"));
//														}
//														mediaObj = new MediaObj(
//																result.optJSONObject(i).optString("C_Name"),
//																new ResolutionList(resolutionObjs, 0), true);
//													}
//												} catch (Exception e) {
//													mediaObj = null;
//												}
//											}
//
//											@Override
//											public void onNG(String reason) {
//												mediaObj = null;
//											}
//
//											@Override
//											public void onCancel() {
//												mediaObj = null;
//											}
//										});
//							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onNG(String reason) {
						mediaObj = null;
					}

					@Override
					public void onCancel() {
						mediaObj = null;
					}
				});
	}

	private interface GuideListLoadListener {
		void onPre();

		void onOk();

		void onFail(String reason);
	}

	/**
	 * 百度播放器
	 */
	@Override
	public void OnCompletionWithParam(int arg0) {

	}

	@Override
	public void onPlayingBufferCache(int arg0) {

	}

	@Override
	public boolean onInfo(int what, int arg1) {
		switch (what) {
		/**
		 * 开始缓冲
		 */
		case BVideoView.MEDIA_INFO_BUFFERING_START:
			Log.i(TAG, "caching start,now playing url : " + mVV.getCurrentPlayingUrl());

			break;
		/**
		 * 结束缓冲
		 */
		case BVideoView.MEDIA_INFO_BUFFERING_END:
			Log.i(TAG, "caching start,now playing url : " + mVV.getCurrentPlayingUrl());

			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onError(int arg0, int arg1) {
		Log.v(TAG, "onError");
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
		return true;
	}

	@Override
	public void onCompletion() {
		Log.v(TAG, "onCompletion");

		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
	}

	@Override
	public void onPrepared() {
		Log.v(TAG, "onPrepared");
		mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
		mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
	}
}
