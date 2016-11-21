package com.appsdk.advancedvideoview;

import java.util.ArrayList;

import org.videolan.libvlc.EventHandler;

import com.appsdk.video.AdvancedVideoView;
import com.appsdk.video.listener.AdvancedVideoViewListener;
import com.appsdk.video.listener.FullscreenAdvertiseListener;
import com.appsdk.video.obj.MediaObj;
import com.appsdk.video.obj.ResolutionList;
import com.appsdk.video.obj.ResolutionObj;
import com.third.library.R;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

public class TestActivity extends Activity {

	private AdvancedVideoView mVideoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		mVideoView = (AdvancedVideoView) findViewById(R.id.advanced_videoview);
		try {
			mVideoView.init();

			mVideoView.showFullscreenAdvertiseView("http://i3.hoopchina.com.cn/blogfile/201408/19/BbsImg14083953203178_635*299.jpg", "www.taobao.com", false, 5, new FullscreenAdvertiseListener() {
				@Override
				public void onTimeEnd() {
					try {
						mVideoView.hideFullscreenAdvertiseView();
						mVideoView.showLoadingView(true);
						MediaObj mediaObj = new MediaObj("广告", "http://113.142.30.163:5080/hls/7qryepnw.m3u8", true);
						mVideoView.addMedia(mediaObj, true);
					} catch (Exception e) {
					}
				}

				@Override
				public void onClose() {
					Toast.makeText(TestActivity.this, "close ad", Toast.LENGTH_SHORT).show();
				}
			});

			// mVideoView.setRate(3.0f);

			// ArrayList<ResolutionObj> resolutionObjs = new
			// ArrayList<ResolutionObj>();
			// resolutionObjs.add(new ResolutionObj("标清",
			// "http://transfer.hzrtv.cn:8060/live/ac39790593344822b289c7af8c25bc9e?fmt=h264_800k_flv"));
			// resolutionObjs.add(new ResolutionObj("高清",
			// "http://live.kunmjy.sobeycache.com/live/ca9887f836b74130ab71185e4f190187.m3u8?fmt=h264_500k_ts&m3u8"));
			// MediaObj mediaObj = new MediaObj("视频", new
			// ResolutionList(resolutionObjs, 0), true);
			// mVideoView.addMedia(mediaObj, false, false);
			// mVideoView.autoHideControlerEnable(false);
			// mVideoView.toggleFullScreenEnable(false);

			// new Handler().postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// mVideoView.setScaleType(AdvancedVideoViewScaleType.SURFACE_16_9);
			// }
			// }, 4000);

			mVideoView.setOnAdvancedVideoViewListener(new AdvancedVideoViewListener() {
				@Override
				public void onEvent(AdvancedVideoView videoView, int env) {
					switch (env) {
					case EventHandler.MediaPlayerEndReached:
						try {
							ArrayList<ResolutionObj> resolutionObjs = new ArrayList<ResolutionObj>();
							resolutionObjs.add(new ResolutionObj("标清", "http://transfer.hzrtv.cn:8060/live/ac39790593344822b289c7af8c25bc9e?fmt=h264_800k_flv"));
							resolutionObjs.add(new ResolutionObj("高清", "http://live.kunmjy.sobeycache.com/live/ca9887f836b74130ab71185e4f190187.m3u8?fmt=h264_500k_ts&m3u8"));
							MediaObj mediaObj = new MediaObj("视频", new ResolutionList(resolutionObjs, 0), true);
							mVideoView.addMedia(mediaObj, false, true);
						} catch (Exception e) {
						}
						break;
					case EventHandler.MediaPlayerPaused:
						mVideoView.showPauseAdvertiseView("http://i3.hoopchina.com.cn/blogfile/201408/19/BbsImg14083953203178_635*299.jpg", "www.taobao.com", true);
						break;
					case EventHandler.MediaPlayerEncounteredError:
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								try{
									mVideoView.hideErrorView();
									mVideoView.showLoadingView(true);
									MediaObj mediaObj = new MediaObj("广告", "http://182.118.12.74/64/26/76/letv-gug/17/ver_00_14-21565548-avc-577495-aac-64283-14960-1224396-7fbc351d36e43561b369873cd0d9c910-1401876313418.letv?crypt=50aa7f2e272&b=1314&nlh=3072&nlt=45&bf=17&p2p=1&video_type=mp4&termid=2&tss=no&geo=CN-23-323-2&tm=1434091200&key=57f607aff97d6715a799a97b60f4aea3&platid=100&splatid=10000&proxy=1026955808,2007471021&tag=gug&gugtype=1&type=ad_m_gaoqing_mp4&playid=0&pay=0&hwtype=un&ostype=android&gn=703&buss=1&qos=5&cips=119.6.200.62", true);
									mVideoView.addMedia(mediaObj, true);
								} catch(Exception e){
								}
							}
						}, 5000);
						break;
					default:
						break;
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		if (mVideoView != null) {
			mVideoView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (mVideoView != null) {
			mVideoView.onDestory();
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		if (mVideoView != null) {
			mVideoView.onResume(false);
		}
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mVideoView != null) {
			mVideoView.onConfigurationChanged();
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mVideoView != null && mVideoView.isFullScreen() && keyCode == KeyEvent.KEYCODE_BACK) {
			mVideoView.toggleFullScreen();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
