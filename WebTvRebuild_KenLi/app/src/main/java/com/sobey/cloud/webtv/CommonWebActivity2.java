package com.sobey.cloud.webtv;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.PluginEntry;
import org.apache.cordova.Whitelist;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.SharePopWindow;
import com.sobey.cloud.webtv.views.user.LoginActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 通用网页浏览
 * 
 * @author lazy
 *
 */
@SuppressLint("NewApi")
public class CommonWebActivity2 extends BaseActivity implements CordovaInterface {

	/**
	 * WebView
	 */
	@GinInjectView(id = R.id.common_wv)
	private SystemWebView webView;
	private WebSettings webSettings;

	/**
	 * 标题
	 */
	@GinInjectView(id = R.id.title_tv)
	private TextView mTitleTv;

	/**
	 * 新闻信息
	 */
	private JSONObject information;

	/**
	 * 用户名
	 */
	private String mUserName;

	/**
	 * 分享
	 */
	private SharePopWindow mSharePopWindow;

	private String mShareImage = null;
	private String mShareUrl = null;
	private String mShareTitle = null;
	private String mShareContent = null;

	private final ExecutorService threadPool = Executors.newCachedThreadPool();

	// Plugin to call when activity result is received
	protected int activityResultRequestCode;
	protected CordovaPlugin activityResultCallback;

	protected CordovaPreferences prefs = new CordovaPreferences();
	protected Whitelist internalWhitelist = new Whitelist();
	protected Whitelist externalWhitelist = new Whitelist();
	protected ArrayList<PluginEntry> pluginEntries;



	public PowerManager pm;
	private PowerManager.WakeLock wakeLock;

	@Override
	public int getContentView() {
		return R.layout.activity_common_web;
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

		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mUserName = "";
		} else {
			mUserName = userInfo.getString("id", "");
		}

		// initView();

		internalWhitelist.addWhiteListEntry("*", false);
		externalWhitelist.addWhiteListEntry("tel:*", false);
		externalWhitelist.addWhiteListEntry("sms:*", false);
		prefs.set("loglevel", "DEBUG");

		// webView.init(this, makeWebViewClient(webView),
		// makeChromeClient(webView), pluginEntries, internalWhitelist,
		// externalWhitelist, prefs);

		// webView.init(this, pluginEntries, prefs);
		ConfigXmlParser parser = new ConfigXmlParser();
		parser.parse(this);
		final CordovaWebView cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(webView));
		cordovaWebView.init(this, parser.getPluginEntries(), parser.getPreferences());

		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			String url = getIntent().getStringExtra("url");
			String title = getIntent().getStringExtra("title");

			if (null != title) {
				mTitleTv.setText(title);
			}
			mTitleTv.setText("");

			String str = getIntent().getStringExtra("information");
			if (null != str) {
				try {
					JSONObject obj = new JSONObject(str);
					News.getArticleById(String.valueOf(obj.getInt("id")), obj.optString("parentid"), mUserName,
							MConfig.TerminalType, null, this, new OnJsonArrayResultListener() {
								@Override
								public void onOK(JSONArray result) {
									try {
										information = result.getJSONObject(0);
										String linkUrl = information.getString("url");
										cordovaWebView.loadUrl(linkUrl);
									} catch (Exception e) {
									}
								}

								@Override
								public void onNG(String reason) {

								}

								@Override
								public void onCancel() {

								}
							});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (url != null) {
				cordovaWebView.loadUrl(url);
			}

		}

		View mActivityLayoutView = (RelativeLayout) findViewById(R.id.activity_common_web_layout);
		mSharePopWindow = new SharePopWindow(this, mActivityLayoutView);

	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.back_rl:
			finishActivity();
			break;
		// 分享
		case R.id.mNewsdetailMore:
			share();
			break;

		// 评论
		case R.id.comment_tv:
			comment();
			break;
		}
	}

	@Override
	public void onPause() {
		try {
			webView.getClass().getMethod("onPause").invoke(webView, (Object[]) null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException e) {
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		try {
			webView.getClass().getMethod("onResume").invoke(webView, (Object[]) null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException e) {
		}
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		webView.destroy();
		super.onDestroy();


		if (null != wakeLock) {
			wakeLock.release();
		}
	}

	// protected CordovaWebViewClient makeWebViewClient(CordovaWebView webView)
	// {
	// return webView.makeWebViewClient(this);
	// }
	//
	// protected CordovaChromeClient makeChromeClient(CordovaWebView webView) {
	// return webView.makeWebChromeClient(this);
	// }

	// /**
	// * 初始化webview相应的参数
	// */
	// public void initView() {
	// // 获取webview的相关配置
	// webSettings = webView.getSettings();
	// // webview的缓存模式
	// webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
	// // 自动加载图片
	// webSettings.setLoadsImagesAutomatically(true);
	// webSettings.setBuiltInZoomControls(true);
	// // 自动缩放
	// webSettings.setSupportZoom(true);
	// // 设置webview的插件转状态
	// webSettings.setPluginState(WebSettings.PluginState.ON);
	// // 允许与js交互
	// webSettings.setJavaScriptEnabled(true);
	// // 设置默认的字符编码
	// webSettings.setDefaultTextEncodingName("utf-8");
	// // webveiw加载网页的方式
	// // webView.loadDataWithBaseURL();
	// // webView.loadData();
	// webView.loadUrl("http://webtv.sobeycloud.com/cydst/xcLive/27582776.shtml?type=1");
	//
	// // 为了防止和过滤掉一些其他的网页地址我们可以重写shouldOverrideUrlLoading
	// // 来覆盖掉之前的url加载路径
	// webView.setWebViewClient(new WebViewClient() {
	// @Override
	// public boolean shouldOverrideUrlLoading(WebView view, String url) {
	// view.loadUrl(url);
	// return true;
	// }
	//
	// @Override
	// public void onPageFinished(WebView view, String url) {
	// super.onPageFinished(view, url);
	// }
	//
	// /** 你可以在出话之前加载一些资源 */
	// @Override
	// public void onLoadResource(WebView view, String url) {
	//
	// }
	// });
	//
	// webView.setWebChromeClient(new WebChromeClient() {
	// @Override
	// public void onShowCustomView(View view, CustomViewCallback callback) {
	// super.onShowCustomView(view, callback);
	// }
	//
	// @Override
	// public void onHideCustomView() {
	// super.onHideCustomView();
	// }
	// });
	//
	// }

	/**
	 * 分享
	 */
	private void share() {
		try {
			mShareUrl = information.getString("url") + "?type=1";
			mShareTitle = information.getString("title");
			mShareContent = information.getString("summary").trim();
			mShareImage = information.getString("logo");
			mSharePopWindow.showShareWindow(mShareUrl, mShareTitle, mShareContent, mShareImage);
		} catch (Exception e) {
		}
	}

	/**
	 * 评论
	 */
	private void comment() {
		if (information != null) {
			SharedPreferences userInfo = getSharedPreferences("user_info", 0);
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(this, LoginActivity.class));
				return;
			}
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra("information", information.toString());
			startActivity(intent);
		}
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public ExecutorService getThreadPool() {
		return threadPool;
	}

	@Override
	public Object onMessage(String id, Object data) {
		if ("exit".equals(id)) {
			super.finish();
		}
		return null;
	}

	@Override
	public void setActivityResultCallback(CordovaPlugin plugin) {
		if (activityResultCallback != null) {
			activityResultCallback.onActivityResult(activityResultRequestCode, Activity.RESULT_CANCELED, null);
		}
		this.activityResultCallback = plugin;
	}

	@Override
	public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {
		setActivityResultCallback(command);
		try {
			startActivityForResult(intent, requestCode);
		} catch (RuntimeException e) {
			activityResultCallback = null;
			throw e;
		}
	}

	@Override
	public void requestPermission(CordovaPlugin plugin, int requestCode, String permission) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestPermissions(CordovaPlugin plugin, int requestCode, String[] permissions) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasPermission(String permission) {
		// TODO Auto-generated method stub
		return true;
	}

}
