package com.sobey.cloud.webtv;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.utils.MConfig;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class VideoNewsPlayerActivity extends BaseActivity implements OnPreparedListener, OnCompletionListener,
		OnErrorListener, OnInfoListener, OnPlayingBufferCacheListener, OnCompletionWithParamListener {
	private ArrayList<ResolutionObj> resolutionObjs = new ArrayList<ResolutionObj>();
	private MediaObj mediaObj;
	private JSONArray staticFilePaths;

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

	@Override
	public int getContentView() {
		return R.layout.activity_videonews_player;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		staticFilePaths = null;
		try {
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			String str = getIntent().getStringExtra("staticfilepaths");
			staticFilePaths = new JSONArray(str);
			// mNewsdetailVideoView.init();
			// mNewsdetailVideoView.toggleFullScreenEnable(false);
			// mNewsdetailVideoView.showLoadingView(true);
			initPalyer();
			loadVideo();
		} catch (Exception e) {
			Toast.makeText(VideoNewsPlayerActivity.this, "网络不给力,请稍后再试吧", Toast.LENGTH_SHORT).show();
			finishActivity();
		}
	}

	private void loadVideo() throws JSONException {
		if (staticFilePaths.length() > 0) {
			News.getVideoPath(((JSONObject) staticFilePaths.get(0)).getString("playerpath"), this,
					new OnJsonObjectResultListener() {
						@Override
						public void onOK(JSONObject result) {
							try {
								resolutionObjs.clear();
								String str = result.getString("playerUrl");
								str = str.substring(str.indexOf("{"));
								JSONObject obj = new JSONObject(str);
								JSONArray formatUriArray = ((JSONObject) obj.getJSONArray("clips").get(0))
										.getJSONArray("urls");
								JSONArray formatArray = obj.getJSONArray("formats");
								String host = obj.getString("host");
								String p2p = obj.getString("p2p");
								for (int i = 0; i < formatUriArray.length(); i++) {
									resolutionObjs.add(new ResolutionObj((String) formatArray.get(i),
											host + ((String) formatUriArray.get(i)) + p2p));
								}
								mediaObj = new MediaObj(obj.optString("title"), new ResolutionList(resolutionObjs, 0),
										true);
								// mVideoAdManager.setVideoAd(VideoNewsPlayerActivity.this,
								// mNewsdetailVideoView, mediaObj,
								// result.optInt("positionId", 0),
								// result.optString("catalogId"));
								if (mediaObj != null) {
									if (mediaObj.isResolutionMode()) {
										mVideoSource = mediaObj.getResolutionList().getResolutions()
												.get(mediaObj.getResolutionList().getResolutionIndex()).getMediaPath();
									} else {
										mVideoSource = mediaObj.getMediaPath();
									}
									startPaly();
								}
							} catch (Exception e) {
								Toast.makeText(VideoNewsPlayerActivity.this, "网络不给力,请稍后再试吧", Toast.LENGTH_SHORT).show();
								finishActivity();
							}
						}

						@Override
						public void onNG(String reason) {
							Toast.makeText(VideoNewsPlayerActivity.this, "网络不给力,请稍后再试吧", Toast.LENGTH_SHORT).show();
							finishActivity();
						}

						@Override
						public void onCancel() {
							Toast.makeText(VideoNewsPlayerActivity.this, "网络不给力,请稍后再试吧", Toast.LENGTH_SHORT).show();
							finishActivity();
						}
					});
		}
	}

	private void initPalyer() {
		mViewHolder = (RelativeLayout) playerView.findViewById(R.id.view_holder);
		mControllerHolder = (LinearLayout) playerView.findViewById(R.id.controller_holder);
		mProgress = (SeekBar) playerView.findViewById(R.id.media_progress);
		mDuration = (TextView) playerView.findViewById(R.id.time_total);
		mCurrPostion = (TextView) playerView.findViewById(R.id.time_current);
		mPlaybtn = (ImageButton) playerView.findViewById(R.id.play_btn);
		mFullScreenBtn = (ImageButton) playerView.findViewById(R.id.media_fullscreen);
		mFullScreenBtn.setVisibility(View.GONE);
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

	private long mTouchTime;
	private boolean barShow = true;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN)
			mTouchTime = System.currentTimeMillis();
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			long time = System.currentTimeMillis() - mTouchTime;
			if (time < 400) {
				updateControlBar(!barShow);
			}
		}

		return true;
	}

	public void updateControlBar(boolean show) {

		if (show) {
			mControllerHolder.setVisibility(View.VISIBLE);
		} else {
			mControllerHolder.setVisibility(View.INVISIBLE);
		}
		barShow = show;
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v(TAG, "onStop");
		// 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
		if (mVV.isPlaying() && (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
			mLastPos = (int) mVV.getCurrentPosition();
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

	private void startPaly() {
		/**
		 * 如果已经开始播放，先停止播放
		 */
		// mVideoSource =
		// "http://182.132.33.53/vodtongh.sobeycache.com/vod/2016/01/17/8aa3890ec0b34331be508cbecc59e429?fmt=h264_256k_mp4&slice=001&wsiphost=local";
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

	/**
	 * 百度播放器监听
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
