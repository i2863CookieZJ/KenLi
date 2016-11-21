package com.sobey.cloud.webtv.broke;

import java.util.ArrayList;

import org.apache.cordova.engine.SystemWebView;
import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.appsdk.video.AdvancedVideoView;
import com.appsdk.video.obj.MediaObj;
import com.appsdk.video.obj.ResolutionList;
import com.appsdk.video.obj.ResolutionObj;
import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.broke.util.BrokeStatus;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.LoadingPopWindow;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.MorePopWindow;
import com.sobey.cloud.webtv.utils.MorePopWindow.CollectionClickListener;
import com.sobey.cloud.webtv.utils.MorePopWindow.FontSizeChangeListener;
import com.sobey.cloud.webtv.utils.SharePopWindow;
import com.sobey.cloud.webtv.utils.SharePopWindow.SharePopWindowClickListener;
import com.sobey.cloud.webtv.utils.VideoAdManager;
import com.sobey.cloud.webtv.widgets.webview.WebViewController;
import com.sobey.cloud.webtv.widgets.webview.WebViewController.ShowImageClickListener;
import com.sobey.cloud.webtv.kenli.R;
import com.umeng.socialize.bean.SHARE_MEDIA;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BrokeNewsDetailActivity extends BaseActivity {

	private String mShareImage = null;
	private String mShareUrl = null;
	private String mShareTitle = null;
	private String mShareContent = null;

	private int mFontSize = MConfig.FontSizeDefault;
	private boolean isShowPicture = true;
	@SuppressWarnings("unused")
	private boolean isNightMode = false;
	private String mUserName;
	private String mRevelationId;
	private ArrayList<ResolutionObj> resolutionObjs = new ArrayList<ResolutionObj>();
	private MediaObj mediaObj;
	private VideoAdManager mVideoAdManager = new VideoAdManager();
	private boolean isShowVideoPlayer = true;
	private boolean isInitVideoPlayer = false;
	private boolean mFirstFlag = true;
	private JSONObject information;
	private SharePopWindow mSharePopWindow;
	private MorePopWindow mMorePopWindow;
	private WebViewController webViewController;

	private LoadingPopWindow loadingPopWindow;

	@GinInjectView(id = R.id.mNewsdetailVideoView)
	AdvancedVideoView mNewsdetailVideoView;
	@GinInjectView(id = R.id.mNewsdetailFooter)
	LinearLayout mNewsdetailFooter;
	@GinInjectView(id = R.id.mNewsdetailDivider)
	LinearLayout mNewsdetailDivider;
	@GinInjectView(id = R.id.mNewsdetailContentLayout)
	ScrollView mNewsdetailContentLayout;
	@GinInjectView(id = R.id.mNewsdetailImageLayout)
	RelativeLayout mNewsdetailImageLayout;
	// Detail
	@GinInjectView(id = R.id.mNewsdetailWebview)
	SystemWebView mNewsdetailWebview;
	@GinInjectView(id = R.id.mNewsdetailImage)
	AdvancedImageView mNewsdetailImage;
	@GinInjectView(id = R.id.mNewsdetailImageCount)
	TextView mNewsdetailImageCount;
	// Footer
	@GinInjectView(id = R.id.mNewsdetailBack)
	ImageButton mNewsdetailBack;
	@GinInjectView(id = R.id.mNewsdetailLeavemessage)
	TextView mNewsdetailLeavemessage;
	@GinInjectView(id = R.id.mNewsdetailMessagenum)
	TextView mNewsdetailMessagenum;
	@GinInjectView(id = R.id.mNewsdetailMessageIcon)
	ImageView mNewsdetailMessageIcon;
	@GinInjectView(id = R.id.mNewsdetailShare)
	ImageButton mNewsdetailShare;
	@GinInjectView(id = R.id.mNewsdetailMore)
	ImageButton mNewsdetailMore;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_broke_news_detail;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		setupNewsDetailActivity();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus && loadingPopWindow == null) {
			int height = mNewsdetailDivider.getTop();
			Rect frame = new Rect();
			getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top;
			loadingPopWindow = new LoadingPopWindow(BrokeNewsDetailActivity.this, mNewsdetailDivider, height,
					statusBarHeight, 0xffffffff);
			loadingPopWindow.showLoadingWindow();
		}
		if (hasFocus) {
			if (mFirstFlag) {
				mFirstFlag = false;
			} else {
				refreshCollection();
			}
			SharedPreferences userInfo = BrokeNewsDetailActivity.this.getSharedPreferences("user_info", 0);
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				mUserName = "";
			} else {
				mUserName = userInfo.getString("id", "");
			}
		}
	}

	public void setupNewsDetailActivity() {
		// Get preferrence
		SharedPreferences settings = getSharedPreferences("settings", 0);
		CheckNetwork network = new CheckNetwork(this);
		isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
				|| network.getWifiState(false) == State.CONNECTED;
		isNightMode = settings.getInt("night_mode", 0) == 1 ? true : false;
		mFontSize = settings.getInt("fontsize", MConfig.FontSizeDefault);
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mUserName = "";
		} else {
			mUserName = userInfo.getString("id", "");
		}

		initFooter();
		String str = getIntent().getStringExtra("information");
		try {
			mRevelationId = (new JSONObject(str)).optString("id");
			News.getBrokeArticleById(mRevelationId, BrokeStatus.PUBLISHED, this, new OnJsonArrayResultListener() {

				@Override
				public void onOK(JSONArray result) {
					try {
						information = result.optJSONObject(0);
						information.put("type", MConfig.BrokeTypeId);
						mNewsdetailMessagenum.setText(information.optString("commcount"));
						getCollection();
						loadContent();
						if (isShowVideoPlayer) {
							initVideo();
							CheckNetwork checkNetwork = new CheckNetwork(BrokeNewsDetailActivity.this);
							if (checkNetwork.check3GOnly(false, null) == CheckNetwork.MOBILE_ONLY) {
								AlertDialog.Builder builder = new AlertDialog.Builder(BrokeNewsDetailActivity.this);
								builder.setTitle("您现在使用的是3G网络，将耗费流量").setMessage("是否继续观看视频?");
								builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
										getOnlineVideoPath();
									}
								}).setNegativeButton("否", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								}).show();
							} else {
								getOnlineVideoPath();
							}
						} else {
							mNewsdetailVideoView.setVisibility(View.GONE);
						}
						News.increaseHitCount(null, information.getString("catalogid"), information.getString("id"));
					} catch (Exception e) {
					}
				}

				@Override
				public void onNG(String reason) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initFooter() {
		View mActivityLayoutView = (RelativeLayout) findViewById(R.id.activity_broke_news_detail_layout);
		mSharePopWindow = new SharePopWindow(this, mActivityLayoutView);
		mSharePopWindow.setOnSharePopWindowClickListener(new SharePopWindowClickListener() {
			@Override
			public void onClick(SHARE_MEDIA media) {
				if (isShowVideoPlayer && isInitVideoPlayer) {
					mNewsdetailVideoView.pause();
				}
			}
		});
		mMorePopWindow = new MorePopWindow(this, mActivityLayoutView, new FontSizeChangeListener() {
			@Override
			public void onChange(int progress) {
				if (webViewController != null) {
					webViewController.setFontSize(progress + MConfig.FontSizeMin);
				}
			}
		}, new CollectionClickListener() {
			@Override
			public void onClick(boolean addFlag) {
				try {
					if (information == null || TextUtils.isEmpty(mRevelationId)) {
						Toast.makeText(BrokeNewsDetailActivity.this, "暂时无法获取详情,请稍后收藏", Toast.LENGTH_SHORT).show();
						return;
					}
					if (TextUtils.isEmpty(mUserName)) {
						Toast.makeText(BrokeNewsDetailActivity.this, "请先登录您的账号", Toast.LENGTH_SHORT).show();
						startActivity(
								new Intent(BrokeNewsDetailActivity.this, com.sobey.cloud.webtv.views.user.LoginActivity.class));
						return;
					}
					if (!addFlag) {
						Toast.makeText(BrokeNewsDetailActivity.this, "正在收藏...", Toast.LENGTH_SHORT).show();
						News.addCollect(mUserName, mRevelationId, BrokeNewsDetailActivity.this,
								new OnJsonArrayResultListener() {
							@Override
							public void onOK(JSONArray result) {
								try {
									if (result.getJSONObject(0).getString("returncode").equalsIgnoreCase("SUCCESS")) {
										mMorePopWindow.setCollection(true);
										Toast.makeText(BrokeNewsDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
										refreshCollection();
									} else {
										Toast.makeText(BrokeNewsDetailActivity.this,
												result.getJSONObject(0).getString("returnmsg"), Toast.LENGTH_SHORT)
												.show();
									}
								} catch (Exception e) {
									Toast.makeText(BrokeNewsDetailActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT)
											.show();
								}
							}

							@Override
							public void onNG(String reason) {
								Toast.makeText(BrokeNewsDetailActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onCancel() {
								Toast.makeText(BrokeNewsDetailActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
							}
						});
					} else {
						Toast.makeText(BrokeNewsDetailActivity.this, "正在取消收藏...", Toast.LENGTH_SHORT).show();
						News.deleteCollect(mUserName, mRevelationId, BrokeNewsDetailActivity.this,
								new OnJsonArrayResultListener() {
							@Override
							public void onOK(JSONArray result) {
								try {
									if (result.getJSONObject(0).getString("returncode").equalsIgnoreCase("SUCCESS")) {
										mMorePopWindow.setCollection(false);
										Toast.makeText(BrokeNewsDetailActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
										refreshCollection();
									} else {
										Toast.makeText(BrokeNewsDetailActivity.this,
												result.getJSONObject(0).getString("returnmsg"), Toast.LENGTH_SHORT)
												.show();
									}
								} catch (Exception e) {
									Toast.makeText(BrokeNewsDetailActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT)
											.show();
								}
							}

							@Override
							public void onNG(String reason) {
								Toast.makeText(BrokeNewsDetailActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onCancel() {
								Toast.makeText(BrokeNewsDetailActivity.this, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
							}
						});
					}
				} catch (Exception e) {
					Toast.makeText(BrokeNewsDetailActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mNewsdetailBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BrokeNewsDetailActivity.this.finish();
			}
		});
		mNewsdetailLeavemessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (information != null) {
					Intent intent = new Intent(BrokeNewsDetailActivity.this,
							com.sobey.cloud.webtv.CommentActivity.class);
					intent.putExtra("information", information.toString());
					BrokeNewsDetailActivity.this.startActivity(intent);
				}
			}
		});
		mNewsdetailMessagenum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (information != null) {
					Intent intent = new Intent(BrokeNewsDetailActivity.this,
							com.sobey.cloud.webtv.ReviewActivity.class);
					intent.putExtra("information", information.toString());
					BrokeNewsDetailActivity.this.startActivity(intent);
				}
			}
		});
		mNewsdetailMessageIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (information != null) {
					Intent intent = new Intent(BrokeNewsDetailActivity.this,
							com.sobey.cloud.webtv.ReviewActivity.class);
					intent.putExtra("information", information.toString());
					BrokeNewsDetailActivity.this.startActivity(intent);
				}
			}
		});
		mNewsdetailShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharePopWindow.showShareWindow(mShareUrl, mShareTitle, mShareContent, mShareImage);
			}
		});
		mNewsdetailMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMorePopWindow.showMoreWindow();
			}
		});
	}

	// Detail
	private void loadContent() {
		initWebView();
		try {
			if (information != null) {
				// mShareUrl = information.getString("url");
				// mShareTitle = information.getString("title");
				// mShareContent = "(" + information.getString("title") + ")";
				// mShareMessageContent = "(" + information.getString("title") +
				// ")";
				// String summary = information.getString("summary").trim();
				// if (summary.length() > mConfig.ShareContentLength) {
				// mShareContent += summary.substring(0,
				// mConfig.ShareContentLength) + "...";
				// } else {
				// mShareContent += summary;
				// }
				// if (summary.length() > mConfig.ShareMessageContentLength) {
				// mShareMessageContent += summary.substring(0,
				// mConfig.ShareMessageContentLength) + "...";
				// } else {
				// mShareMessageContent += summary;
				// }
				// mShareContent += " " + information.getString("url");
				// mShareMessageContent += " " + information.getString("url");
				// mShareImage = information.getString("logo");
				final JSONArray contentArray = information.optJSONArray("content");
				int imageNum = 0;
				int videoNum = 0;
				String imageUrl = "";
				for (int i = 0; i < contentArray.length(); i++) {
					JSONObject contentObject = contentArray.optJSONObject(i);
					if (contentObject.optString("type").equalsIgnoreCase("image")) {
						if (TextUtils.isEmpty(imageUrl)) {
							imageUrl = contentObject.optString("imgpath");
						}
						imageNum++;
					} else if (contentObject.optString("type").equalsIgnoreCase("video")) {
						videoNum++;
					}
				}
				isShowVideoPlayer = videoNum > 0 ? true : false;
				if (imageNum > 0) {
					mNewsdetailImage.setNetImage(imageUrl);
					mNewsdetailImageCount.setText(imageNum + "张");
					mNewsdetailImageLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(BrokeNewsDetailActivity.this,
									BrokeNewsDetailImageShowActivity.class);
							intent.putExtra("information", contentArray.toString());
							BrokeNewsDetailActivity.this.startActivity(intent);
						}
					});
				} else {
					mNewsdetailImageLayout.setVisibility(View.GONE);
				}
				String content = information.optString("title");
				content = "<html><head></head><body style='font-size:" + mFontSize + "px;'>" + content;
				content = content + "</body></html>";
				webViewController = new WebViewController(this, mNewsdetailWebview, content, isShowPicture);
				webViewController.setShowImageClickListener(new ShowImageClickListener() {
					@Override
					public void onClick(JSONArray imageUrlArray) {
					}
				});
				webViewController.start();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (loadingPopWindow != null) {
							loadingPopWindow.hideLoadingWindow();
						}
					}
				}, 500);
			} else {
			}
		} catch (Exception e) {
		}
	}

	private void initWebView() {
		mNewsdetailWebview.getSettings().setDefaultTextEncodingName("gbk");
		mNewsdetailWebview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mNewsdetailWebview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}
		});
	}

	private void getCollection() {
		try {
			if (information != null && mMorePopWindow != null) {
				if (information.getString("iscollect").equalsIgnoreCase("1")) {
					mMorePopWindow.setCollection(true);
				} else {
					mMorePopWindow.setCollection(false);
				}
			}
		} catch (Exception e) {
		}
	}

	private void refreshCollection() {
		try {
			News.getArticleById(String.valueOf(information.getInt("id")), information.optString("parentid"), mUserName,
					null, null, this, new OnJsonArrayResultListener() {
						@Override
						public void onOK(JSONArray result) {
							try {
								information = result.getJSONObject(0);
								getCollection();
							} catch (Exception e) {
								information = null;
							}
						}

						@Override
						public void onNG(String reason) {
						}

						@Override
						public void onCancel() {
						}
					});
		} catch (Exception e) {
		}
	}

	// Video
	private void initVideo() {
		try {
			mNewsdetailVideoView.init();
			mNewsdetailVideoView.showLoadingView(true);
			isInitVideoPlayer = true;
		} catch (Exception e) {
		}
	}

	@Override
	public void onPause() {
		if (mNewsdetailVideoView != null) {
			mNewsdetailVideoView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (loadingPopWindow != null) {
			loadingPopWindow.hideLoadingWindow();
		}
		if (mNewsdetailVideoView != null) {
			mNewsdetailVideoView.onDestory();
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		if (mNewsdetailVideoView != null) {
			mNewsdetailVideoView.onResume(true);
		}
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mNewsdetailVideoView != null) {
			mNewsdetailVideoView.onConfigurationChanged();
		}
		super.onConfigurationChanged(newConfig);
	}

	private void getOnlineVideoPath() {
		JSONArray videoArray;
		try {
			if (information != null) {
				videoArray = information.getJSONArray("content");
				for (int i = 0; i < videoArray.length(); i++) {
					JSONObject contentObject = videoArray.optJSONObject(i);
					if (contentObject.optString("type").equalsIgnoreCase("video")) {
						News.getVideoPath(contentObject.optString("playerfilepath"), this,
								new OnJsonObjectResultListener() {
									@Override
									public void onOK(JSONObject result) {
										try {
											String str = result.getString("playerUrl");
											str = str.substring(str.indexOf("{"));
											JSONObject obj = new JSONObject(str);
											JSONArray formatUriArray = ((JSONObject) obj.getJSONArray("clips").get(0))
													.getJSONArray("urls");
											JSONArray formatArray = obj.getJSONArray("formats");
											String host = obj.getString("host");
											String p2p = obj.getString("p2p");
											for (int i = 0; i < formatUriArray.length(); i++) {
												resolutionObjs.add(new ResolutionObj(formatArray.get(i).toString(),
														(host + ((String) formatUriArray.get(i)) + p2p)));
											}
											mediaObj = new MediaObj(obj.optString("title"),
													new ResolutionList(resolutionObjs, 0), true);
											mVideoAdManager.setVideoAd(BrokeNewsDetailActivity.this,
													mNewsdetailVideoView, mediaObj, result.optInt("positionId", 0),
													result.optString("catalogId"));
										} catch (Exception e) {
											e.printStackTrace();
										}
									}

									@Override
									public void onNG(String reason) {
									}

									@Override
									public void onCancel() {
									}
								});
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mMorePopWindow.isShowing()) {
				mMorePopWindow.hideMoreWindow();
				return true;
			}
			if (mSharePopWindow.isShowing()) {
				mSharePopWindow.hideShareWindow();
				return true;
			}
			if (mNewsdetailVideoView != null && mNewsdetailVideoView.isFullScreen()
					&& keyCode == KeyEvent.KEYCODE_BACK) {
				mNewsdetailVideoView.toggleFullScreen();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSharePopWindow.onActivityResult(requestCode, resultCode, data);
	}
}
