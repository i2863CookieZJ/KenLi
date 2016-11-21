package com.dylan.uiparts.videoplayer;

import java.util.LinkedList;

import com.dylan.uiparts.verticalseekbar.VerticalSeekBar;
import com.dylan.uiparts.videoplayer.VideoView.MySizeChangeLinstener;
import com.third.library.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class VideoPlayer extends Activity {

	private enum ScreenSize {
		SCREEN_DEFAULT, SCREEN_FULL
	}

	private final static int PROGRESS_CHANGED = 0;
	private final static int HIDE_CONTROLER = 1;
	private final static int SHOW_CONTROLER = 2;

	private static int VOLUME_DEFAULT = 5;

	private static int hideDelayTime = 6868;
	private int position = 0;
	private int resolution = 0;
	private static int maxVolume = 0;
	private static int currentVolume = 0;
	private static int screenWidth = 0;
	private static int screenHeight = 0;
	private static int videoViewWidth = 0;
	private static int videoViewHeight = 0;
	private int playedTime = 0;
	private static boolean isOnline = false;
	private boolean isChangedVideo = false;
	private static boolean isControllerShow = true;
	private boolean isPaused = false;
	private static boolean isFullScreen = false;
	private boolean isSilent = false;

	private String resolutionType = "";//"标清";
	private boolean isResolutionInit = false;
	private boolean isResolutionEnable = false;
	private boolean isLive = false;
	private boolean isLoading = false;

	// Configure
	private static boolean isFullScreenOnly = false;
	private boolean isPreAndNextButton = true;
	private boolean isDefaultSizeShowControl = true;
	private boolean mAutoPlay = false;
	private boolean mControlerEnable = true;

	private LinkedList<VideoInfo> playList = new LinkedList<VideoInfo>();
	private LinkedList<VideoInfo> uriList = new LinkedList<VideoInfo>();

	private GestureDetector mGestureDetector = null;
	private AudioManager mAudioManager = null;
	private Handler myHandler = null;

	private FullScreenListener mFullScreenListener = null;
	private FullScreenCancelListener mFullScreenCancelListener = null;

	private View videoViewControler = null;
	private View controlView = null;
	private ProgressBar loadingView = null;
	private PopupWindow controler = null;
	private VideoView videoView = null;
	private SeekBar seekBar = null;
	private VerticalSeekBar volumeSeekBar = null;
	private TextView durationTextView = null;
	private TextView playedTextView = null;
	private TextView titleTextView = null;
	private TextView resolutionTextView = null;
	private RelativeLayout resolutionLayout = null;
	private LinearLayout resolutionListLayout = null;
	private ImageButton preBtn = null;
	private ImageButton nextBtn = null;
	private ImageButton playAndPauseBtn = null;
	private ImageButton playCenterBtn = null;
	private ImageButton fullScreenBtn = null;
	private ImageView soundBtn = null;
	private ImageView volumeInfo = null;
	private ImageView seekbarInfo = null;

	private Activity context;

	public VideoPlayer(Activity activity, VideoView videoView, View videoViewControler) {
		this.context = activity;
		this.videoView = videoView;
		this.videoViewControler = videoViewControler;
		isFullScreenOnly = false;
		maxVolume = 0;
		currentVolume = 0;
		screenWidth = 0;
		screenHeight = 0;
		videoViewWidth = 0;
		videoViewHeight = 0;
		isOnline = false;
		isControllerShow = true;
		isFullScreen = false;
	}

	/**
	 * Set VideoPlayer's configure
	 * 
	 * @param fullScreenOnly
	 *            if true, the screen can only be full size
	 * @param preAndNextButton
	 *            whether have pre and next button
	 * @param defaultSizeShowControl
	 *            if true, the controler cannot be hide when in default size
	 */
	public void setConfigure(boolean fullScreenOnly, boolean preAndNextButton, boolean defaultSizeShowControl, boolean autoPlay) {
		isFullScreenOnly = fullScreenOnly;
		isPreAndNextButton = preAndNextButton;
		isDefaultSizeShowControl = defaultSizeShowControl;
		mAutoPlay = autoPlay;
	}

	public void playLocalFile(final LinkedList<VideoInfo> playList) {
		if(isFullScreenOnly) {
			setFullScreenStatus(false);
		}
		this.playList = playList;
		playLocalFile(position);
	}

	public void playOnlineFile(final LinkedList<VideoInfo> uriList, boolean isLive) {
		this.isLive = isLive;
		if(isFullScreenOnly) {
			setFullScreenStatus(false);
		}
		this.uriList = uriList;
		playOnlineFile();
	}

	public boolean onTouch(MotionEvent event) {
		if(isControllerShow) {
			return true;
		} else {
			return mGestureDetector.onTouchEvent(event);
		}
	}

	public void onPause() {
		playedTime = videoView.getCurrentPosition();
		setPlayStatus(false);
	}

	public void onResume() {
		if(!isChangedVideo && !isOnline) {
			videoView.seekTo(playedTime);
		} else {
			isChangedVideo = false;
		}
		if(videoView.isPlaying()) {
			setPlayStatus(false);
		}
		if(context.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	public void onDestroy() {
		if(controler.isShowing()) {
			controler.dismiss();
		}
		myHandler.removeMessages(PROGRESS_CHANGED);
		myHandler.removeMessages(HIDE_CONTROLER);
		if(videoView.isPlaying()) {
			videoView.stopPlayback();
		}
		playList.clear();
	}

	public interface FullScreenListener {
		public void clearScreen();
	}

	public interface FullScreenCancelListener {
		public void resumeScreen();
	}

	/**
	 * When full screen, you should set any other view gone,
	 * setVisibility(View.GONE)
	 * 
	 * @param fullScreenListener
	 */
	public void setFullScreenListener(FullScreenListener fullScreenListener) {
		mFullScreenListener = fullScreenListener;
	}

	/**
	 * When cancel full screen, you should let any other view visible again,
	 * setVisibility(View.VISIBLE)
	 * 
	 * @param fullScreenCancelListener
	 */
	public void setFullScreenCancelListener(FullScreenCancelListener fullScreenCancelListener) {
		mFullScreenCancelListener = fullScreenCancelListener;
	}

	@SuppressWarnings("deprecation")
	public void init() {
		controlView = context.getLayoutInflater().inflate(R.layout.controler, null);
		loadingView = (ProgressBar) controlView.findViewById(R.id.loading_icon);
		controler = new PopupWindow(controlView);
		seekBar = (SeekBar) controlView.findViewById(R.id.seekbar);
		volumeSeekBar = (VerticalSeekBar) controlView.findViewById(R.id.volume_seekbar);
		durationTextView = (TextView) controlView.findViewById(R.id.duration);
		playedTextView = (TextView) controlView.findViewById(R.id.has_played);
		titleTextView = (TextView) controlView.findViewById(R.id.title);
		resolutionTextView = (TextView) controlView.findViewById(R.id.resolution);
		resolutionLayout = (RelativeLayout) controlView.findViewById(R.id.resolution_layout);
		resolutionListLayout = (LinearLayout) controlView.findViewById(R.id.resolution_list_layout);
		preBtn = (ImageButton) controlView.findViewById(R.id.pre_btn);
		nextBtn = (ImageButton) controlView.findViewById(R.id.next_btn);
		playAndPauseBtn = (ImageButton) controlView.findViewById(R.id.play_pause_btn);
		playCenterBtn = (ImageButton) controlView.findViewById(R.id.play_center_btn);
		fullScreenBtn = (ImageButton) controlView.findViewById(R.id.full_screen_btn);
		soundBtn = (ImageView) controlView.findViewById(R.id.sound_btn);
		volumeInfo = (ImageView) controlView.findViewById(R.id.volume_info);
		seekbarInfo = (ImageView) controlView.findViewById(R.id.seekbar_info);

		mGestureDetector = new GestureDetector(new MySimpleOnGestureListener(this));
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		SharedPreferences settings = context.getSharedPreferences("settings", 0);
		currentVolume = settings.getInt("volume", VOLUME_DEFAULT);
		myHandler = new MyHandler(this);

		preBtn.setAlpha(0xBB);
		nextBtn.setAlpha(0xBB);
		playAndPauseBtn.setAlpha(0xBB);
		playCenterBtn.setAlpha(0xBB);
		fullScreenBtn.setAlpha(0xBB);
		soundBtn.setAlpha(findAlphaFromSound());

		getVideoViewSize();
		getScreenSize();
		setResolutionType(resolutionType);

		Looper.myQueue().addIdleHandler(new IdleHandler() {
			@Override
			public boolean queueIdle() {
				if(controler != null && videoView.isShown()) {
					controler.showAsDropDown(videoViewControler, 0, -videoViewHeight);
					controler.update(videoViewControler, videoViewWidth, videoViewHeight);
				}
				return false;
			}
		});

		controlView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});

		videoView.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer arg0) {
				int i = videoView.getDuration();
				seekBar.setMax(i);
				volumeSeekBar.setMax(maxVolume);
				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;
				durationTextView.setText(String.format("%02d:%02d:%02d", hour, minute, second));
				videoView.seekTo(1);
				hideControllerDelay();
				myHandler.sendEmptyMessage(PROGRESS_CHANGED);
			}
		});

		videoView.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				setPlayStatus(false);
				videoView.seekTo(1);
				if(isFullScreen) {
					setFullScreenStatus(true);
				}
			}
		});

		videoView.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				videoView.stopPlayback();
				isOnline = false;
				new AlertDialog.Builder(context).setTitle("错误").setMessage("无法播放该视频").setPositiveButton("确定", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						videoView.stopPlayback();
					}
				}).setCancelable(false).show();
				return false;
			}
		});

		videoView.setMySizeChangeLinstener(new MySizeChangeLinstener() {
			@Override
			public void doMyThings() {
				endLoading();
				cancelDelayHide();
				setFullScreenStatus(!isFullScreen);
				hideControllerDelay();
				isResolutionEnable = true;
				if(isFullScreenOnly) {
					cancelDelayHide();
					hideControllerForce();
					if(!mAutoPlay) {
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								showController();
							}
						}, 500);
					}
				}
				if(!isChangedVideo && !isOnline) {
					videoView.seekTo(playedTime);
				} else {
					isChangedVideo = false;
				}
				if(mAutoPlay) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							setPlayStatus(true);
						}
					}, 1000);
				}
			}
		});

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				if(fromUser) {
					if(!isOnline) {
						videoView.seekTo(progress);
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				cancelDelayHide();
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				hideControllerDelay();
			}
		});

		volumeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				hideControllerDelay();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				cancelDelayHide();
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updateVolume(progress);
			}
		});

		resolutionTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isResolutionEnable) {
					if(uriList.size() > 0) {
						if(!isResolutionInit) {
							for(int i = 0; i < uriList.get(position).getUriList().size(); i++) {
								TextView view = (TextView) context.getLayoutInflater().inflate(R.layout.videoplayer_resolution_listitem, null);
								view.setText(uriList.get(position).getFormatTypeList().get(i));
								resolutionListLayout.addView(view);
								ResolutionItemClickListener listener = new ResolutionItemClickListener(i);
								view.setOnClickListener(listener);
							}
							isResolutionInit = true;
						}
						if(resolutionLayout.getVisibility() == View.VISIBLE) {
							resolutionLayout.setVisibility(View.GONE);
						} else {
							resolutionLayout.setVisibility(View.VISIBLE);
						}
					}
				}
			}
		});

		if(isPreAndNextButton) {
			preBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(position < playList.size() && position > 0) {
						position--;
						playLocalFile(position);
					}
				}
			});

			nextBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(position < (playList.size() - 1) && position >= -1) {
						position++;
						playLocalFile(position);
					}
				}
			});
		} else {
			preBtn.setVisibility(View.GONE);
			nextBtn.setVisibility(View.GONE);
		}

		playAndPauseBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setPlayStatus(isPaused);
			}
		});

		playCenterBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setPlayStatus(true);
			}
		});

		fullScreenBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setFullScreenStatus(isFullScreen);
			}
		});

		soundBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isSilent = !isSilent;
				updateVolume(currentVolume);
			}
		});

		startLoading();
		setPlayStatus(false);
		updateVolume(currentVolume);
	}

	private void startLoading() {
		loadingView.setVisibility(View.VISIBLE);
		playCenterBtn.setVisibility(View.GONE);
		volumeInfo.setVisibility(View.VISIBLE);
		seekbarInfo.setVisibility(View.VISIBLE);
		isLoading = true;
	}
	
	private void endLoading() {
		loadingView.setVisibility(View.GONE);
		if(isPaused) {
			playCenterBtn.setVisibility(View.VISIBLE);
		}
		volumeInfo.setVisibility(View.GONE);
		seekbarInfo.setVisibility(View.GONE);
		isLoading = false;
	}

	public void onConfigurationChanged(Configuration newConfig) {
		cancelDelayHide();
		hideControllerForce();
		showController();
		hideControllerDelay();
	}

	private void setResolutionType(String type) {
		resolutionType = type;
		resolutionTextView.setText(type);
	}

	private void getVideoViewSize() {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		videoViewHeight = videoViewControler.getLayoutParams().height < 0 ? (metrics.heightPixels - 25) : videoViewControler.getLayoutParams().height;
		videoViewWidth = videoViewControler.getLayoutParams().width < 0 ? metrics.widthPixels : videoViewControler.getLayoutParams().width;
	}

	private void getScreenSize() {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		if(isFullScreen) {
			screenHeight = metrics.widthPixels > metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;
			screenWidth = metrics.widthPixels > metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
		} else {
			screenHeight = metrics.widthPixels > metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
			screenWidth = metrics.widthPixels > metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;
		}
	}
	
	public void setControllerEnable(boolean state) {
		if(state) {
			mControlerEnable = true;
		} else {
			hideControllerForce();
			mControlerEnable = false;
		}
	}

	private void hideControllerForce() {
		controler.dismiss();
		isControllerShow = false;
	}

	private void hideController() {
		if(!isDefaultSizeShowControl || isFullScreen) {
			if(controler.isShowing() && isPaused == false) {
				controler.dismiss();
				isControllerShow = false;
			}
		}
	}

	private void hideControllerDelay() {
		myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, hideDelayTime);
	}

	private void cancelDelayHide() {
		myHandler.removeMessages(HIDE_CONTROLER);
	}

	private void showController() {
		if(mControlerEnable) {
			if(isFullScreen) {
				controler.showAtLocation(videoView, Gravity.BOTTOM, 0, 0);
				controler.update(0, 0, screenWidth, screenHeight);
			} else {
				controler.showAsDropDown(videoViewControler, 0, -videoViewHeight);
				controler.update(videoViewControler, videoViewWidth, videoViewHeight);
			}
			updateVolume(currentVolume);
			isControllerShow = true;
		}
	}

	private void setVideoScale(ScreenSize screenSize) {
		switch (screenSize){
		case SCREEN_FULL:
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getScreenSize();
			int videoWidth = videoView.getVideoWidth();
			int videoHeight = videoView.getVideoHeight();
			int mWidth = screenWidth;
			int mHeight = screenHeight;
			if(videoWidth > 0 && videoHeight > 0) {
				if(videoWidth * mHeight > mWidth * videoHeight) {
					mHeight = mWidth * videoHeight / videoWidth;
				} else if(videoWidth * mHeight < mWidth * videoHeight) {
					mWidth = mHeight * videoWidth / videoHeight;
				} else {
				}
			}
			videoView.setVideoScale(mWidth, mHeight, 0, 0);
			break;
		case SCREEN_DEFAULT:
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getScreenSize();
			int videoWidth1 = videoView.getVideoWidth();
			int videoHeight1 = videoView.getVideoHeight();
			int mWidth1 = videoViewWidth;
			int mHeight1 = videoViewHeight;
			if(videoWidth1 > 0 && videoHeight1 > 0) {
				if(videoWidth1 * mHeight1 > mWidth1 * videoHeight1) {
					mHeight1 = mWidth1 * videoHeight1 / videoWidth1;
				} else if(videoWidth1 * mHeight1 < mWidth1 * videoHeight1) {
					mWidth1 = mHeight1 * videoWidth1 / videoHeight1;
				} else {
				}
			}
			int top = mHeight1 < videoViewHeight ? ((videoViewHeight - mHeight1) / 2) : 0;
			videoView.setVideoScale(mWidth1, mHeight1, top, 0);
			break;
		}
	}

	private int findAlphaFromSound() {
		if(mAudioManager != null) {
			int alpha = currentVolume * (0xCC - 0x55) / maxVolume + 0x55;
			return alpha;
		} else {
			return 0xCC;
		}
	}

	@SuppressWarnings("deprecation")
	private void updateVolume(int index) {
		if(mAudioManager != null) {
			if(isSilent) {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
				soundBtn.setImageResource(R.drawable.sounddisable);
			} else {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
				soundBtn.setImageResource(R.drawable.soundenable);
			}
			currentVolume = index;
			volumeSeekBar.setProgress(index);
			soundBtn.setAlpha(findAlphaFromSound());
			SharedPreferences settings = context.getSharedPreferences("settings", 0);
			Editor editor = settings.edit();
			editor.putInt("volume", index);
			editor.commit();
		}
	}

	private void setPlayStatus(boolean flag) {
		cancelDelayHide();
		if(flag) {
			videoView.start();
			playAndPauseBtn.setImageResource(R.drawable.pause);
			playCenterBtn.setVisibility(View.GONE);
			hideControllerDelay();
			isPaused = false;
		} else {
			videoView.pause();
			playAndPauseBtn.setImageResource(R.drawable.play);
			if(!isLoading) {
				playCenterBtn.setVisibility(View.VISIBLE);
			}
			isPaused = true;
		}
	}

	private void setFullScreenStatus(boolean flag) {
		if(flag) {
			if(isFullScreenOnly) {
				context.finish();
			} else {
				isFullScreen = false;
				if(mFullScreenCancelListener != null) {
					mFullScreenCancelListener.resumeScreen();
				}
				setVideoScale(ScreenSize.SCREEN_DEFAULT);
				fullScreenBtn.setImageResource(R.drawable.full_screen);
			}
		} else {
			isFullScreen = true;
			if(mFullScreenListener != null) {
				mFullScreenListener.clearScreen();
			}
			setVideoScale(ScreenSize.SCREEN_FULL);
			fullScreenBtn.setImageResource(R.drawable.full_screen_cancel);
		}
	}

	private void playLocalFile(int pos) {
		isOnline = false;
		setPlayStatus(false);
		if(playList.size() > 0) {
			videoView.setVideoPath(playList.get(pos).getPath());
			titleTextView.setText(playList.get(pos).getTitle());
		} else {
			Toast.makeText(context, "播放列表为空", Toast.LENGTH_SHORT).show();
		}
	}

	private void playOnlineFile() {
		if(isLive) {
			isOnline = true;
		} else {
			isOnline = false;
		}
		setPlayStatus(false);
		if(uriList.size() > 0) {
			videoView.stopPlayback();
			Uri uri = null;
			for(int i = 0; i < uriList.get(position).getUriList().size(); i++) {
				if(resolutionType.equalsIgnoreCase(uriList.get(position).getFormatTypeList().get(i))) {
					uri = uriList.get(position).getUriList().get(i);
					setResolutionType(uriList.get(position).getFormatTypeList().get(i));
					resolution = i;
					break;
				}
			}
			if(uri == null) {
				uri = uriList.get(position).getUriList().get(resolution);
				setResolutionType(uriList.get(position).getFormatTypeList().get(resolution));
			}
			videoView.setVideoURI(uri);
			videoView.requestFocus();
			titleTextView.setText(uriList.get(position).getTitle());
			isResolutionEnable = false;
		} else {
			Toast.makeText(context, "播放列表为空", Toast.LENGTH_SHORT).show();
		}
	}

	private static class MyHandler extends Handler {
		private VideoPlayer videoPlayer;

		public MyHandler(VideoPlayer videoPlayer) {
			this.videoPlayer = videoPlayer;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			case PROGRESS_CHANGED:
				int i = videoPlayer.videoView.getCurrentPosition();
				videoPlayer.seekBar.setProgress(i);
				if(isOnline) {
					int j = videoPlayer.videoView.getBufferPercentage();
					videoPlayer.seekBar.setSecondaryProgress(j * videoPlayer.seekBar.getMax() / 100);
				} else {
					videoPlayer.seekBar.setSecondaryProgress(0);
				}
				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;
				videoPlayer.playedTextView.setText(String.format("%02d:%02d:%02d", hour, minute, second));
				sendEmptyMessageDelayed(PROGRESS_CHANGED, 100);
				break;
			case HIDE_CONTROLER:
				videoPlayer.hideController();
				break;
			case SHOW_CONTROLER:
				videoPlayer.showController();
				break;
			}
			super.handleMessage(msg);
		}
	}

	private static class MySimpleOnGestureListener extends SimpleOnGestureListener {
		private VideoPlayer videoPlayer;
		private int startX = 0;
		private int startY = 0;
		private int startVideoDuration = 0;
		private int startVolume = 0;

		public MySimpleOnGestureListener(VideoPlayer videoPlayer) {
			this.videoPlayer = videoPlayer;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if(!isFullScreenOnly) {
				videoPlayer.setFullScreenStatus(isFullScreen);
			}
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if(!isControllerShow) {
				videoPlayer.showController();
				videoPlayer.hideControllerDelay();
			} else {
				videoPlayer.hideController();
			}
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			startX = (int) e.getX();
			startY = (int) e.getY();
			startVideoDuration = videoPlayer.seekBar.getProgress();
			startVolume = currentVolume;
			return super.onDown(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if(!isControllerShow) {
				videoPlayer.showController();
				videoPlayer.hideControllerDelay();
			}
			int e1X = startX;
			int e1Y = startY;
			int e2X = (int) e2.getX();
			int e2Y = (int) e2.getY();
			int disX = Math.abs(e1X - e2X);
			int disY = Math.abs(e1Y - e2Y);
			float videoSensitivity = (float) 60000;
			float volumeSensitivity = (float) 1.0;
			if(disX > disY) {
				int maxVideoDuration = videoPlayer.seekBar.getMax();
				int currentVideoDuration = startVideoDuration;
				if((e1X - e2X) < 0) {
					int upVideoDuration = 0;
					if(isFullScreen) {
						upVideoDuration = (int) ((float) currentVideoDuration + (float) disX * videoSensitivity / (float) screenWidth);
					} else {
						upVideoDuration = (int) ((float) currentVideoDuration + (float) disX * videoSensitivity / (float) videoViewWidth);
					}
					videoPlayer.videoView.seekTo(upVideoDuration > maxVideoDuration ? maxVideoDuration : upVideoDuration);
					videoPlayer.seekBar.setProgress(upVideoDuration > maxVideoDuration ? maxVideoDuration : upVideoDuration);
					return true;
				} else {
					int downVideoDuration = 0;
					if(isFullScreen) {
						downVideoDuration = (int) ((float) currentVideoDuration - (float) disX * videoSensitivity / (float) screenWidth);
					} else {
						downVideoDuration = (int) ((float) currentVideoDuration - (float) disX * videoSensitivity / (float) videoViewWidth);
					}
					videoPlayer.videoView.seekTo(downVideoDuration < 0 ? 0 : downVideoDuration);
					videoPlayer.seekBar.setProgress(downVideoDuration < 0 ? 0 : downVideoDuration);
					return true;
				}
			}
			// change volume
			else {
				if((e1Y - e2Y) > 0) {
					int upVolume = 0;
					if(isFullScreen) {
						upVolume = (int) ((float) disY * (float) maxVolume / (float) screenHeight * volumeSensitivity + (float) startVolume);
					} else {
						upVolume = (int) ((float) disY * (float) maxVolume / (float) videoViewHeight * volumeSensitivity + (float) startVolume);
					}
					upVolume = upVolume > startVolume ? upVolume : upVolume + 1;
					videoPlayer.updateVolume(upVolume > maxVolume ? maxVolume : upVolume);
					videoPlayer.volumeSeekBar.setProgress(upVolume > maxVolume ? maxVolume : upVolume);
					return true;
				} else {
					int downVolume = 0;
					if(isFullScreen) {
						downVolume = (int) ((float) startVolume - (float) disY * (float) maxVolume / (float) screenHeight * volumeSensitivity);

					} else {
						downVolume = (int) ((float) startVolume - (float) disY * (float) maxVolume / (float) videoViewHeight * volumeSensitivity);
					}
					downVolume = downVolume < startVolume ? downVolume : downVolume - 1;
					videoPlayer.updateVolume(downVolume < 0 ? 0 : downVolume);
					videoPlayer.volumeSeekBar.setProgress(downVolume < 0 ? 0 : downVolume);
					return true;
				}
			}
		}
	}

	private class ResolutionItemClickListener implements OnClickListener {
		private int index;

		public ResolutionItemClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			if(resolution != index) {
				resolution = index;
				playedTime = videoView.getCurrentPosition();
				setResolutionType(uriList.get(position).getFormatTypeList().get(index));
				startLoading();
				playOnlineFile();
			}
			if(resolutionLayout.getVisibility() == View.VISIBLE) {
				resolutionLayout.setVisibility(View.GONE);
			} else {
				resolutionLayout.setVisibility(View.VISIBLE);
			}
		}

	}

}
