package com.sobey.cloud.webtv;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.baidu.mobstat.StatService;
import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.fragment.WebPageFragment;
import com.sobey.cloud.webtv.model.provide.SplashModel;
import com.sobey.cloud.webtv.obj.AdObj;
import com.sobey.cloud.webtv.push.PushNotificationService;
import com.sobey.cloud.webtv.push.PushNotificationUtils;
import com.sobey.cloud.webtv.utils.AdManager;
import com.sobey.cloud.webtv.utils.CatalogControl;
import com.sobey.cloud.webtv.utils.MConfig;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public class SplashActivity extends BaseActivity<SplashModel> {

	@GinInjectView(id = R.id.mSplashImage)
	AdvancedImageView mSplashImage;

	@GinInjectView(id = R.id.mSplashText)
	TextView mSplashText;

	@Override
	public int getContentView() {
		return R.layout.activity_splash;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		tGet();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	private void tGet() {
		JPushInterface.init(getApplicationContext());
		try {

			StatService.setAppKey(MConfig.BaiduTongji);// 百度统计的服务
			StatService.setAppChannel(this, MConfig.ChannalName, true);// 设置App
																		// Channel（发布渠道）
			SharedPreferences adManager = getSharedPreferences("ad_manager", 0);
			Editor editor = adManager.edit();
			editor.putBoolean("banner_enable", true);
			editor.commit();
			CheckNetwork checkNetwork = new CheckNetwork(this);
			if (checkNetwork.getNetworkState(false)) {
				String str = getIntent().getStringExtra("information");
				JSONObject information = null;
				if (!TextUtils.isEmpty(str) && PushNotificationUtils.isAppRun(SplashActivity.this)) {
					information = new JSONObject(str);
					if (TextUtils.isEmpty(information.optString("type"))) {
						finishActivity();
					}
				}
				if (!TextUtils.isEmpty(str) && PushNotificationUtils.isAppRun(SplashActivity.this)
						&& !TextUtils.isEmpty(information.optString("type"))) {
					openPush(information);
				} else {
					News.getCatalogList(null, 1, this, new OnJsonArrayResultListener() {
						@Override
						public void onOK(JSONArray result) {
							try {
								if (!PushNotificationUtils.isServiceRun(SplashActivity.this)) {// 启动推送服务
									Intent intent = new Intent(SplashActivity.this, PushNotificationService.class);
									SplashActivity.this.startService(intent);
								}
								CatalogControl.setCatalogList(SplashActivity.this, result);
								SharedPreferences newGuide = SplashActivity.this.getSharedPreferences("newguide", 0);
								// if (newGuide == null ||
								// TextUtils.isEmpty(newGuide.getString("openFlag",
								// null)) || newGuide.getString("openFlag",
								// "").equalsIgnoreCase("true")) {
								// SplashActivity.this.startActivity(new
								// Intent(SplashActivity.this,
								// NewGuideActivity.class));
								// finishActivity();
								// } else {
								try {
									final AdObj adObj = AdManager.getScreenAdPicUrl(SplashActivity.this);
									if (adObj != null && !TextUtils.isEmpty(adObj.getPicUrl())
											&& !TextUtils.isEmpty(adObj.getPicPath())) {
										File picFile = new File(adObj.getPicPath());
										if (picFile.exists()) {
											final String adLinkUrl = adObj.getLinkUrl();
											new Handler().postDelayed(new Runnable() {

												@Override
												public void run() {
													showAdvImage(adLinkUrl, adObj);
												}
											}, 2000);
										} else {
											new Handler().postDelayed(new Runnable() {
												public void run() {
													AdManager.saveScreenAdPic(SplashActivity.this, adObj);
													openActivity();
												}
											}, 3000);

										}
									} else {
										new Handler().postDelayed(new Runnable() {
											public void run() {
												openActivity();
											}
										}, 3000);
									}
								} catch (Exception e) {
									new Handler().postDelayed(new Runnable() {
										public void run() {
											openActivity();
										}
									}, 3000);
								}
								// }
							} catch (Exception e) {
								errorFinish();
							}
						}

						@Override
						public void onNG(String reason) {
							errorFinish();
						}

						@Override
						public void onCancel() {
							errorFinish();
						}
					});

				}
			} else {
				noNetWork();
			}
		} catch (Exception e) {
			e.printStackTrace();
			finishActivity();
		}
	}

	/**
	 * 没有网络
	 */
	private void noNetWork() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("无可用网络").setMessage("现在去设置网络?");
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finishActivity();
			}
		});
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = null;
				try {
					int sdkVersion = android.os.Build.VERSION.SDK_INT;
					if (sdkVersion > 10) {
						intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
					} else {
						intent = new Intent();
						ComponentName comp = new ComponentName("com.android.settings",
								"com.android.settings.WirelessSettings");
						intent.setComponent(comp);
						intent.setAction("android.intent.action.VIEW");
					}
					startActivity(intent);
				} catch (Exception e) {
				}
				finishActivity();
			}
		}).setNegativeButton("否", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finishActivity();
			}
		}).show();
	}

	/**
	 * 显示广告
	 * 
	 * @param adLinkUrl
	 * @param adObj
	 */
	protected void showAdvImage(final String adLinkUrl, final AdObj adObj) {
		mSplashImage.setLocalImage(adObj.getPicPath());
		mSplashImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					if (!TextUtils.isEmpty(adLinkUrl)) {
						String url;
						if (adLinkUrl.indexOf("http://") < 0) {
							url = "http://" + adLinkUrl;
						} else {
							url = adLinkUrl;
						}
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						Uri content_url = Uri.parse(url);
						intent.setData(content_url);
						startActivity(intent);
					}
				} catch (Exception e) {
				}
			}
		});
		mSplashText.setVisibility(View.VISIBLE);
		mSplashText.setText("3");
		new Handler().postDelayed(new Runnable() {
			public void run() {
				mSplashText.setText("2");
			}
		}, 1000);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				mSplashText.setText("1");
			}
		}, 2000);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				openActivity();
			}
		}, 3000);
	}

	private void openActivity() {
		try {
			if (!PushNotificationUtils.isAppRun(SplashActivity.this)) {
				// Intent intent=new Intent(this, WebPageFragment.class);
				// startActivity(intent);
				CatalogControl.startFirstActivity(SplashActivity.this);
			}
			String str = getIntent().getStringExtra("information");
			if (!TextUtils.isEmpty(str)) {
				JSONObject information = new JSONObject(str);
				if (!TextUtils.isEmpty(information.optString("type"))) {
					openPush(information);
				}
			}
			finishActivity();
		} catch (Exception e) {
			errorFinish();
		}
	}

	private void openPush(JSONObject information) {
		try {
			Intent intent = new Intent(this, WebPageFragment.class);
			startActivity(intent);
			if (!TextUtils.isEmpty(information.optString("id")) && !TextUtils.isEmpty(information.optString("type"))) {
				if (information.opt("type").toString().equalsIgnoreCase("1")) {
					Intent intent1 = new Intent(SplashActivity.this, GeneralNewsDetailActivity.class);
					intent1.putExtra("information", information.toString());
					startActivity(intent1);
				} else if (information.opt("type").toString().equalsIgnoreCase("2")) {
					Intent intent1 = new Intent(SplashActivity.this, PhotoNewsDetailActivity.class);
					intent1.putExtra("information", information.toString());
					startActivity(intent1);
				} else if (information.opt("type").toString().equalsIgnoreCase("3")) {
					Intent intent1 = new Intent(SplashActivity.this, LiveNewsDetailActivity.class);
					intent1.putExtra("information", information.toString());
					startActivity(intent1);
				} else if (information.opt("type").toString().equalsIgnoreCase("5")) {
					Intent intent1 = new Intent(SplashActivity.this, VideoNewsDetailActivity.class);
					intent1.putExtra("information", information.toString());
					startActivity(intent1);
				}
			}
			finishActivity();
		} catch (Exception e) {
			e.printStackTrace();
			finishActivity();
		}
	}

	private void errorFinish() {
		Toast.makeText(this, "连接服务器失败，请稍后登陆", Toast.LENGTH_SHORT).show();
		finishActivity();
	}

	@Override
	public void onResume() {
		StatService.onResume(this);
		JPushInterface.onResume(this);
		super.onResume();

	}

	@Override
	public void onPause() {
		StatService.onPause(this);
		JPushInterface.onPause(this);
		super.onPause();
	}

}
