package com.appsdk.video.gui;

import com.appsdk.video.AdvancedVideoView;
import com.appsdk.video.listener.AdvancedVideoViewListener;
import com.appsdk.video.listener.FullscreenAdvertiseListener;
import com.appsdk.video.obj.MediaObj;
import com.appsdk.video.utils.WeakHandler;
import com.third.library.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class AdvancedVideoViewControler extends RelativeLayout {

	private static final int ADD_VIEW = 1;
	private static final int INIT_VIEW = 2;
	private static final int SHOW_CONTROLER_VIEW = 3;
	private static final int HIDE_CONTROLER_VIEW = 4;
	private static final int SHOW_LOADING_VIEW = 5;
	private static final int HIDE_LOADING_VIEW = 6;
	private static final int SHOW_PAUSE_AD_VIEW = 7;
	private static final int HIDE_PAUSE_AD_VIEW = 8;
	private static final int SHOW_FULLSCREEN_AD_VIEW = 9;
	private static final int HIDE_FULLSCREEN_AD_VIEW = 10;
	private static final int SHOW_ERROR_VIEW = 11;
	private static final int HIDE_ERROR_VIEW = 12;
	private static final int TOGGLE_FULL_SCREEN = 13;
	private static final int TOGGLE_FULL_SCREEN_ENABLE = 14;
	private static final int AUTO_HIDE_CONTROLER_ENABLE = 15;
	private static final int SET_VOLUME_SEEKBAR = 16;
	private static final int CAN_SHOW_CONTROLER_VIEW = 17;
	private static final int SET_SILENT_MODE = 18;

	// System
	private Context mContext;
	private AdvancedVideoView mVideoView;
	private LayoutInflater mInflater;
	private MediaObj mMediaObj;
	private ControlHandler mHandler;
	// View
	private RelativeLayout mControlerLayout;
	private ControlerView mControlerView;
	private RelativeLayout mLoadingLayout;
	private LoadingView mLoadingView;
	private PauseAdvertiseLayout mPauseAdvertiseLayout;
	private PauseAdvertiseView mPauseAdvertiseView;
	private RelativeLayout mFullscreenAdvertiseLayout;
	private FullscreenAdvertiseView mFullscreenAdvertiseView;
	private RelativeLayout mErrorLayout;
	private ErrorView mErrorView;
	// Variable
	private FullscreenAdvertiseListener mFullscreenAdvertiseListener;

	public AdvancedVideoViewControler(Context context) {
		this(context, null);
	}

	public AdvancedVideoViewControler(Context context, AttributeSet attrs) {
		this(context, null, 0);
	}

	public AdvancedVideoViewControler(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mHandler = new ControlHandler(this);
		mHandler.sendEmptyMessage(ADD_VIEW);
	}

	public void init(AdvancedVideoView videoView) {
		mVideoView = videoView;
		mHandler.sendEmptyMessage(INIT_VIEW);
	}

	public void setControlerViewConfigure(MediaObj mediaObj) {
		mMediaObj = mediaObj;
		if (mControlerView != null) {
			mControlerView.setConfigure(mediaObj);
			if (!mMediaObj.isShowControler()) {
				hideControlerView();
			} else {
				showControlerView();
			}
		}
	}

	public void setVolumeSeekBar(int volume) {
		Message msg = new Message();
		msg.what = SET_VOLUME_SEEKBAR;
		Bundle data = new Bundle();
		data.putInt("set_volume_seekbar", volume);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	public void setSilentMode(boolean isSilent) {
		Message msg = new Message();
		msg.what = SET_SILENT_MODE;
		Bundle data = new Bundle();
		data.putBoolean("set_silent_mode", isSilent);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}
	
	public boolean isControlerViewShown() {
		if (mControlerView != null) {
			return mControlerView.isControlerShown();
		}
		return false;
	}

	public void showControlerView() {
		mHandler.sendEmptyMessage(SHOW_CONTROLER_VIEW);
	}

	public void hideControlerView() {
		mHandler.sendEmptyMessage(HIDE_CONTROLER_VIEW);
	}

	public void canShowControlerView(boolean canShow) {
		Message msg = new Message();
		msg.what = CAN_SHOW_CONTROLER_VIEW;
		Bundle data = new Bundle();
		data.putBoolean("can_show_controler_view", canShow);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	public void showLoadingView(boolean isShowInfoImage) {
		Message msg = new Message();
		msg.what = SHOW_LOADING_VIEW;
		Bundle data = new Bundle();
		data.putBoolean("is_show_info_image", isShowInfoImage);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	public void hideLoadingView() {
		mHandler.sendEmptyMessage(HIDE_LOADING_VIEW);
	}

	/**
	 * This view will automatic hide when video is playing, can only effect
	 * while video is paused or stopped
	 * 
	 * @param adImageUrl
	 * @param adLinkUrl
	 * @param canClose
	 */
	public void showPauseAdvertiseView(String adImageUrl, String adLinkUrl, boolean canClose) {
		Message msg = new Message();
		msg.what = SHOW_PAUSE_AD_VIEW;
		Bundle data = new Bundle();
		data.putString("pause_ad_image_url", adImageUrl);
		data.putString("pause_ad_link_url", adLinkUrl);
		data.putBoolean("pause_ad_can_close", canClose);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	public void hidePauseAdvertiseView() {
		mHandler.sendEmptyMessage(HIDE_PAUSE_AD_VIEW);
	}
	
	/**
	 * This view will automatic hide when video is playing, can only effect
	 * while video is paused or stopped
	 * @param adImageUrl
	 * @param adLinkUrl
	 * @param canClose
	 * @param countDown
	 * @param listener
	 */
	public void showFullscreenAdvertiseView(String adImageUrl, String adLinkUrl, boolean canClose, int countDown, FullscreenAdvertiseListener listener) {
		mFullscreenAdvertiseListener = listener;
		Message msg = new Message();
		msg.what = SHOW_FULLSCREEN_AD_VIEW;
		Bundle data = new Bundle();
		data.putString("fullscreen_ad_image_url", adImageUrl);
		data.putString("fullscreen_ad_link_url", adLinkUrl);
		data.putBoolean("fullscreen_ad_can_close", canClose);
		data.putInt("fullscreen_ad_countdown", countDown);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	public void hideFullscreenAdvertiseView() {
		mHandler.sendEmptyMessage(HIDE_FULLSCREEN_AD_VIEW);
	}
	
	public void showErrorView() {
		mHandler.sendEmptyMessage(SHOW_ERROR_VIEW);
	}

	public void hideErrorView() {
		mHandler.sendEmptyMessage(HIDE_ERROR_VIEW);
	}

	public void destory() {
		mControlerView.destory();
	}

	public boolean isFullScreen() {
		if (mControlerView != null) {
			return mControlerView.isFullScreen();
		}
		return false;
	}

	public void toggleFullScreen() {
		if (mControlerView != null) {
			mHandler.sendEmptyMessage(TOGGLE_FULL_SCREEN);
		}
	}

	public void toggleFullScreenEnable(boolean enable) {
		Message msg = new Message();
		msg.what = TOGGLE_FULL_SCREEN_ENABLE;
		Bundle data = new Bundle();
		data.putBoolean("toggle_full_screen_enable", enable);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	public void autoHideControlerEnable(boolean enable) {
		Message msg = new Message();
		msg.what = AUTO_HIDE_CONTROLER_ENABLE;
		Bundle data = new Bundle();
		data.putBoolean("auto_hide_controler_enable", enable);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	private void initListener() {
		mVideoView.setOnAdvancedVideoViewControlerListener(new AdvancedVideoViewListener() {
			@Override
			public void onEvent(AdvancedVideoView videoView, int env) {
				if (mControlerView != null) {
					mControlerView.onEvent(videoView, env);
				}
				if (mLoadingView != null) {
					mLoadingView.onEvent(videoView, env);
				}
				if (mPauseAdvertiseView != null) {
					mPauseAdvertiseView.onEvent(videoView, env);
				}
				if (mFullscreenAdvertiseView != null) {
					mFullscreenAdvertiseView.onEvent(videoView, env);
				}
				if (mErrorView != null) {
					mErrorView.onEvent(videoView, env);
				}
			}
		});
	}

	private static class ControlHandler extends WeakHandler<AdvancedVideoViewControler> {
		public ControlHandler(AdvancedVideoViewControler owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			AdvancedVideoViewControler controler = getOwner();

			switch (msg.what) {
			case ADD_VIEW:
				controler.mControlerLayout = (RelativeLayout) controler.mInflater.inflate(R.layout.layout_controler, null);
				RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				controler.addView(controler.mControlerLayout, layoutParams1);
				controler.mControlerLayout.setVisibility(View.GONE);
				controler.mPauseAdvertiseLayout = (PauseAdvertiseLayout) controler.mInflater.inflate(R.layout.layout_pause_advertisement, null);
				RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
				controler.addView(controler.mPauseAdvertiseLayout, layoutParams2);
				controler.mPauseAdvertiseLayout.setVisibility(View.GONE);
				controler.mLoadingLayout = (RelativeLayout) controler.mInflater.inflate(R.layout.layout_loading, null);
				RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				controler.addView(controler.mLoadingLayout, layoutParams3);
				controler.mLoadingLayout.setVisibility(View.GONE);
				controler.mFullscreenAdvertiseLayout = (RelativeLayout) controler.mInflater.inflate(R.layout.layout_fullscreen_advertisement, null);
				RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				controler.addView(controler.mFullscreenAdvertiseLayout, layoutParams4);
				controler.mFullscreenAdvertiseLayout.setVisibility(View.GONE);
				controler.mErrorLayout = (RelativeLayout) controler.mInflater.inflate(R.layout.layout_error, null);
				RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				controler.addView(controler.mErrorLayout, layoutParams5);
				controler.mErrorLayout.setVisibility(View.GONE);
				break;

			case INIT_VIEW:
				controler.mControlerView = new ControlerView(controler.mContext, controler.mVideoView, controler.mControlerLayout);
				controler.mLoadingView = new LoadingView(controler.mContext, controler.mVideoView, controler.mLoadingLayout);
				controler.mPauseAdvertiseView = new PauseAdvertiseView(controler.mContext, controler.mVideoView, controler.mPauseAdvertiseLayout);
				controler.mFullscreenAdvertiseView = new FullscreenAdvertiseView(controler.mContext, controler.mVideoView, controler.mFullscreenAdvertiseLayout);
				controler.mErrorView = new ErrorView(controler.mContext, controler.mVideoView, controler.mErrorLayout);
				controler.initListener();
				break;

			case SHOW_CONTROLER_VIEW:
				controler.mControlerView.showControler();
				break;

			case HIDE_CONTROLER_VIEW:
				controler.mControlerView.hideControler();
				break;

			case SHOW_LOADING_VIEW:
				try {
					boolean isShowInfoImage = msg.getData().getBoolean("is_show_info_image");
					controler.mLoadingView.showLoadingView(isShowInfoImage);
				} catch (Exception e) {
				}
				break;

			case HIDE_LOADING_VIEW:
				controler.mLoadingView.hideLoadingView();
				break;

			case SHOW_PAUSE_AD_VIEW:
				try {
					String adImageUrl = msg.getData().getString("pause_ad_image_url");
					String adLinkUrl = msg.getData().getString("pause_ad_link_url");
					boolean canClose = msg.getData().getBoolean("pause_ad_can_close");
					controler.mPauseAdvertiseView.showPauseAdvertiseView(adImageUrl, adLinkUrl, canClose);
				} catch (Exception e) {
				}
				break;

			case HIDE_PAUSE_AD_VIEW:
				controler.mPauseAdvertiseView.hidePauseAdvertiseView();
				break;
				
			case SHOW_FULLSCREEN_AD_VIEW:
				try {
					String adImageUrl = msg.getData().getString("fullscreen_ad_image_url");
					String adLinkUrl = msg.getData().getString("fullscreen_ad_link_url");
					boolean canClose = msg.getData().getBoolean("fullscreen_ad_can_close");
					int countDown = msg.getData().getInt("fullscreen_ad_countdown");
					controler.mFullscreenAdvertiseView.showFullscreenAdvertiseView(adImageUrl, adLinkUrl, canClose, countDown, controler.mFullscreenAdvertiseListener);
				} catch (Exception e) {
				}
				break;

			case HIDE_FULLSCREEN_AD_VIEW:
				controler.mFullscreenAdvertiseView.hideFullscreenAdvertiseView();
				break;
				
			case SHOW_ERROR_VIEW:
				controler.mErrorView.showErrorView();
				break;

			case HIDE_ERROR_VIEW:
				controler.mErrorView.hideErrorView();
				break;
				
			case TOGGLE_FULL_SCREEN:
				controler.mControlerView.toggleFullScreen();
				break;

			case TOGGLE_FULL_SCREEN_ENABLE:
				try {
					if (controler.mControlerView != null) {
						boolean enable = msg.getData().getBoolean("toggle_full_screen_enable");
						controler.mControlerView.toggleFullScreenEnable(enable);
					}
				} catch (Exception e) {
				}
				break;

			case AUTO_HIDE_CONTROLER_ENABLE:
				try {
					if (controler.mControlerView != null) {
						boolean enable = msg.getData().getBoolean("auto_hide_controler_enable");
						controler.mControlerView.autoHideControlerEnable(enable);
					}
				} catch (Exception e) {
				}
				break;

			case SET_VOLUME_SEEKBAR:
				if (controler.mControlerView != null) {
					int volume = msg.getData().getInt("set_volume_seekbar");
					controler.mControlerView.setVolumeSeekBar(volume);
				}
				break;

			case CAN_SHOW_CONTROLER_VIEW:
				try {
					if (controler.mControlerView != null) {
						boolean canShow = msg.getData().getBoolean("can_show_controler_view");
						controler.mControlerView.canShowControler(canShow);
					}
				} catch (Exception e) {
				}
				break;

			case SET_SILENT_MODE:
				try {
					if (controler.mControlerView != null) {
						boolean isSilent = msg.getData().getBoolean("set_silent_mode");
						controler.mControlerView.setSilentMode(isSilent);
					}
				} catch (Exception e) {
				}
				break;
			}
			super.handleMessage(msg);
		}
	}
}
