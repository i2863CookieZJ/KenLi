package com.sobey.cloud.webtv;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.utils.CheckNetwork;
import com.dylan.common.utils.DateParse;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.CommonMethod;
import com.sobey.cloud.webtv.utils.LoadingPopWindow;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.MorePopWindow;
import com.sobey.cloud.webtv.utils.MorePopWindow.CollectionClickListener;
import com.sobey.cloud.webtv.utils.MorePopWindow.FontSizeChangeListener;
import com.sobey.cloud.webtv.utils.SharePopWindow;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.widgets.webview.WebViewController;
import com.sobey.cloud.webtv.widgets.webview.WebViewController.ShowImageClickListener;
import com.sobey.cloud.webtv.kenli.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class GeneralNewsDetailActivity extends BaseActivity {

	private String mShareImage = null;
	private String mShareUrl = null;
	private String mShareTitle = null;
	private String mShareContent = null;

	private JSONObject mInformation;
	private int mFontSize = MConfig.FontSizeDefault;
	private boolean isShowPicture = true;
	@SuppressWarnings("unused")
	private boolean isNightMode = false;
	private JSONObject information;
	private JSONArray staticFilePaths;
	private WebViewController webViewController;
	private SharePopWindow mSharePopWindow;
	private MorePopWindow mMorePopWindow;
	private String mTitle;
	private String mUserName;
	private boolean mFirstFlag = true;
	private String mVoteId;
	private String mVoteTitle;

	private LoadingPopWindow loadingPopWindow;
	@GinInjectView(id = R.id.mNewsdetailoadingfail)
	private View emptyLayout;
	@GinInjectView(id = R.id.mNewsdetailFooter)
	LinearLayout mNewsdetailFooter;
	@GinInjectView(id = R.id.mNewsdetailDivider)
	LinearLayout mNewsdetailDivider;
	@GinInjectView(id = R.id.mNewsdetailContent)
	ScrollView mNewsdetailContent;
	@GinInjectView(id = R.id.mNewsdetailContentTitle)
	TextView mNewsdetailContentTitle;
	@GinInjectView(id = R.id.mNewsdetailContentDate)
	TextView mNewsdetailContentDate;
	@GinInjectView(id = R.id.mNewsdetailContentRefername)
	TextView mNewsdetailContentRefername;
	@GinInjectView(id = R.id.mNewsdetailWebview)
	WebView mNewsdetailWebview;
	@GinInjectView(id = R.id.mNewsdetailContentVideoScreenshotLayout)
	RelativeLayout mNewsdetailContentVideoScreenshotLayout;
	@GinInjectView(id = R.id.mNewsdetailContentVideoScreenshot)
	AdvancedImageView mNewsdetailContentVideoScreenshot;
	@GinInjectView(id = R.id.mNewsdetailContentVideoScreenshotPlay)
	ImageView mNewsdetailContentVideoScreenshotPlay;
	// Footer
	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mNewsdetailBack;
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
	@GinInjectView(id = R.id.mNewsdetailVoteLayout)
	LinearLayout mNewsdetailVoteLayout;
	@GinInjectView(id = R.id.mNewsdetailVoteTitle)
	TextView mNewsdetailVoteTitle;
	@GinInjectView(id = R.id.mNewsdetailVoters)
	TextView mNewsdetailVoters;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_generalnews_detail;
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
			loadingPopWindow = new LoadingPopWindow(GeneralNewsDetailActivity.this, mNewsdetailDivider, height,
					statusBarHeight, 0xffffffff);
			if (CommonMethod.checkNetwork(this)) {
				loadingPopWindow.showLoadingWindow();
			}

		}
		if (hasFocus) {
			if (mFirstFlag) {
				mFirstFlag = false;
			} else {
				refreshCollection();
			}
			SharedPreferences userInfo = GeneralNewsDetailActivity.this.getSharedPreferences("user_info", 0);
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
		SharedPreferences userInfo = GeneralNewsDetailActivity.this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mUserName = "";
		} else {
			mUserName = userInfo.getString("id", "");
		}

		initFooter();
		if (!CommonMethod.checkNetwork(this)) {
			emptyLayout.setVisibility(View.VISIBLE);
			if (loadingPopWindow != null) {
				loadingPopWindow.hideLoadingWindow();
			}
			return;
		}
		String str = getIntent().getStringExtra("information");
		try {
			mInformation = new JSONObject(str);
			News.getArticleById(String.valueOf(mInformation.getInt("id")), mInformation.optString("parentid"),
					mUserName, null, null, this, new OnJsonArrayResultListener() {
						@Override
						public void onOK(JSONArray result) {
							try {
								information = result.getJSONObject(0);
								getCollection();
								try {
									News.increaseHitCount(null, information.getString("catalogid"),
											information.getString("id"));
									getVote(information.optString("id"));
								} catch (Exception e) {
								}
							} catch (JSONException e) {
								information = null;
							}
							loadContent();
						}

						@Override
						public void onNG(String reason) {
							Toast.makeText(GeneralNewsDetailActivity.this, "NG", Toast.LENGTH_SHORT).show();
							loadContent();
						}

						@Override
						public void onCancel() {
							Toast.makeText(GeneralNewsDetailActivity.this, "cancel", Toast.LENGTH_SHORT).show();
							loadContent();
						}
					});
		} catch (Exception e) {
			Toast.makeText(GeneralNewsDetailActivity.this, "exception", Toast.LENGTH_SHORT).show();
			loadContent();
		}
	}

	@SuppressWarnings("deprecation")
	private void initWebView() {
		mNewsdetailWebview.getSettings().setJavaScriptEnabled(true);
		mNewsdetailWebview.getSettings().setDefaultTextEncodingName("gbk");
		mNewsdetailWebview.getSettings().setRenderPriority(RenderPriority.HIGH);
		mNewsdetailWebview.getSettings().setBlockNetworkImage(true);
		mNewsdetailWebview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mNewsdetailWebview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}
		});
	}

	private void loadContent() {
		initWebView();
		try {
			if (information != null) {
				mNewsdetailMessagenum.setText(information.getString("commcount"));
				mShareUrl = information.getString("url");
				mShareTitle = information.getString("title");
				mShareContent = information.getString("summary").trim();
				mShareImage = information.getString("logo");
				mTitle = information.getString("title");
				staticFilePaths = information.getJSONArray("staticfilepaths");
				if (staticFilePaths.length() > 0) {
					mNewsdetailContentVideoScreenshotLayout.setVisibility(View.VISIBLE);
					mNewsdetailContentVideoScreenshot.setNetImage(information.getString("logo"));
					mNewsdetailContentVideoScreenshotPlay.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							CheckNetwork checkNetwork = new CheckNetwork(GeneralNewsDetailActivity.this);
							if (checkNetwork.check3GOnly(false, null) == CheckNetwork.MOBILE_ONLY) {
								AlertDialog.Builder builder = new AlertDialog.Builder(GeneralNewsDetailActivity.this);
								builder.setTitle("您现在使用的是3G网络，将耗费流量").setMessage("是否继续观看视频?");
								builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
										Intent intent = new Intent(GeneralNewsDetailActivity.this,
												VideoNewsPlayerActivity.class);
										intent.putExtra("staticfilepaths", staticFilePaths.toString());
										GeneralNewsDetailActivity.this.startActivity(intent);
									}
								}).setNegativeButton("否", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								}).show();
							} else {
								Intent intent = new Intent(GeneralNewsDetailActivity.this,
										VideoNewsPlayerActivity.class);
								intent.putExtra("staticfilepaths", staticFilePaths.toString());
								GeneralNewsDetailActivity.this.startActivity(intent);
							}
						}
					});
				}
				Log.d("zxd", "get content  and set title");
				mNewsdetailContentTitle.setText(mTitle);
				mNewsdetailContentDate.setText(
						DateParse.getDate(0, 0, 0, 0, information.getString("publishdate"), null, "yyyy-MM-dd HH:mm"));
				mNewsdetailContentRefername.setText(information.getString("refername"));
				String content = information.getString("content").replaceAll("font-size:", "").replaceAll("font:", "");
				content = "<html><head></head><body style='font-size:" + mFontSize + "px;'>" + content;
				content = content + "</body></html>";
				webViewController = new WebViewController(this, mNewsdetailWebview, content, isShowPicture);
				webViewController.setShowImageClickListener(new ShowImageClickListener() {
					@Override
					public void onClick(JSONArray imageUrlArray) {
						Intent intent = new Intent(GeneralNewsDetailActivity.this,
								GeneralNewsDetailShowImageActivity.class);
						intent.putExtra("information", information.toString());
						intent.putExtra("image_url", imageUrlArray.toString());
						GeneralNewsDetailActivity.this.startActivity(intent);
					}
				});
				webViewController.start();
				Log.d("zxd", "post to parse");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (loadingPopWindow != null) {
							try {
								loadingPopWindow.hideLoadingWindow();
							} catch (Exception e) {
							}
						}
					}
				}, 500);
			} else {
				mNewsdetailWebview.loadData("暂时无法获取新闻详情", "text/html; charset=UTF-8", null);
			}
		} catch (Exception e) {
			mNewsdetailWebview.loadData("暂时无法获取新闻详情", "text/html; charset=UTF-8", null);
		}
	}

	private void initFooter() {
		View mActivityLayoutView = (RelativeLayout) findViewById(R.id.activity_generalnews_detail_layout);
		mSharePopWindow = new SharePopWindow(this, mActivityLayoutView);
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
					if (information == null || TextUtils.isEmpty(information.getString("id"))) {
						Toast.makeText(GeneralNewsDetailActivity.this, "暂时无法获取新闻详情,请稍后收藏", Toast.LENGTH_SHORT).show();
						return;
					}
					String articleId = information.getString("id");
					if (TextUtils.isEmpty(mUserName)) {
						Toast.makeText(GeneralNewsDetailActivity.this, "请先登录您的账号", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(GeneralNewsDetailActivity.this, LoginActivity.class));
						return;
					}
					if (!addFlag) {
						Toast.makeText(GeneralNewsDetailActivity.this, "正在收藏...", Toast.LENGTH_SHORT).show();
						News.addCollect(mUserName, articleId, GeneralNewsDetailActivity.this,
								new OnJsonArrayResultListener() {
									@Override
									public void onOK(JSONArray result) {
										try {
											if (result.getJSONObject(0).getString("returncode")
													.equalsIgnoreCase("SUCCESS")) {
												mMorePopWindow.setCollection(true);
												Toast.makeText(GeneralNewsDetailActivity.this, "收藏成功",
														Toast.LENGTH_SHORT).show();
												refreshCollection();
											} else {
												Toast.makeText(GeneralNewsDetailActivity.this,
														result.getJSONObject(0).getString("returnmsg"),
														Toast.LENGTH_SHORT).show();
											}
										} catch (Exception e) {
											Toast.makeText(GeneralNewsDetailActivity.this, "操作失败，请稍后重试",
													Toast.LENGTH_SHORT).show();
										}
									}

									@Override
									public void onNG(String reason) {
										Toast.makeText(GeneralNewsDetailActivity.this, "网络不给力，请稍后重试",
												Toast.LENGTH_SHORT).show();
									}

									@Override
									public void onCancel() {
										Toast.makeText(GeneralNewsDetailActivity.this, "网络不给力，请稍后重试",
												Toast.LENGTH_SHORT).show();
									}
								});
					} else {
						Toast.makeText(GeneralNewsDetailActivity.this, "正在取消收藏...", Toast.LENGTH_SHORT).show();
						News.deleteCollect(mUserName, articleId, GeneralNewsDetailActivity.this,
								new OnJsonArrayResultListener() {
									@Override
									public void onOK(JSONArray result) {
										try {
											if (result.getJSONObject(0).getString("returncode")
													.equalsIgnoreCase("SUCCESS")) {
												mMorePopWindow.setCollection(false);
												Toast.makeText(GeneralNewsDetailActivity.this, "取消成功",
														Toast.LENGTH_SHORT).show();
												refreshCollection();
											} else {
												Toast.makeText(GeneralNewsDetailActivity.this,
														result.getJSONObject(0).getString("returnmsg"),
														Toast.LENGTH_SHORT).show();
											}
										} catch (Exception e) {
											Toast.makeText(GeneralNewsDetailActivity.this, "操作失败，请稍后重试",
													Toast.LENGTH_SHORT).show();
										}
									}

									@Override
									public void onNG(String reason) {
										Toast.makeText(GeneralNewsDetailActivity.this, "网络不给力，请稍后重试",
												Toast.LENGTH_SHORT).show();
									}

									@Override
									public void onCancel() {
										Toast.makeText(GeneralNewsDetailActivity.this, "网络不给力，请稍后重试",
												Toast.LENGTH_SHORT).show();
									}
								});
					}
				} catch (Exception e) {
					Toast.makeText(GeneralNewsDetailActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mNewsdetailBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});
		mNewsdetailLeavemessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (information != null) {
					SharedPreferences userInfo = GeneralNewsDetailActivity.this.getSharedPreferences("user_info", 0);
					if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
						GeneralNewsDetailActivity.this
								.startActivity(new Intent(GeneralNewsDetailActivity.this, LoginActivity.class));
						return;
					}
					Intent intent = new Intent(GeneralNewsDetailActivity.this, CommentActivity.class);
					intent.putExtra("information", information.toString());
					GeneralNewsDetailActivity.this.startActivity(intent);
				}
			}
		});
		mNewsdetailMessagenum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (information != null) {
					Intent intent = new Intent(GeneralNewsDetailActivity.this, ReviewActivity.class);
					intent.putExtra("information", information.toString());
					GeneralNewsDetailActivity.this.startActivity(intent);
				}
			}
		});
		mNewsdetailMessageIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (information != null) {
					Intent intent = new Intent(GeneralNewsDetailActivity.this, ReviewActivity.class);
					intent.putExtra("information", information.toString());
					startActivity(intent);
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
			News.getArticleById(String.valueOf(mInformation.getInt("id")), mInformation.optString("parentid"),
					mUserName, null, null, this, new OnJsonArrayResultListener() {
						@Override
						public void onOK(JSONArray result) {
							try {
								information = result.getJSONObject(0);
								getCollection();
							} catch (JSONException e) {
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

	private void getVote(String articleId) {
		if (TextUtils.isEmpty(articleId)) {
			return;
		}
		News.getArticleRelaVote(articleId, this, new OnJsonObjectResultListener() {
			@Override
			public void onOK(JSONObject result) {
				try {
					showVote(result.optString("ID"), result.optString("Total"), result.optString("Title"));
				} catch (Exception e) {
					useCatalogVote();
				}
			}

			@Override
			public void onNG(String reason) {
				useCatalogVote();
			}

			@Override
			public void onCancel() {
				useCatalogVote();
			}
		});
	}

	private void showVote(String id, String voters, String title) {
		if (TextUtils.isEmpty(id) || TextUtils.isEmpty(title)) {
			return;
		}
		if (TextUtils.isEmpty(voters)) {
			voters = "0";
		}
		mVoteId = id;
		mVoteTitle = title;
		mNewsdetailVoteTitle.setText(title);
		mNewsdetailVoters.setText(voters + " 人参加");
		mNewsdetailVoteLayout.setVisibility(View.VISIBLE);
		mNewsdetailVoteLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(GeneralNewsDetailActivity.this, VoteDetailActivity.class);
				intent.putExtra("vote_id", mVoteId);
				intent.putExtra("vote_title", mVoteTitle);
				GeneralNewsDetailActivity.this.startActivity(intent);
			}
		});
	}

	private void useCatalogVote() {
		try {
			String str = getIntent().getStringExtra("vote");
			JSONObject result = new JSONObject(str);
			showVote(result.optString("ID"), result.optString("Total"), result.optString("Title"));
		} catch (Exception e) {
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
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSharePopWindow.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 显示分享
	 */
	public void showSharePopWindow() {
		this.mSharePopWindow.showShareWindow(this.mShareUrl, this.mShareTitle, this.mShareContent, this.mShareImage);
	}
}
