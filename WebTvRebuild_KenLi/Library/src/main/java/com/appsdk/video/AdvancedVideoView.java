package com.appsdk.video;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaList;

import com.appsdk.video.gui.AdvancedVideoViewControler;
import com.appsdk.video.listener.AdvancedVideoViewListener;
import com.appsdk.video.listener.FullscreenAdvertiseListener;
import com.appsdk.video.listener.FullscreenListener;
import com.appsdk.video.myenum.AdvancedVideoViewScaleType;
import com.appsdk.video.obj.MediaObj;
import com.appsdk.video.utils.SharedPreferencesManager;
import com.appsdk.video.utils.WeakHandler;
import com.appsdk.video.utils.mConfig;
import com.third.library.R;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class AdvancedVideoView extends RelativeLayout implements SurfaceHolder.Callback, IVideoPlayer {

	private static final int ERROR_RETURN_CODE = -1;
	private static final int SURFACE_SIZE = 1;
	private static final int PAUSE_DELAY = 2;
	private static final int LIBVLC_PLAY = 3;
	private static final int LIBVLC_PLAY_INDEX = 4;
	private static final int LIBVLC_PAUSE = 5;
	private static final int LIBVLC_STOP = 6;
	private static final int LIBVLC_SET_TIME = 7;
	private static final int LIBVLC_SET_RATE = 8;

	// System configure
	private Context mContext;
	private SurfaceView mVideoSurfaceView;
	private SurfaceHolder mVideoSurfaceHolder;
	private AdvancedVideoViewControler mControlerView;
	private AudioManager mAudioManager;
	private GestureDetector mGestureDetector;
	// Variable
	private MediaObj mMediaObj;
	private LayoutParams mLayoutParams;
	private float mAspectRatio = 0.0f;
	// VLC
	private LibVLC mLibVLC;
	private final Handler mHandler = new VideoPlayerHandler(this);
	private final VideoEventHandler mEventHandler = new VideoEventHandler(this);
	// Surface Size
	private int mHeight = 0;
	private int mWidth = 0;
	private int mVisibleHeight = 0;
	private int mVisibleWidth = 0;
	private int mSarNum = 0;
	private int mSarDen = 0;
	// Video Settings
	private AdvancedVideoViewScaleType mScaleType = AdvancedVideoViewScaleType.SURFACE_BEST_FIT;
	// Listener
	private AdvancedVideoViewListener mListener;
	private AdvancedVideoViewListener mLocalListener;
	private AdvancedVideoViewListener mControlerListener;
	private FullscreenListener mFullscreenListener;
	// Flag
	private boolean mHasOnPaused = false;
	private boolean mPlayNow = true;

	public AdvancedVideoView(Context context) {
		this(context, null);
	}

	public AdvancedVideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AdvancedVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		if (attrs != null) {
			mAspectRatio = attrs.getAttributeFloatValue(mConfig.XMLNS, "aspectRatio", 0.0f);
		}

		setBackgroundResource(R.color.background);
		mVideoSurfaceView = new SurfaceView(mContext);
		mVideoSurfaceHolder = mVideoSurfaceView.getHolder();
		RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams1.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(mVideoSurfaceView, layoutParams1);
		mControlerView = new AdvancedVideoViewControler(mContext);
		RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mControlerView, layoutParams2);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mAspectRatio > 0 && !isFullScreen()) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			height = (int) (width / mAspectRatio);
			this.setMeasuredDimension(width, height);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * Init AdvancedVideoView
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		init(true);
	}

	/**
	 * Add media url to media list
	 * 
	 * @param mediaObj
	 * @param playNow
	 */
	public void addMedia(MediaObj mediaObj, boolean playNow) {
		addMedia(mediaObj, playNow, false);
	}

	/**
	 * Add media url to media list
	 * 
	 * @param mediaObj
	 * @param playNow
	 * @param isShowLoadingInfo
	 *            whether show info tips in loading view
	 */
	public void addMedia(MediaObj mediaObj, boolean playNow, boolean isShowLoadingInfo) {
		if (mLibVLC != null) {
			mControlerView.canShowControlerView(false);
			mControlerView.showLoadingView(isShowLoadingInfo);

			mMediaObj = mediaObj;
			MediaList list = mLibVLC.getMediaList();
			list.clear();
			if (mMediaObj.isResolutionMode()) {
				list.add(new Media(mLibVLC, mMediaObj.getResolutionList().getResolutions().get(mMediaObj.getResolutionList().getResolutionIndex()).getMediaPath()), false);
			} else {
				list.add(new Media(mLibVLC, mMediaObj.getMediaPath()), false);
				
			}
			mPlayNow = playNow;
			play(list.size() - 1);
		}
	}

	/**
	 * Call this function in Activity onPause() event
	 */
	public void onPause() {
		if (mLibVLC != null) {
			mHasOnPaused = true;
			if (mLibVLC.isSeekable()) {
				SharedPreferencesManager.putPauseTime(mContext, mLibVLC.getTime());
			}
			if (isFullScreen()) {
				toggleFullScreen();
			}
			releaseVideoView();
		}
	}

	/**
	 * Call this function in Activity onResume() event
	 */
	public void onResume(final boolean autoPlay) {
		if (mHasOnPaused && mMediaObj != null) {
			try {
				init(false);
				mHasOnPaused = false;
				addMedia(mMediaObj, true);
				mLocalListener = new AdvancedVideoViewListener() {
					@Override
					public void onEvent(AdvancedVideoView videoView, int env) {
						if (env == EventHandler.MediaPlayerPlaying) {
							if (mLibVLC.isSeekable()) {
								setTime(SharedPreferencesManager.getPauseTime(mContext));
							}
							if (!autoPlay) {
								videoView.pause();
							}
							mLocalListener = null;
						}
					}
				};
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Call this function in Activity onDestory() event
	 */
	public void onDestory() {
		releaseVideoView();
		mVideoSurfaceHolder = null;
		mListener = null;
	}

	/**
	 * Call this function in Activity onConfigurationChanged(Configuration
	 * newConfig) event
	 */
	public void onConfigurationChanged() {
		setSurfaceSize(mWidth, mHeight, mVisibleWidth, mVisibleHeight, mSarNum, mSarDen);
	}

	/**
	 * Set on AdvancedVideoViewListener, you can get all video player state and
	 * event in it
	 * 
	 * @param listener
	 */
	public void setOnAdvancedVideoViewListener(AdvancedVideoViewListener listener) {
		mListener = listener;
	}

	public AdvancedVideoViewListener getAdvancedVideoViewListener() {
		return mListener;
	}

	private AdvancedVideoViewListener getAdvancedVideoViewLocalListener() {
		return mLocalListener;
	}

	public void setOnAdvancedVideoViewControlerListener(AdvancedVideoViewListener listener) {
		mControlerListener = listener;
	}

	private AdvancedVideoViewListener getAdvancedVideoViewControlerListener() {
		return mControlerListener;
	}

	public void play() {
		mHandler.sendEmptyMessage(LIBVLC_PLAY);
		mVideoSurfaceView.setKeepScreenOn(true);
	}

	public void play(int index) {
		if (index < mLibVLC.getMediaList().size() && index >= 0) {
			Message msg = new Message();
			msg.what = LIBVLC_PLAY_INDEX;
			Bundle data = new Bundle();
			data.putInt("libvlc_play_index", index);
			msg.setData(data);
			mHandler.sendMessage(msg);
			mVideoSurfaceView.setKeepScreenOn(true);
		}
	}

	public void replay() {
		if (mMediaObj != null) {
			addMedia(mMediaObj, true);
		}
	}

	public void pause() {
		mHandler.sendEmptyMessage(LIBVLC_PAUSE);
		mVideoSurfaceView.setKeepScreenOn(false);
	}

	public void stop() {
		mHandler.sendEmptyMessage(LIBVLC_STOP);
		mVideoSurfaceView.setKeepScreenOn(false);
	}

	public long getLength() {
		if (mLibVLC != null) {
			return mLibVLC.getLength();
		}
		return ERROR_RETURN_CODE;
	}

	public long getTime() {
		if (mLibVLC != null) {
			return mLibVLC.getTime();
		}
		return ERROR_RETURN_CODE;
	}

	public void setTime(long time) {
		Message msg = new Message();
		msg.what = LIBVLC_SET_TIME;
		Bundle data = new Bundle();
		data.putLong("libvlc_set_time", time);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	public boolean isSeekable() {
		if (mLibVLC != null) {
			return mLibVLC.isSeekable();
		}
		return false;
	}

	public int getMaxVolume() {
		if (mAudioManager != null) {
			return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		}
		return ERROR_RETURN_CODE;
	}

	public int getVolume() {
		if (mAudioManager != null) {
			return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		}
		return ERROR_RETURN_CODE;
	}

	public void setVolume(int volume) {
		if (mControlerView != null) {
			mControlerView.setVolumeSeekBar(volume);
		}
	}

	public float getRate() {
		if (mLibVLC != null) {
			return mLibVLC.getRate();
		}
		return ERROR_RETURN_CODE;
	}

	public void setRate(float rate) {
		Message msg = new Message();
		msg.what = LIBVLC_SET_RATE;
		Bundle data = new Bundle();
		data.putFloat("libvlc_set_rate", rate);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	public boolean isPlaying() {
		if (mLibVLC != null) {
			return mLibVLC.isPlaying();
		}
		return false;
	}

	public boolean isFullScreen() {
		if (mControlerView != null) {
			return mControlerView.isFullScreen();
		}
		return false;
	}

	public void toggleFullScreen() {
		if (mControlerView != null) {
			mControlerView.toggleFullScreen();
		}
	}

	public void toggleFullScreenEnable(boolean enable) {
		if (mControlerView != null) {
			mControlerView.toggleFullScreenEnable(enable);
		}
	}

	public void autoHideControlerEnable(boolean enable) {
		if (mControlerView != null) {
			mControlerView.autoHideControlerEnable(enable);
		}
	}

	/**
	 * Set scale type of the video view
	 * 
	 * @param scaleType
	 */
	public void setScaleType(AdvancedVideoViewScaleType scaleType) {
		mScaleType = scaleType;
		changeSurfaceSize();
	}

	public void showControlerView() {
		if (mControlerView != null) {
			mControlerView.showControlerView();
		}
	}

	public void hideControlerView() {
		if (mControlerView != null) {
			mControlerView.hideControlerView();
		}
	}

	public void showLoadingView(boolean isShowInfoImage) {
		if (mControlerView != null) {
			mControlerView.showLoadingView(isShowInfoImage);
		}
	}

	public void hideLoadingView() {
		if (mControlerView != null) {
			mControlerView.hideLoadingView();
		}
	}

	public void showPauseAdvertiseView(String adImageUrl, String adLinkUrl, boolean canClose) {
		if (mControlerView != null) {
			mControlerView.showPauseAdvertiseView(adImageUrl, adLinkUrl, canClose);
		}
	}

	public void hidePauseAdvertiseView() {
		if (mControlerView != null) {
			mControlerView.hidePauseAdvertiseView();
		}
	}

	public void showFullscreenAdvertiseView(String adImageUrl, String adLinkUrl, boolean canClose, int countDown, FullscreenAdvertiseListener listener) {
		if (mControlerView != null) {
			mControlerView.showFullscreenAdvertiseView(adImageUrl, adLinkUrl, canClose, countDown, listener);
		}
	}

	public void hideFullscreenAdvertiseView() {
		if (mControlerView != null) {
			mControlerView.hideFullscreenAdvertiseView();
		}
	}

	public void showErrorView() {
		if (mControlerView != null) {
			mControlerView.showErrorView();
		}
	}

	public void hideErrorView() {
		if (mControlerView != null) {
			mControlerView.hideErrorView();
		}
	}
	
	public void setOnFullScreenListener(FullscreenListener listener) {
		mFullscreenListener = listener;
	}

	/**
	 * inner function
	 */
	public void setFullScreen() {
		mLayoutParams = (LayoutParams) getLayoutParams();
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		int width = metrics.widthPixels > metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
		int height = metrics.widthPixels > metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;
		setLayoutParams(new LayoutParams(width, height));
		invalidate();
		if (mFullscreenListener != null) {
			mFullscreenListener.onFullscreen();
		}
	}

	/**
	 * inner function
	 */
	public void cancelFullScreen() {
		if (mLayoutParams != null) {
			setLayoutParams(mLayoutParams);
			invalidate();
			if (mFullscreenListener != null) {
				mFullscreenListener.onCancelFullscreen();
			}
		}
	}

	private void init(boolean clearFlag) throws Exception {
		if (clearFlag) {
			initSettings();

			// Init System
			mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		}

		// Init VLC
		mVideoSurfaceHolder.addCallback(this);
		mVideoSurfaceView.setKeepScreenOn(true);
		mVideoSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
		mLibVLC = LibVLC.getInstance();
		mLibVLC.setHardwareAcceleration(LibVLC.HW_ACCELERATION_DISABLED);
		mLibVLC.setSubtitlesEncoding("");
		mLibVLC.setAout(LibVLC.AOUT_OPENSLES);
		mLibVLC.setTimeStretching(true);
		mLibVLC.setChroma("RV32");
		mLibVLC.setVerboseMode(true);
		LibVLC.restart(mContext);
		EventHandler.getInstance().addHandler(mEventHandler);

		if (mLibVLC != null)
			mLibVLC.attachSurface(mVideoSurfaceHolder.getSurface(), this);

		// Init view
		mControlerView.init(this);

		// Init gesture detector
		mGestureDetector = new GestureDetector(mContext, new MySimpleOnGestureListener(this));
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});
	}

	private void releaseVideoView() {
		if (mLibVLC == null)
			return;
		EventHandler.getInstance().removeHandler(mEventHandler);
		mLibVLC.stop();
		mLibVLC.detachSurface();
		mLibVLC.closeAout();
		mLibVLC.destroy();
		mLibVLC = null;
		mControlerView.destory();
	}

	private void changeSurfaceSize() {
		int sw;
		int sh;

		// get screen size
		sw = getWidth();
		sh = getHeight();

		double dw = sw, dh = sh;
		// boolean isPortrait;

		// getWindow().getDecorView() doesn't always take orientation into
		// account, we have to correct the values
		// isPortrait = getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_PORTRAIT;
		//
		// if (sw > sh && isPortrait || sw < sh && !isPortrait) {
		// dw = sh;
		// dh = sw;
		// }
		if (mControlerView != null && mControlerView.isFullScreen() && sw < sh) {
			dw = sh;
			dh = sw;
		}

		// sanity check
		if (dw * dh == 0 || mWidth * mHeight == 0) {
			return;
		}

		// compute the aspect ratio
		double ar, vw;
		if (mSarDen == mSarNum) {
			/* No indication about the density, assuming 1:1 */
			vw = mVisibleWidth;
			ar = vw / (double) mVisibleHeight;
		} else {
			/* Use the specified aspect ratio */
			vw = mVisibleWidth * (double) mSarNum / mSarDen;
			ar = vw / (double) mVisibleHeight;
		}

		// compute the display aspect ratio
		double dar = dw / dh;

		switch (mScaleType) {
		case SURFACE_BEST_FIT:
			if (dar < ar)
				dh = dw / ar;
			else
				dw = dh * ar;
			break;
		case SURFACE_FIT_HORIZONTAL:
			dh = dw / ar;
			break;
		case SURFACE_FIT_VERTICAL:
			dw = dh * ar;
			break;
		case SURFACE_FILL:
			break;
		case SURFACE_16_9:
			ar = 16.0 / 9.0;
			if (dar < ar)
				dh = dw / ar;
			else
				dw = dh * ar;
			break;
		case SURFACE_4_3:
			ar = 4.0 / 3.0;
			if (dar < ar)
				dh = dw / ar;
			else
				dw = dh * ar;
			break;
		case SURFACE_ORIGINAL:
			dh = mVisibleHeight;
			dw = vw;
			break;
		}

		SurfaceView surface;
		SurfaceHolder surfaceHolder;

		surface = mVideoSurfaceView;
		surfaceHolder = mVideoSurfaceHolder;

		// force surface buffer size
		surfaceHolder.setFixedSize(mWidth, mHeight);

		// set display size
		LayoutParams lp = (LayoutParams) surface.getLayoutParams();
		lp.width = (int) Math.ceil(dw * mWidth / mVisibleWidth);
		lp.height = (int) Math.ceil(dh * mHeight / mVisibleHeight);
		surface.setLayoutParams(lp);

		surface.invalidate();
	}

	private static class VideoPlayerHandler extends WeakHandler<AdvancedVideoView> {
		public VideoPlayerHandler(AdvancedVideoView owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			AdvancedVideoView videoView = getOwner();
			if (videoView == null) // WeakReference could be GC'ed early
				return;

			switch (msg.what) {
			case SURFACE_SIZE:
				videoView.changeSurfaceSize();
				break;
			case PAUSE_DELAY:
				if (videoView.mLibVLC != null) {
					videoView.mLibVLC.pause();
					videoView.mControlerView.setSilentMode(false);
				}
				break;
			case LIBVLC_PLAY:
				if (videoView.mLibVLC != null && !videoView.mLibVLC.isPlaying()) {
					videoView.mLibVLC.play();
				}
				break;
			case LIBVLC_PLAY_INDEX:
				if (videoView.mLibVLC != null && !videoView.mLibVLC.isPlaying()) {
					int index = msg.getData().getInt("libvlc_play_index");
					videoView.mLibVLC.playIndex(index);
				}
				break;
			case LIBVLC_PAUSE:
				if (videoView.mLibVLC != null && videoView.mLibVLC.isPlaying()) {
					videoView.mLibVLC.pause();
				}
				break;
			case LIBVLC_STOP:
				if (videoView.mLibVLC != null && videoView.mLibVLC.isPlaying()) {
					videoView.mLibVLC.stop();
				}
				break;
			case LIBVLC_SET_TIME:
				if (videoView.mLibVLC != null && videoView.mLibVLC.isSeekable()) {
					long time = msg.getData().getLong("libvlc_set_time");
					videoView.mLibVLC.setTime(time);
				}
				break;
			case LIBVLC_SET_RATE:
				if (videoView.mLibVLC != null) {
					float rate = msg.getData().getFloat("libvlc_set_rate");
					videoView.mLibVLC.setRate(rate);
				}
				break;
			}
		}
	};

	private static class VideoEventHandler extends WeakHandler<AdvancedVideoView> {
		public VideoEventHandler(AdvancedVideoView owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			AdvancedVideoView videoView = getOwner();
			if (videoView == null)
				return;
			if (videoView.getAdvancedVideoViewListener() != null) {
				videoView.getAdvancedVideoViewListener().onEvent(videoView, msg.getData().getInt("event"));
			}
			if (videoView.getAdvancedVideoViewLocalListener() != null) {
				videoView.getAdvancedVideoViewLocalListener().onEvent(videoView, msg.getData().getInt("event"));
			}
			if (videoView.getAdvancedVideoViewControlerListener() != null) {
				videoView.getAdvancedVideoViewControlerListener().onEvent(videoView, msg.getData().getInt("event"));
			}
			switch (msg.getData().getInt("event")) {
			case EventHandler.MediaParsedChanged:
				if (videoView.mControlerView != null) {
					videoView.mControlerView.setControlerViewConfigure(videoView.mMediaObj);
					if (videoView.mMediaObj.isShowControler()) {
						videoView.mControlerView.canShowControlerView(true);
						videoView.mControlerView.showControlerView();
					}
				}
				if (!videoView.mPlayNow) {
					videoView.mPlayNow = true;
					videoView.mControlerView.setSilentMode(true);
					videoView.mHandler.sendEmptyMessageDelayed(PAUSE_DELAY, 200);
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
		if (width * height == 0)
			return;
		mWidth = width;
		mHeight = height;
		mVisibleWidth = visible_width;
		mVisibleHeight = visible_height;
		mSarDen = sar_den;
		mSarNum = sar_num;
		mHandler.sendEmptyMessage(SURFACE_SIZE);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		if (mLibVLC != null)
			mLibVLC.attachSurface(mVideoSurfaceHolder.getSurface(), this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		if (mLibVLC != null)
			mLibVLC.detachSurface();
	}

	private void initSettings() {
		SharedPreferencesManager.putPauseTime(mContext, 0);
	}

	private static class MySimpleOnGestureListener extends SimpleOnGestureListener {
		private AdvancedVideoView videoView;
		private int startX = 0;
		private int startY = 0;
		private long maxVideoDuration = 0;
		private long currentVideoDuration = 0;
		private int maxVolume = 0;
		private int startVolume = 0;

		public MySimpleOnGestureListener(AdvancedVideoView videoView) {
			this.videoView = videoView;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (videoView.mControlerView != null) {
				videoView.mControlerView.toggleFullScreen();
			}
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (videoView.mControlerView != null) {
				if (videoView.mControlerView.isControlerViewShown()) {
					videoView.mControlerView.hideControlerView();
				} else {
					videoView.mControlerView.showControlerView();
				}
			}
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			startX = (int) e.getX();
			startY = (int) e.getY();
			maxVolume = videoView.getMaxVolume();
			startVolume = videoView.getVolume();
			maxVideoDuration = videoView.getLength();
			currentVideoDuration = videoView.getTime();
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			int e1X = startX;
			int e1Y = startY;
			int e2X = (int) e2.getX();
			int e2Y = (int) e2.getY();
			int disX = Math.abs(e1X - e2X);
			int disY = Math.abs(e1Y - e2Y);
			float videoSensitivity = (float) 60000;
			float volumeSensitivity = (float) 1.0;
			if (disX > disY) {
				if (videoView.mMediaObj != null && videoView.mMediaObj.isShowControler()) {
					if ((e1X - e2X) < 0) {
						long upVideoDuration = 0;
						if (videoView.mControlerView.isFullScreen()) {
							DisplayMetrics metrics = videoView.mContext.getResources().getDisplayMetrics();
							int width = metrics.widthPixels > metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
							upVideoDuration = (long) ((float) currentVideoDuration + (float) disX * videoSensitivity / (float) width);
						} else {
							upVideoDuration = (int) ((float) currentVideoDuration + (float) disX * videoSensitivity / (float) videoView.getWidth());
						}
						videoView.setTime(upVideoDuration > maxVideoDuration ? maxVideoDuration : upVideoDuration);
						return true;
					} else {
						long downVideoDuration = 0;
						if (videoView.mControlerView.isFullScreen()) {
							DisplayMetrics metrics = videoView.mContext.getResources().getDisplayMetrics();
							int width = metrics.widthPixels > metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
							downVideoDuration = (long) ((float) currentVideoDuration - (float) disX * videoSensitivity / (float) width);
						} else {
							downVideoDuration = (long) ((float) currentVideoDuration - (float) disX * videoSensitivity / (float) videoView.getWidth());
						}
						videoView.setTime(downVideoDuration < 0 ? 0 : downVideoDuration);
						return true;
					}
				}
				return true;
			}
			// change volume
			else {
				if ((e1Y - e2Y) > 0) {
					int upVolume = 0;
					if (videoView.mControlerView.isFullScreen()) {
						DisplayMetrics metrics = videoView.mContext.getResources().getDisplayMetrics();
						int height = metrics.widthPixels > metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;
						upVolume = (int) ((float) disY * (float) maxVolume / (float) height * volumeSensitivity + (float) startVolume);
					} else {
						upVolume = (int) ((float) disY * (float) maxVolume / (float) videoView.getHeight() * volumeSensitivity + (float) startVolume);
					}
					upVolume = upVolume > startVolume ? upVolume : upVolume + 1;
					videoView.setVolume(upVolume > maxVolume ? maxVolume : upVolume);
					return true;
				} else {
					int downVolume = 0;
					if (videoView.mControlerView.isFullScreen()) {
						DisplayMetrics metrics = videoView.mContext.getResources().getDisplayMetrics();
						int height = metrics.widthPixels > metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;
						downVolume = (int) ((float) startVolume - (float) disY * (float) maxVolume / (float) height * volumeSensitivity);
					} else {
						downVolume = (int) ((float) startVolume - (float) disY * (float) maxVolume / (float) videoView.getHeight() * volumeSensitivity);
					}
					downVolume = downVolume < startVolume ? downVolume : downVolume - 1;
					videoView.setVolume(downVolume < 0 ? 0 : downVolume);
					return true;
				}
			}
		}
	}
}
