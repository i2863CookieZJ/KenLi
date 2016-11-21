package com.appsdk.video.gui;

import org.videolan.libvlc.EventHandler;

import com.appsdk.video.AdvancedVideoView;
import com.appsdk.video.listener.AdvancedVideoViewListener;
import com.appsdk.video.obj.MediaObj;
import com.appsdk.video.obj.ResolutionList;
import com.appsdk.video.utils.SharedPreferencesManager;
import com.appsdk.video.utils.WeakHandler;
import com.appsdk.video.verticalseekbar.VerticalSeekBar;
import com.third.library.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ControlerView implements AdvancedVideoViewListener {

	private static final int HIDE_DELAY = 10000;

	private static final int CONTROLER_SEEKBAR_CHANGE = 0;
	private static final int CONTROLER_HIDE = 1;

	// System
	private Context mContext;
	private AdvancedVideoView mVideoView;
	private RelativeLayout mControlerLayout;
	private ControlHandler mControlHandler;
	private AudioManager mAudioManager;
	// Variable
	private MediaObj mMediaObj;
	private boolean mFullScreenFlag = false;
	private boolean mCanSetFullScreen = true;
	private boolean mCanAutoHide = true;
	private boolean mCanShow = true;
	private boolean mIsPlaying = false;
	private int mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	// Widget
	private SeekBar mSeekBar;
	private VerticalSeekBar mVolumeSeekBar;
	private TextView mDurationTextView;
	private TextView mPlayedTextView;
	private RelativeLayout mTopbarLayout;
	private TextView mTitleTextView;
	private TextView mResolutionTextView;
	private RelativeLayout mResolutionLayout;
	private LinearLayout mResolutionListLayout;
	private ImageButton mPlayAndPauseBtn;
	private ImageButton mPlayCenterBtn;
	private ImageButton mRepeatCenterBtn;
	private ImageButton mFullScreenBtn;
	private ImageView mSoundBtn;

	public ControlerView(Context context, AdvancedVideoView videoView, RelativeLayout controlerLayout) {
		mContext = context;
		mVideoView = videoView;
		mControlerLayout = controlerLayout;
		mControlHandler = new ControlHandler(this);
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		initWidget();
		mControlHandler.sendEmptyMessage(CONTROLER_SEEKBAR_CHANGE);
		showControler();
	}

	/**
	 * 设置标题、分辨率
	 * 
	 * @param mediaObj
	 */
	public void setConfigure(MediaObj mediaObj) {
		try {
			mMediaObj = mediaObj;
			if (mMediaObj.isShowTitle()) {
				mTopbarLayout.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(mMediaObj.getTitle())) {
					mTitleTextView.setText(mMediaObj.getTitle());
				}
				if (mMediaObj.isResolutionMode()) {
					ResolutionList list = mMediaObj.getResolutionList();
					mResolutionTextView.setVisibility(View.VISIBLE);
					mResolutionTextView.setText(list.getResolutions().get(list.getResolutionIndex()).getTitle());
					mResolutionListLayout.removeAllViews();
					for (int i = 0; i < list.getResolutions().size(); i++) {
						TextView view = (TextView) ((Activity) mContext).getLayoutInflater().inflate(R.layout.listitem_resolution, null);
						view.setText(list.getResolutions().get(i).getTitle());
						mResolutionListLayout.addView(view);
						ResolutionItemClickListener listener = new ResolutionItemClickListener(i);
						view.setOnClickListener(listener);
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public void setVolumeSeekBar(int volume) {
		if (mVolumeSeekBar != null) {
			int progress = volume < 0 ? 0 : volume;
			progress = volume > mVolumeSeekBar.getMax() ? mVolumeSeekBar.getMax() : volume;
			mVolumeSeekBar.setProgress(progress);
		}
	}

	public void setSilentMode(boolean isSilent) {
		if (!isSilent) {
			mSoundBtn.setImageResource(R.drawable.soundenable);
			mVideoView.setVolume(SharedPreferencesManager.getVolume(mContext));
			mVolumeSeekBar.setProgress(SharedPreferencesManager.getVolume(mContext));
			SharedPreferencesManager.putSoundable(mContext, true);
		} else {
			SharedPreferencesManager.putVolume(mContext, mVideoView.getVolume());
			mSoundBtn.setImageResource(R.drawable.sounddisable);
			mVideoView.setVolume(0);
			mVolumeSeekBar.setProgress(0);
			SharedPreferencesManager.putSoundable(mContext, false);
		}
	}

	public void canShowControler(boolean canShow) {
		mCanShow = canShow;
		if (!mCanShow) {
			if (mControlerLayout.getVisibility() != View.GONE) {
				mControlerLayout.setVisibility(View.GONE);
			}
			if (mResolutionLayout != null && mResolutionLayout.getVisibility() == View.VISIBLE) {
				mResolutionLayout.setVisibility(View.GONE);
			}
		}
	}

	public boolean isControlerShown() {
		if (mControlerLayout.getVisibility() != View.GONE) {
			return true;
		} else {
			return false;
		}
	}

	public void showControler() {
		refreshVolume();
		if (mCanShow && (mMediaObj == null || mMediaObj.isShowControler()) && mControlerLayout.getVisibility() != View.VISIBLE) {
			mControlerLayout.setVisibility(View.VISIBLE);
		}
		cancelHideControlerDelay();
		hideControlerDelay();
	}

	public void hideControler() {
		if (mControlerLayout.getVisibility() != View.GONE) {
			mControlerLayout.setVisibility(View.GONE);
		}
		if (mResolutionLayout != null && mResolutionLayout.getVisibility() == View.VISIBLE) {
			mResolutionLayout.setVisibility(View.GONE);
		}
		cancelHideControlerDelay();
	}

	public void hideControlerDelay() {
		if (mCanAutoHide && mIsPlaying) {
			mControlHandler.sendEmptyMessageDelayed(CONTROLER_HIDE, HIDE_DELAY);
		}
	}

	public void cancelHideControlerDelay() {
		mControlHandler.removeMessages(CONTROLER_HIDE);
	}

	public void destory() {
		mControlHandler.removeMessages(CONTROLER_HIDE);
		mControlHandler.removeMessages(CONTROLER_SEEKBAR_CHANGE);
	}

	public boolean isFullScreen() {
		return mFullScreenFlag;
	}

	public void toggleFullScreen() {
		if (mCanSetFullScreen) {
			if (mFullScreenFlag) {
				mFullScreenFlag = false;
				mFullScreenBtn.setImageResource(R.drawable.full_screen);
				((Activity) mContext).setRequestedOrientation(mScreenOrientation);
				((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				mVideoView.cancelFullScreen();
			} else {
				mFullScreenFlag = true;
				mFullScreenBtn.setImageResource(R.drawable.full_screen_cancel);
				mScreenOrientation = ((Activity) mContext).getRequestedOrientation();
				((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				mVideoView.setFullScreen();
			}
		}
	}

	public void toggleFullScreenEnable(boolean enable) {
		mCanSetFullScreen = enable;
		if (mCanSetFullScreen) {
			mFullScreenBtn.setVisibility(View.VISIBLE);
		} else {
			mFullScreenBtn.setVisibility(View.GONE);
		}
	}

	public void autoHideControlerEnable(boolean enable) {
		mCanAutoHide = enable;
		cancelHideControlerDelay();
	}

	@Override
	public void onEvent(AdvancedVideoView videoView, int env) {
		switch (env) {
		case EventHandler.MediaParsedChanged:
			mIsPlaying = false;
			mRepeatCenterBtn.setVisibility(View.GONE);
			mPlayCenterBtn.setVisibility(View.GONE);
			showControler();
			break;
		case EventHandler.MediaPlayerPlaying:
			mIsPlaying = true;
			mSeekBar.setMax(Math.round(mVideoView.getLength() / 1000));
			mDurationTextView.setText(formatTimeString(mVideoView.getLength()));
			mPlayAndPauseBtn.setImageResource(R.drawable.pause);
			mPlayCenterBtn.setVisibility(View.GONE);
			hideControlerDelay();
			break;
		case EventHandler.MediaPlayerStopped:
		case EventHandler.MediaPlayerPaused:
			mIsPlaying = false;
			mPlayAndPauseBtn.setImageResource(R.drawable.play);
			mPlayCenterBtn.setVisibility(View.VISIBLE);
			showControler();
			cancelHideControlerDelay();
			break;
		case EventHandler.MediaPlayerEndReached:
			mPlayCenterBtn.setVisibility(View.GONE);
			mRepeatCenterBtn.setVisibility(View.VISIBLE);
			showControler();
			cancelHideControlerDelay();
			break;
		}
	}

	private void initWidget() {
		mSeekBar = (SeekBar) mControlerLayout.findViewById(R.id.seekbar);
		mVolumeSeekBar = (VerticalSeekBar) mControlerLayout.findViewById(R.id.volume_seekbar);
		mDurationTextView = (TextView) mControlerLayout.findViewById(R.id.duration);
		mPlayedTextView = (TextView) mControlerLayout.findViewById(R.id.has_played);
		mTopbarLayout = (RelativeLayout) mControlerLayout.findViewById(R.id.topbar_layout);
		mTitleTextView = (TextView) mControlerLayout.findViewById(R.id.title);
		mResolutionTextView = (TextView) mControlerLayout.findViewById(R.id.resolution);
		mResolutionLayout = (RelativeLayout) mControlerLayout.findViewById(R.id.resolution_layout);
		mResolutionListLayout = (LinearLayout) mControlerLayout.findViewById(R.id.resolution_list_layout);
		mPlayAndPauseBtn = (ImageButton) mControlerLayout.findViewById(R.id.play_pause_btn);
		mPlayCenterBtn = (ImageButton) mControlerLayout.findViewById(R.id.play_center_btn);
		mRepeatCenterBtn = (ImageButton) mControlerLayout.findViewById(R.id.repeat_center_btn);
		mFullScreenBtn = (ImageButton) mControlerLayout.findViewById(R.id.full_screen_btn);
		mSoundBtn = (ImageView) mControlerLayout.findViewById(R.id.sound_btn);

		// 进度条
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				if (fromUser) {
					if (mVideoView.isSeekable()) {
						mVideoView.setTime(progress * 1000);
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				cancelHideControlerDelay();
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				hideControlerDelay();
			}
		});

		// 音量条
		refreshVolume();
		mVolumeSeekBar.setMax(mVideoView.getMaxVolume());
		mVolumeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				if (progress >= 0 && progress <= mVideoView.getMaxVolume()) {
					if (mAudioManager != null) {
						mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
					}
				}
				if (progress > 0) {
					mSoundBtn.setImageResource(R.drawable.soundenable);
					SharedPreferencesManager.putSoundable(mContext, true);
				} else {
					mSoundBtn.setImageResource(R.drawable.sounddisable);
					SharedPreferencesManager.putSoundable(mContext, false);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				cancelHideControlerDelay();
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				hideControlerDelay();
			}
		});

		// 静音按钮
		mSoundBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setSilentMode(SharedPreferencesManager.getSoundable(mContext));
				cancelHideControlerDelay();
				hideControlerDelay();
			}
		});

		// 播放暂停键
		mPlayAndPauseBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mVideoView.isPlaying()) {
					mPlayAndPauseBtn.setImageResource(R.drawable.play);
					mPlayCenterBtn.setVisibility(View.VISIBLE);
					mVideoView.pause();
				} else {
					mPlayAndPauseBtn.setImageResource(R.drawable.pause);
					mPlayCenterBtn.setVisibility(View.GONE);
					mVideoView.play();
				}
				cancelHideControlerDelay();
				hideControlerDelay();
			}
		});

		// 中间播放键
		mPlayCenterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!mVideoView.isPlaying()) {
					mPlayAndPauseBtn.setImageResource(R.drawable.pause);
					mPlayCenterBtn.setVisibility(View.GONE);
					mVideoView.play();
				}
				cancelHideControlerDelay();
				hideControlerDelay();
			}
		});

		// 中间重播键
		mRepeatCenterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mRepeatCenterBtn.setVisibility(View.GONE);
				if (mVideoView != null) {
					mVideoView.replay();
				}
			}
		});

		// 分辨率
		mResolutionTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mResolutionLayout.getVisibility() == View.VISIBLE) {
					mResolutionLayout.setVisibility(View.GONE);
				} else {
					mResolutionLayout.setVisibility(View.VISIBLE);
				}
				cancelHideControlerDelay();
				hideControlerDelay();
			}
		});

		// TODO 全屏
		mFullScreenBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				toggleFullScreen();
				cancelHideControlerDelay();
				hideControlerDelay();
			}
		});
	}

	/**
	 * 分辨率下拉条点击事件
	 */
	private class ResolutionItemClickListener implements OnClickListener {
		private int index;

		public ResolutionItemClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			try {
				int resolutionIndex = mMediaObj.getResolutionList().getResolutionIndex();
				if (resolutionIndex != index) {
					mVideoView.stop();
					MediaObj mediaObj = mMediaObj;
					mediaObj.getResolutionList().setResolutionIndex(index);
					mVideoView.addMedia(mediaObj, true);
				}
				if (mResolutionLayout.getVisibility() == View.VISIBLE) {
					mResolutionLayout.setVisibility(View.GONE);
				} else {
					mResolutionLayout.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
			}
		}

	}

	private static class ControlHandler extends WeakHandler<ControlerView> {
		public ControlHandler(ControlerView owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			ControlerView controler = getOwner();
			switch (msg.what) {
			case CONTROLER_SEEKBAR_CHANGE:
				if (controler.mSeekBar != null) {
					int progress = Math.round(controler.mVideoView.getTime() / 1000);
					controler.mSeekBar.setProgress(progress);
				}
				if (controler.mPlayedTextView != null) {
					controler.mPlayedTextView.setText(controler.formatTimeString(controler.mVideoView.getTime()));
				}
				sendEmptyMessageDelayed(CONTROLER_SEEKBAR_CHANGE, 1000);
				break;
			case CONTROLER_HIDE:
				controler.hideControler();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	/**
	 * 更新音量
	 */
	private void refreshVolume() {
		if (mVideoView.getVolume() > 0) {
			mSoundBtn.setImageResource(R.drawable.soundenable);
			mVolumeSeekBar.setProgress(mVideoView.getVolume());
			SharedPreferencesManager.putSoundable(mContext, true);
		} else {
			mSoundBtn.setImageResource(R.drawable.sounddisable);
			mVolumeSeekBar.setProgress(0);
			SharedPreferencesManager.putSoundable(mContext, false);
		}
	}

	/**
	 * 格式化播放时长
	 * 
	 * @param time
	 * @return
	 */
	private String formatTimeString(long time) {
		try {
			int i = (int) (time / 1000);
			int minute = i / 60;
			int hour = minute / 60;
			int second = i % 60;
			minute %= 60;
			return String.format("%02d:%02d:%02d", hour, minute, second);
		} catch (Exception e) {
			return "";
		}
	}

}
